package implicit.annotation.generator

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.GENERATOR
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Retention(RUNTIME)
@Target(FUNCTION, ANNOTATION_CLASS, CLASS)
@Implicit(GENERATOR)
annotation class EqualsHashCode(val exclude:Boolean = false)
