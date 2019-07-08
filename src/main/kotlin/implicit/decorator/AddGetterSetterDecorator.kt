package implicit.decorator

import implicit.annotation.Explicit
import net.bytebuddy.description.ByteCodeElement
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers.*
import java.util.function.Function

class AddGetterSetterDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    /**
     * {@inheritDoc}
     */
    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        val typeMatcher = object:ElementMatcher<TypeDescription> {
            override fun matches(target: TypeDescription): Boolean {
                return target.isInHierarchyWith(intf) && target.isInterface
            }
        }
        val nameMatcher = object:ElementMatcher<MethodDescription> {
            override fun matches(target: MethodDescription): Boolean {
                return target.name.startsWith("get") || target.name.startsWith("set")
            }
        }

        return builder
                .method(isDeclaredBy<ByteCodeElement>(typeMatcher)
                        .and(not<MethodDescription>(isDefaultMethod<MethodDescription>()))
                        .and(not<MethodDescription>(isAnnotatedWith(Explicit::class.java)))
                        .and(nameMatcher))
                .intercept(FieldAccessor.ofBeanProperty())
    }

}
