package implicit.decorator

import implicit.ImplicitInterceptor
import implicit.annotation.Explicit
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Simple.Annotatable
import net.bytebuddy.implementation.FieldAccessor.ofField
import net.bytebuddy.implementation.Implementation
import net.bytebuddy.implementation.Implementation.Composable
import net.bytebuddy.implementation.MethodCall
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

        return annotatable.intercept(invoke)
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

}
