package implicit.validation.validator

import implicit.annotation.validation.GreaterEquals
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class GreaterEqualsValidator(val annotation: GreaterEquals) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is Number && value.toInt() <= annotation.size) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("value of field '${method.name}' is < ${annotation.size}")
            }
        }
    }

}
