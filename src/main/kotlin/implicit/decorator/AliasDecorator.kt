package implicit.decorator

import implicit.annotation.generator.Alias
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FieldAccessor
import java.lang.reflect.Modifier
import java.util.function.Function


class AliasDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

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
                val modifier = Modifier.PUBLIC
                result = result.defineMethod("get" + annotation.capitalize(), String::class.java, modifier)
                        .intercept(FieldAccessor.ofField(method.name.substring(3).decapitalize()))

                result = result.defineMethod("set" + annotation.capitalize(), Void.TYPE, modifier)
                        .withParameter(String::class.java)
                        .intercept(FieldAccessor.ofField(method.name.substring(3).decapitalize()))
            }
        }
        return result
    }

    private fun getAnnotation(annotations: Array<Annotation>): String? {
        return annotations.flatMap { annotation ->
            if (annotation is Alias) {
                return@flatMap listOf(annotation.value)
            }
            val result = annotation.annotationClass.annotations.find { it is Alias }
            if (result == null) listOf() else listOf((result as Alias).value)
        }.firstOrNull()
    }

}
