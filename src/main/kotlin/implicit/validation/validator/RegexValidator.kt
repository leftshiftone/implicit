package implicit.validation.validator

import implicit.annotation.validation.Regex
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class RegexValidator(val annotation: Regex) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value != null || !value.toString().matches(kotlin.text.Regex.fromLiteral(annotation.pattern))) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("value of field '${method.name}' does not match regex")
            }
        }
    }

}
