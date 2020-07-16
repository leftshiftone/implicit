package implicit.extension

import java.lang.reflect.Method
import kotlin.reflect.KClass

fun <T : Annotation> Method.findAnnotation(type: KClass<T>): T? {
    if (this.isAnnotationPresent(type.java))
        return this.getAnnotation(type.java)

    return this.annotations.flatMap { annotation ->
        if (annotation.annotationClass == type) {
            return@flatMap listOf(annotation as T)
        }
        val result = annotation.annotationClass.annotations.find { it.annotationClass == type }
        if (result == null) listOf() else listOf((result as T))
    }.firstOrNull()
}

fun <T : Annotation> Class<*>.findAnnotation(type: KClass<T>): T? {
    if (this.isAnnotationPresent(type.java))
        return this.getAnnotation(type.java)

    return this.annotations.flatMap { annotation ->
        if (annotation.annotationClass == type) {
            return@flatMap listOf(annotation as T)
        }
        val result = annotation.annotationClass.annotations.find { it.annotationClass == type }
        if (result == null) listOf() else listOf((result as T))
    }.firstOrNull()
}