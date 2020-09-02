package implicit.conversion

object TypeConversion {

    @Suppress("UNCHECKED_CAST")
    fun <O> convert(from: Any?, to: Class<O>): O? {
        if (from == null) return null
        val fromClazz = from::class.java
        if (fromClazz == to) return from as O?

        return when (from) {
            is Number -> NumberConverter.convert(from, to) as O?
            is String -> StringConverter.convert(from, to) as O?
            else -> from as O // do not convert and pray for the best
        }
    }
}
