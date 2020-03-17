package implicit.interceptor.generator

import implicit.annotation.generator.EqualsHashCode
import net.bytebuddy.implementation.bind.annotation.Argument
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object EqualsInterceptor {

    private val methodCache = ConcurrentHashMap<String, List<Method>>()

    @RuntimeType
    fun intercept(@This obj: Any, @Argument(0) arg: Any): Any? {
        val cls = obj::class.java
        methodCache.putIfAbsent(cls.name, getRelevantMethods(cls))

        val values1 = methodCache[cls.name]!!.map { it.invoke(obj) }
        val values2 = methodCache[cls.name]!!.map { it.invoke(arg) }

        return Objects.hash(values1).equals(Objects.hash(values2))
    }

    private fun getRelevantMethods(cls: Class<*>): List<Method> {
        return cls.declaredMethods
                .filter { m -> m.name.startsWith("get") }
                .filter { m -> !m.isDefault }
                .filter { m -> !m.isAnnotationPresent(EqualsHashCode::class.java) }
    }

}
