package implicit.annotation.validation

import implicit.annotation.Implicit
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER, ANNOTATION_CLASS)
@Implicit
annotation class Pattern(val pattern:String, val message:String = "")
