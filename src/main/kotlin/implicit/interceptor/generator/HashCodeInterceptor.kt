package implicit.interceptor.generator

import implicit.annotation.generator.EqualsHashCode
import implicit.extension.findAnnotation
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object HashCodeInterceptor {

    private val methodCache = ConcurrentHashMap<String, List<Method>>()

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        val cls = obj::class.java.interfaces[0]
        methodCache.computeIfAbsent(cls.name) { getRelevantMethods(cls) }

        val values = methodCache[cls.name]!!.map { it.invoke(obj) }
        return Objects.hash(values)
    }

    private fun getRelevantMethods(cls: Class<*>): List<Method> {
        val typeAnnotation = cls.findAnnotation(EqualsHashCode::class)
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m ->
                    val annotation = m.findAnnotation(EqualsHashCode::class)

                    if (annotation != null && !annotation.exclude)
                        return@filter true
                    if (typeAnnotation != null && !typeAnnotation.exclude)
                        return@filter true
                    return@filter false
                }
    }

}
