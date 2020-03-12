package implicit.interceptor.validator

import implicit.annotation.validation.Min
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class MinValidator(val annotation: Min) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is Number && value.toFloat() < annotation.size) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("value of field '${method.name}' is < ${annotation.size}")
            }
        }
    }

}
