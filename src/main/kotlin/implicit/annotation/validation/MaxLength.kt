package implicit.annotation.validation

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.VALIDATOR
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
@Implicit(VALIDATOR)
annotation class MaxLength(val size:Int, val message:String = "")
