package implicit.interceptor.generator

import implicit.annotation.generator.ToString
import implicit.extension.findAnnotation
import implicit.marker.IMappable
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.util.concurrent.ConcurrentHashMap


object ToStringInterceptor {

    private val fieldCache = ConcurrentHashMap<String, Set<String>>()

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        val cls = obj::class.java.interfaces[0]
        fieldCache.computeIfAbsent(cls.name) { relevantFields(cls).toSortedSet() }

        val map = (obj as IMappable).toMap().toSortedMap()
        val fields = fieldCache[cls.name]!!.map { "$it=${map[it]}" }

        return "${cls.simpleName}(${fields.joinToString(", ")})"
    }

    private fun relevantFields(cls: Class<*>): List<String> {
        val typeAnnotation = cls.findAnnotation(ToString::class)
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m ->
                    val annotation = m.findAnnotation(ToString::class)

                    if (annotation != null)
                        return@filter !annotation.exclude
                    if (typeAnnotation != null)
                        return@filter !typeAnnotation.exclude
                    return@filter false
                }
                .map { m -> m.name.substring(3).decapitalize() }
    }

}
