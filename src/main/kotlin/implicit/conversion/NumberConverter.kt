package implicit.conversion

object NumberConverter {
    fun <O> convert(from: Number, to: Class<O>): Any {
        return when (to) {
            Int::class.java,
            Integer::class.java -> from.toInt()
            Float::class.java,
            java.lang.Float::class.java -> from.toFloat()
            Long::class.java,
            java.lang.Long::class.java -> from.toLong()
            Number::class.java,
            java.lang.Number::class.java -> from
            else -> throw IllegalArgumentException("Can not convert ${from::class.java.simpleName} to ${to.simpleName}")
        }
    }
}
