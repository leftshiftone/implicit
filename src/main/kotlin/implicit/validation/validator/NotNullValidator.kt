package implicit.validation.validator

import implicit.annotation.validation.NotNull
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class NotNullValidator(val annotation: NotNull) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value == null) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("value of field '${method.name}' must not be null")
            }
        }
    }

}
