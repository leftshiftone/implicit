package implicit.exception

class ImplicitViolations(val violations: List<ImplicitValidationException>) : ImplicitException("Implicit violations were detected: " + violations.fold("") { acc, entry ->  acc.plus("[" + entry.message+ "]") })
