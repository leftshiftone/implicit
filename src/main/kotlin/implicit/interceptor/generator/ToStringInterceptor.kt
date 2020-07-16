package implicit.interceptor.generator

import implicit.annotation.generator.ToString
import implicit.marker.IMappable
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.util.concurrent.ConcurrentHashMap


object ToStringInterceptor {

    private val fieldCache = ConcurrentHashMap<String, List<String>>()

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        val cls = obj::class.java.interfaces[0]
        fieldCache.computeIfAbsent(cls.name) { relevantFields(cls) }

        val map = (obj as IMappable).toMap()
        val fields = fieldCache[cls.name]!!.map { "$it=${map[it]}" }

        return "${cls.simpleName}(${fields.joinToString(", ")})"
    }

    private fun relevantFields(cls: Class<*>): List<String> {
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m ->
                    val annotation = m.getAnnotation(ToString::class.java)
                    annotation == null || !annotation.exclude
                }
                .map { m -> m.name.substring(3).decapitalize() }
    }

}
