package implicit

import implicit.decorator.*
import net.bytebuddy.ByteBuddy
import net.bytebuddy.NamingStrategy
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.isDeclaredBy
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

        val unloaded = addMixin(addAlias(addGetterSetter(addField(addConstructor(init(intf)))))).make()
        interceptor.onLoading(unloaded)

        val loaded = unloaded.load(Implicit::class.java.classLoader, INJECTION)
        interceptor.onLoaded(loaded)

        val loadedType = loaded.getLoaded()
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
            val instance = instantiate(type, cache)
            type.declaredMethods
                    .filter { it.name.startsWith("set") }
                    .forEach {
                        val field = it.name.substring(3).decapitalize()
                        if (map.containsKey(field)) {
                            it.invoke(instance, map[field])
                        }
                    }
            return@Function instance
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

}
