package implicit.annotation.validation.composite

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.VALIDATOR
import implicit.annotation.validation.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
@Implicit(VALIDATOR)
@Pattern("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "invalid url")
annotation class URL
