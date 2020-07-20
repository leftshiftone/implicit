package implicit.interceptor.validator

import implicit.annotation.validation.NotEmpty
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class NotEmptyValidator(val annotation: NotEmpty) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value ==null){
                throwImplicitValidationException(method)
            }
            when (value) {
                is Collection<*> -> if(value.isEmpty()) throwImplicitValidationException(method)
                is String -> if(value.isEmpty()) throwImplicitValidationException(method)

            }
        }
    }

    fun throwImplicitValidationException(method: Method){
        if (annotation.message.isNotBlank())
            throw ImplicitValidationException(annotation.message)
        throw ImplicitValidationException("value of field '${method.name}' is empty")
    }

}
