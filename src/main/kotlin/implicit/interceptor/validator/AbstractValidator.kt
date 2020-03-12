package implicit.interceptor.validator

import java.lang.reflect.Method

abstract class AbstractValidator {
    abstract fun validate(values: List<*>, method: Method)
}
