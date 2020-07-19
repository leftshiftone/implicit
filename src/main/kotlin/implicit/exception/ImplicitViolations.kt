package implicit.exception

class ImplicitViolations(val violations: List<ImplicitValidationException>) : ImplicitException("Implicit validations were detected: " + violations.fold("") { acc, entry ->  acc.plus("[" + entry.message+ "]") })
