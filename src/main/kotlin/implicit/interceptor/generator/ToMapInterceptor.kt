package implicit.interceptor.generator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import java.time.OffsetDateTime


object ToMapInterceptor {

    private val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    @RuntimeType
    fun intercept(@This obj: Any): Any? {
        var map = objectMapper.convertValue(obj, Map::class.java)

        if(map.containsKey("updateTime") && map["updateTime"] != null) {
            map = map.plus(Pair("updateTime", OffsetDateTime.parse(map["updateTime"] as String)))
        }

        if(map.containsKey("createTime") && map["createTime"] != null) {
            map = map.plus(Pair("createTime", OffsetDateTime.parse(map["createTime"] as String)))
        }

        return map
    }

}
