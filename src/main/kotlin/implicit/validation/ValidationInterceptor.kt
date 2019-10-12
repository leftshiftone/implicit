package implicit.validation

import implicit.annotation.Implicit
import implicit.annotation.validation.*
import implicit.exception.ImplicitException
import implicit.validation.validator.*
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import java.lang.reflect.Method


object ValidationInterceptor {

    @RuntimeType
    fun intercept(@RuntimeType value: Any?, @Origin method: Method): Any? {
        val annotations = ArrayList<Annotation>()
        annotations.addAll(method.declaredAnnotations)
        annotations.addAll(method.parameterAnnotations.flatten())

        annotations
                .filter { it.annotationClass.annotations.any { e -> e is Implicit } }
                .map(this::getValidator)
                .forEach { it.validate(listOf(value), method) }

        return value
    }

    private fun getValidator(annotation: Annotation): AbstractValidator {
        when (annotation) {
            is NotNull -> return NotNullValidator(annotation)
            is GreaterThan -> return GreaterThanValidator(annotation)
            is GreaterEquals -> return GreaterEqualsValidator(annotation)
            is LowerThan -> return LowerThanValidator(annotation)
            is LowerEquals -> return LowerEqualsValidator(annotation)
            is NotBlank -> return NotBlankValidator(annotation)
            is MaxLength -> return MaxLengthValidator(annotation)
            is MinLength -> return MinLengthValidator(annotation)
            is ContentNotNull -> return ContentNullValidator(annotation)
            is Between -> return BetweenValidator(annotation)
            else -> throw ImplicitException("unknown annotation $annotation")
        }
    }

}
