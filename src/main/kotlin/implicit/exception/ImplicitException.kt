package implicit.exception

open class ImplicitException(message: String, cause: Throwable?) : RuntimeException(message, cause) {

    constructor(message: String) : this(message, null)
}
