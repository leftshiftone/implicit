package implicit.annotation.validation.composite

import implicit.annotation.Implicit
import implicit.annotation.Implicit.Type.VALIDATOR
import implicit.annotation.validation.Pattern

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Implicit(VALIDATOR)
@Pattern("^(?:(?=[^:/]{4,253})(?!-)[a-zA-Z0-9-]{1,63}(?<!-)(?:\\\\.(?!-)[a-zA-Z0-9-]{1,63}(?<!-))*(?::[0-9]{1,5})?/)?((?![._-])(?:[a-z0-9._-]*)(?<![._-])(?:/(?![._-])[a-z0-9._-]*(?<![._-]))*)(?::(?![.-])[a-zA-Z0-9_.-]{1,128})?\\\$")
annotation class Docker
