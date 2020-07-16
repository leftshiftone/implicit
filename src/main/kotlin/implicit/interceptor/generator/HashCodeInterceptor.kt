package implicit.interceptor.generator

import implicit.annotation.generator.EqualsHashCode
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object HashCodeInterceptor {

    private val methodCache = ConcurrentHashMap<String, List<Method>>()

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        val cls = obj::class.java
        methodCache.computeIfAbsent(cls.name) { getRelevantMethods(cls) }

        val values = methodCache[cls.name]!!.map { it.invoke(obj) }
        return Objects.hash(values)
    }

    private fun getRelevantMethods(cls: Class<*>): List<Method> {
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m ->
                    val annotation = m.getAnnotation(EqualsHashCode::class.java)
                    annotation == null || !annotation.exclude
                }
    }

}
