package implicit.validation.validator

import implicit.annotation.validation.Between
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class BetweenValidator(val annotation: Between) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is Number && value.toInt() >= annotation.min && value.toInt() <= annotation.max) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("value of field '${method.name}' is not between ${annotation.min} and ${annotation.max}")
            }
        }
    }

}
