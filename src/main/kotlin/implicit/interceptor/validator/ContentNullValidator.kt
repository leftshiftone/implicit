package implicit.interceptor.validator

import implicit.annotation.validation.ContentNotNull
import implicit.exception.ImplicitValidationException
import java.lang.reflect.Method

internal class ContentNullValidator(val annotation: ContentNotNull) : AbstractValidator() {

    override fun validate(values: List<*>, method: Method) {
        for (value in values) {
            if (value is Map<*, *>) {
                isNotNull(value, method)
            }
            if (value is Collection<*>) {
                isNotNull(value, method)
            }
        }
    }

    private fun isNotNull(map:Map<*, *>, method: Method) {
        for (entry in map.entries) {
            if (entry.key == null || entry.value == null) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("content of field '${method.name}' must not be null")
            }
            if (entry.value is Map<*, *>) {
                isNotNull(entry.value as Map<*, *>, method)
            }
            if (entry.value is Collection<*>) {
                isNotNull(entry.value as Collection<*>, method)
            }
        }
    }

    private fun isNotNull(collection:Collection<*>, method: Method) {
        for (entry in collection) {
            if (entry == null) {
                if (annotation.message.isNotBlank())
                    throw ImplicitValidationException(annotation.message)
                throw ImplicitValidationException("content of field '${method.name}' must not be null")
            }
            if (entry is Map<*, *>) {
                isNotNull(entry, method)
            }
            if (entry is Collection<*>) {
                isNotNull(entry, method)
            }
        }
    }

}
