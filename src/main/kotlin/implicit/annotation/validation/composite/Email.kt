package implicit.annotation.validation.composite

import implicit.annotation.Implicit
import implicit.annotation.validation.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
@Implicit
@Pattern("^[a-zA-Z0-9_!#\\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\$", "invalid email")
annotation class Email
