package implicit.decorator

import implicit.annotation.Explicit
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.DefaultMethodCall
import net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith
import java.util.function.Function

class AddExplicitDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    /**
     * {@inheritDoc}
     */
    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        return builder
                .method(isAnnotatedWith(Explicit::class.java))
                .intercept(DefaultMethodCall.prioritize(intf))
    }

}
