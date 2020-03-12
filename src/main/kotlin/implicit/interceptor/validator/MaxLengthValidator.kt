package implicit.interceptor.validator

import implicit.annotation.validation.MaxLength
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class MaxLengthValidator(val annotation: MaxLength) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is String && value.length > annotation.size) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("length of field '${method.name}' is > ${annotation.size}")
            }
        }
    }

}
