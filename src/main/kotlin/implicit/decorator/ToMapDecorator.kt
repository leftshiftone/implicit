package implicit.decorator

import implicit.interceptor.generator.ToMapInterceptor
import implicit.marker.IMappable
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import java.util.function.Function


class ToMapDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    /**
     * {@inheritDoc}
     */
    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        return builder
                .implement(IMappable::class.java)
                .method(ElementMatchers.named("toMap"))
                .intercept(MethodDelegation.to(ToMapInterceptor))
    }

}
