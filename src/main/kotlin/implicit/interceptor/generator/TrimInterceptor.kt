package implicit.interceptor.generator

import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap


object TrimInterceptor {

    private val fieldCache = ConcurrentHashMap<String, Field>()

    @RuntimeType
    fun intercept(@Origin method: Method, @This obj: Any): Any? {
        fieldCache.computeIfAbsent(getKey(method)) { _ -> getField(method, obj) }
        val fieldValue = fieldCache[getKey(method)]!!.get(obj)
        return trim(fieldValue)
    }


    private fun getField(method: Method, obj: Any): Field {
        val fieldName = method.name.substring(3).decapitalize()
        val field = obj::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field
    }

    private fun trim(value: Any?): Any? {
        return if (value == null) null else when {
            value is String -> value.trim()
            else -> value
        }
    }

    private fun getKey(method: Method): String {
        return method.declaringClass.name + "#" + method.name
    }

}
