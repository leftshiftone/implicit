package implicit.decorator

import implicit.annotation.generator.Mixin
import net.bytebuddy.dynamic.DynamicType
import java.util.function.Function


class MixinDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    /**
     * {@inheritDoc}
     */
    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        return handleGenerators(builder)
    }

    private fun handleGenerators(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        var result = builder
        for (method in intf.declaredMethods) {
            val annotation = getAnnotation(method.declaredAnnotations)
            if (annotation != null) {
                result = result.implement(annotation)
            }
        }
        return result
    }

    private fun getAnnotation(annotations: Array<Annotation>): Class<*>? {
        return annotations.flatMap { annotation ->
            if (annotation is Mixin) {
                return@flatMap listOf(annotation.clazz.java)
            }
            val result = annotation.annotationClass.annotations.find { it is Mixin }
            if (result == null) listOf() else listOf((result as Mixin).clazz.java)
        }.firstOrNull()
    }

}
