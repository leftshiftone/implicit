package implicit.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.TYPE

@Retention(RUNTIME)
@Target(TYPE, FUNCTION)
annotation class Explicit
