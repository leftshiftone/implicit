package implicit

import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Simple.Annotatable

open class ImplicitInterceptor {

    open fun <T>onConstructorParameter(annotatable: Annotatable<T>) : Annotatable<T> {
        return annotatable
    }

    open fun <T>onLoading(unloaded: DynamicType.Unloaded<T>) {
        // do nothing
    }

    open fun <T>onLoaded(loaded: DynamicType.Loaded<T>) {
        // do nothing
    }
}
