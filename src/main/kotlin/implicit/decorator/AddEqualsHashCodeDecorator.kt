package implicit.decorator

import implicit.annotation.generator.EqualsHashCode
import implicit.interceptor.generator.EqualsInterceptor
import implicit.interceptor.generator.HashCodeInterceptor
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import java.util.function.Function


class AddEqualsHashCodeDecorator<T>(private val type: Class<*>) : Function<Builder<T>, Builder<T>> {

    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        val fields = relevantFields(type)
        if (fields.isEmpty())
            return builder

        return builder.method(ElementMatchers.isEquals())
                .intercept(MethodDelegation.to(EqualsInterceptor))
                .method(ElementMatchers.isHashCode())
                .intercept(MethodDelegation.to(HashCodeInterceptor))
    }

    private fun relevantFields(cls: Class<*>): List<String> {
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m -> !m.isAnnotationPresent(EqualsHashCode::class.java) }
                .map { m -> m.name.substring(3).decapitalize() }
    }

}
