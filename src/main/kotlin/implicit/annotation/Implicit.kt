package implicit.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS

@Retention(RUNTIME)
@Target(ANNOTATION_CLASS)
internal annotation class Implicit(val value: Type) {
    enum class Type {
        VALIDATOR, GENERATOR
    }
}
