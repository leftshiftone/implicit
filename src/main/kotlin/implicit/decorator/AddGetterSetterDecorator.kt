package implicit.decorator

import implicit.annotation.Explicit
import implicit.annotation.Implicit
import implicit.annotation.generator.Default
import implicit.annotation.generator.Trim
import implicit.interceptor.generator.DefaultInterceptor
import implicit.interceptor.generator.TrimInterceptor
import implicit.interceptor.generator.ValidationInterceptor
import net.bytebuddy.description.ByteCodeElement
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FieldAccessor
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatcher
import net.bytebuddy.matcher.ElementMatchers.*
import java.util.function.Function


class AddGetterSetterDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        val typeMatcher = object : ElementMatcher<TypeDescription> {
            override fun matches(target: TypeDescription): Boolean {
                return target.isInHierarchyWith(intf) && target.isInterface
            }
        }
        val nameMatcher = object : ElementMatcher<MethodDescription> {
            override fun matches(target: MethodDescription): Boolean {
                return target.name.startsWith("get") || target.name.startsWith("set")
            }
        }
        val annotationMatcher = object : ElementMatcher<MethodDescription> {
            override fun matches(target: MethodDescription): Boolean {
                val expr1 = target.declaredAnnotations.any {
                    it.annotationType.declaredAnnotations.any { e ->
                        e.toString() == "@implicit.annotation.Implicit(value=VALIDATOR)"
                    }
                }
                val expr2 = target.parameters.any { x ->
                    x.declaredAnnotations.any { y ->
                        y.annotationType.declaredAnnotations.isAnnotationPresent(Implicit::class.java)
                    }
                }
                return expr1 || expr2
            }
        }

        return builder
            .method(
                isDeclaredBy<ByteCodeElement>(typeMatcher)
                    .and(not<MethodDescription>(isDefaultMethod<MethodDescription>()))
                    .and(not<MethodDescription>(isAnnotatedWith(Explicit::class.java)))
                    .and(not<MethodDescription>(isAnnotatedWith(Default::class.java)))
                    .and(nameMatcher)
            )
            .intercept(FieldAccessor.ofBeanProperty())
            .method(
                isDeclaredBy<ByteCodeElement>(typeMatcher)
                    .and(annotationMatcher)
                    .and(not<MethodDescription>(isAnnotatedWith(Default::class.java)))
                    .and(nameMatcher)
            )
            .intercept(
                MethodDelegation.to(ValidationInterceptor)
                    .andThen(FieldAccessor.ofBeanProperty())
            )
            .method(
                isDeclaredBy<ByteCodeElement>(typeMatcher)
                    .and(nameMatcher)
                    .and(isAnnotatedWith(Default::class.java))
            )
            .intercept(MethodDelegation.to(DefaultInterceptor))
            .method(
                isDeclaredBy<ByteCodeElement>(typeMatcher)
                    .and(nameMatcher)
                    .and(isAnnotatedWith(Trim::class.java))
            )
            .intercept(MethodDelegation.to(TrimInterceptor))
    }

}
