package implicit.annotation.generator

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.GENERATOR
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(FUNCTION, ANNOTATION_CLASS)
@Implicit(GENERATOR)
annotation class Mixin(val value: KClass<*>)
