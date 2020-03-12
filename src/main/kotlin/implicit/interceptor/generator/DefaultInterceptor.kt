package implicit.interceptor.generator

import implicit.annotation.generator.Default
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap


object DefaultInterceptor {

    private val annotationCache = ConcurrentHashMap<String, Default>()
    private val fieldCache = ConcurrentHashMap<String, Field>()

    @RuntimeType
    fun intercept(@Origin method: Method, @This obj: Any): Any? {
        annotationCache.computeIfAbsent(method.name) { _ -> method.getAnnotation(Default::class.java) }
        val annotation = annotationCache[method.name]!!

        fieldCache.computeIfAbsent(method.name) { _ -> getField(method, obj) }
        return fieldCache[method.name]!!.get(obj) ?: when (method.returnType.simpleName) {
            "String" -> annotation.value
            "Integer" -> coalesce(annotation.value, "0").toInt()
            "Int" -> coalesce(annotation.value, "0").toInt()
            "Float" -> coalesce(annotation.value, "0.0").toFloat()
            "Double" -> coalesce(annotation.value, "0.0").toDouble()
            "Boolean" -> coalesce(annotation.value, "false").toBoolean()
            "Short" -> coalesce(annotation.value, "0").toShort()
            "Long" -> coalesce(annotation.value, "0").toLong()
            "Byte" -> coalesce(annotation.value, "0").toByte()
            "List" -> ArrayList<Any>()
            "Map" -> HashMap<Any, Any>()
            else -> throw IllegalArgumentException("cannot set default value for $method")
        }
    }

    private fun getField(method: Method, obj: Any): Field {
        val fieldName = method.name.substring(3).decapitalize()

        val field = obj::class.java.getDeclaredField(fieldName)
        field.isAccessible = true

        return field
    }

    private fun coalesce(str:String, defaultStr:String) = if (str.isBlank()) defaultStr else str

}
