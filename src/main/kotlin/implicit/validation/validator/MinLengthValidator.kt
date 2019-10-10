package implicit.validation.validator

import implicit.annotation.validation.MinLength
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class MinLengthValidator(val annotation: MinLength) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is String && value.length < annotation.size) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("length of field '${method.name}' is < ${annotation.size}")
            }
        }
    }

}
