package implicit

import implicit.conversion.TypeConversion
import implicit.decorator.*
import implicit.exception.ImplicitException
import implicit.exception.ImplicitValidationException
import implicit.exception.ImplicitViolations
import net.bytebuddy.ByteBuddy
import net.bytebuddy.NamingStrategy
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.isDeclaredBy
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.function.Supplier

class Implicit(val namingStrategy: (TypeDescription) -> CharSequence) {

    companion object {
        private val intfTypeRegistry = ConcurrentHashMap<String, String>()
        private val supplierRegistry = ConcurrentHashMap<String, Supplier<*>>()
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> create(intf: Class<T>, interceptor: ImplicitInterceptor = ImplicitInterceptor()): Class<out T> {
        if (!intf.isInterface)
            throw IllegalArgumentException("argument must be an interface")

        if (intfTypeRegistry.containsKey(intf.name))
            return Class.forName(intfTypeRegistry.get(intf.name)) as Class<out T>

        val addField = AddFieldDecorator<T>(intf)::apply
        val addGetterSetter = AddGetterSetterDecorator<T>(intf)::apply
        val addConstructor = AddConstructorDecorator<T>(intf, interceptor)::apply
        val addAlias = AliasDecorator<T>(intf)::apply
        val addMixin = MixinDecorator<T>(intf)::apply
        val addEqualsHashCode = AddEqualsHashCodeDecorator<T>(intf)::apply
        val addToString = AddToStringDecorator<T>(intf)::apply
        val toMap = ToMapDecorator<T>(intf)::apply

        val unloaded = toMap(addToString(addEqualsHashCode(
                addMixin(addAlias(addGetterSetter(addField(addConstructor(init(intf))))))))).make()
        interceptor.onLoading(unloaded)

        val loaded = unloaded.load(Implicit::class.java.classLoader, INJECTION)
        interceptor.onLoaded(loaded)

        val loadedType = loaded.loaded
        intfTypeRegistry.put(intf.name, loadedType.name)

        return loadedType
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getSupplierType(type: Class<T>): Class<Supplier<T>> {
        return ByteBuddy()
                .subclass(Supplier::class.java)
                .method(isDeclaredBy<MethodDescription>(Supplier::class.java))
                .intercept(MethodDelegation.toConstructor(type))
                .make()
                .load(type.classLoader)
                .loaded as Class<Supplier<T>>
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> getSupplier(type: Class<T>, cache: Boolean = false,
                        interceptor: ImplicitInterceptor = ImplicitInterceptor()): Supplier<out T> {
        if (cache && supplierRegistry.containsKey(type.name))
            return supplierRegistry[type.name] as Supplier<out T>

        if (type.isInterface) {
            val supplier = getSupplierType(create(type, interceptor)).newInstance()
            if (cache)
                supplierRegistry[type.name] = supplier
            return supplier
        }
        val supplier = getSupplierType(type).newInstance()
        if (cache)
            supplierRegistry[type.name] = supplier
        return supplier
    }

    // TODO: performance improvement
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> getFunction(type: Class<T>, cache: Boolean = false): Function<Map<*, *>, out T> {
        return Function { map ->
            val instance: T = instantiate(type, cache)
            val implicitViolations = getType(instance!!).declaredMethods
                    .filter { method -> isSetter(method) }
                    .fold(ImplicitViolations(listOf())) { acc, entry ->
                        try {
                            val field = getFieldNameFromSetterMethod(entry)
                            setMapValueInInstance(instance, entry, map[field])
                            acc
                        } catch (ex: ImplicitValidationException) {
                            ImplicitViolations(acc.violations.plus(ex))
                        }
                    }
            if(implicitViolations!=null && !implicitViolations.violations.isEmpty()){
                throw implicitViolations
            }
            return@Function instance
        }
    }

    fun <T> setMapValueInInstance(instance: T, method: Method, fieldValue: Any?) {
        if (fieldValue != null) {
            val clazz = method.parameterTypes[0]
            if (isNestedImplicitObject(clazz, fieldValue))
                invoke(instance, method, instantiateNestedObject(clazz, fieldValue as Map<*, *>))
            else
                invoke(instance, method, TypeConversion.convert(fieldValue, method.parameterTypes[0]))
        } else {
            initializeField(instance, method, method.parameterTypes[0])
        }
    }

    fun getFieldNameFromSetterMethod(setter: Method): String = setter.name.substring(3).decapitalize()
    fun isNestedImplicitObject(objClass: Class<*>, fieldValue: Any?): Boolean = objClass.isInterface && objClass != Map::class.java && fieldValue is Map<*, *>
    fun isSetter(method: Method): Boolean = method.name.startsWith("set")
    fun isGetter(method: Method): Boolean = method.name.startsWith("get") || method.name.startsWith("is")
    fun <T> instantiateNestedObject(clazz: Class<T>, map: Map<*, *>) = instantiate(clazz, map)

    fun <T> initializeField(instance: T, setter: Method, parameterType: Class<*>) {
        if (!isFieldInitialized(instance, getFieldNameFromSetterMethod(setter)) && !parameterType.isPrimitive) {
            invoke(instance, setter, null)
        }
    }

    private fun <T> isFieldInitialized(instance: T, fieldName: String): Boolean {
        val fieldGetter = getType(instance!!).declaredMethods
                .filter { method -> isGetter(method) && method.name.contains(fieldName.capitalize()) }
                .first()

        return fieldGetter.invoke(instance) != null
    }

    fun <T> invoke(instance: T, setter: Method, value: Any?) {
        try {
            setter.invoke(instance, value)
        } catch (ex: InvocationTargetException) {
            when (ex.targetException) {
                is ImplicitException -> throw ex.targetException
                else -> throw ex
            }
        }
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> instantiate(type: Class<T>, cache: Boolean = false): T {
        return getSupplier(type, cache).get()
    }

    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> instantiate(type: Class<T>, map: Map<*, *>, cache: Boolean = false): T {
        return getFunction(type, cache).apply(map)
    }

    private fun <T> init(intf: Class<T>): DynamicType.Builder<T> {
        return ByteBuddy().with(object : NamingStrategy.AbstractBase() {
            override fun name(superClass: TypeDescription): String {
                return namingStrategy(superClass).toString()
            }
        }).subclass(intf).annotateType(*intf.annotations)
    }

    private fun getType(obj: Any) = obj::class.java

}
