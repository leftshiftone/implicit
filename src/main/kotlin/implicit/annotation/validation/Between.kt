package implicit.annotation.validation

import implicit.annotation.Implicit
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
@Implicit
annotation class Between(val min:Int, val max:Int, val message:String = "")
