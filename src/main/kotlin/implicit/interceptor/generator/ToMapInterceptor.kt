package implicit.interceptor.generator

import com.fasterxml.jackson.databind.ObjectMapper
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This


object ToMapInterceptor {

    private val objectMapper = ObjectMapper()

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        return objectMapper.convertValue(obj, Map::class.java)
    }

}
