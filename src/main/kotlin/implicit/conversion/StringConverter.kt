package implicit.conversion

import java.time.OffsetDateTime

object StringConverter {
    fun <O> convert(from: String, to: Class<O>): Any {
        return when (to) {
            OffsetDateTime::class.java -> OffsetDateTime.parse(from)
            java.lang.String::class.java -> from
            Object::class.java -> from
            else -> throw IllegalArgumentException("Can not convert ${from::class.java.simpleName} to ${to.simpleName}")
        }
    }
}
