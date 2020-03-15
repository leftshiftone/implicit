package implicit.annotation.validation.composite

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.VALIDATOR
import implicit.annotation.validation.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
@Implicit(VALIDATOR)
@Pattern("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}", "invalid uuid")
annotation class UUID
