package implicit.interceptor.validator

import implicit.annotation.validation.Pattern
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class RegexValidator(val annotation: Pattern) : AbstractValidator() {

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
