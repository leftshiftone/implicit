package implicit.decorator

import implicit.ImplicitInterceptor
import implicit.annotation.Explicit
import implicit.exception.ImplicitException
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Simple.Annotatable
import net.bytebuddy.implementation.FieldAccessor.ofField
import net.bytebuddy.implementation.Implementation
import net.bytebuddy.implementation.Implementation.Composable
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method
import java.lang.reflect.Modifier.PUBLIC
import java.util.function.Function


class AddConstructorDecorator<T>(val intf: Class<*>, val interceptor: ImplicitInterceptor)
    : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    /**
     * {@inheritDoc}
     */
    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        val getters = getters(intf)
        if (getters.isEmpty())
            return builder

        var invoke: Implementation = MethodCall.invoke(Any::class.java.getConstructor())

        getters.forEachIndexed { i, m ->
            invoke = (invoke as Composable).andThen(ofField(getName(m)).setsArgumentAt(i))
        }

        var annotatable: Annotatable<T> = interceptor.onConstructorParameter(builder.defineConstructor(PUBLIC)
                .withParameter(getters(intf)[0].returnType))

        var i = 1
        while (i < getters(intf).size) {
            val method = getters(intf)[i]
            annotatable = interceptor.onConstructorParameter(annotatable
                    .withParameter(method.returnType))
            i++
        }



        return applyMapConstructor(annotatable.intercept(invoke))
    }

    private fun applyMapConstructor(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        return builder.defineConstructor(PUBLIC)
                .withParameter(Map::class.java)
                .intercept(
                        MethodCall.invoke(Any::class.java.getConstructor()).andThen(
                                MethodDelegation.to(MapConstructorInterceptor
                                )))
    }

    private fun getters(cls: Class<*>): List<Method> {
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m -> !m.isAnnotationPresent(Explicit::class.java) }
    }

    private fun getName(method: Method): String {
        val methodNameWithoutPrefix = method.name.substring(3)
        return methodNameWithoutPrefix.substring(0, 1).toLowerCase() + methodNameWithoutPrefix.substring(1)
    }

    object MapConstructorInterceptor {
        fun intercept(@RuntimeType map: Map<String, *>, @This obj: Any) {
            obj::class.java.declaredMethods
                    .filter { it.name.startsWith("set") }
                    .forEach {
                        val field = it.name.substring(3).decapitalize()
                        if (!map.containsKey(field)) {
                            throw ImplicitException("no map entry '${it.name}' found")
                        } else {
                            it.invoke(obj, map[field])
                        }
                    }
        }
    }

}
