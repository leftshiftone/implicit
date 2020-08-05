package implicit.annotation.generator

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GenericType(val value: KClass<*>)
