package implicit.decorator

import implicit.interceptor.generator.ToStringInterceptor
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.DynamicType.Builder
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import java.util.function.Function


class AddToStringDecorator<T>(private val type: Class<*>) : Function<Builder<T>, Builder<T>> {

    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        return builder.method(ElementMatchers.isToString())
                .intercept(MethodDelegation.to(ToStringInterceptor))
    }

}
