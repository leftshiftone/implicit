package implicit.decorator

import implicit.annotation.Explicit
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.DynamicType
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

class AddFieldDecorator<T>(private val intf: Class<*>) : Function<DynamicType.Builder<T>, DynamicType.Builder<T>> {

    override fun apply(builder: DynamicType.Builder<T>): DynamicType.Builder<T> {
        val reference = AtomicReference(builder)
        eachMethodIn(intf, reference, HashSet())
        return reference.get()
    }

    private fun getFieldName(method: Method, index:Int = 3): String {
        val methodNameWithoutPrefix = method.name.substring(index)
        return methodNameWithoutPrefix.substring(0, 1).toLowerCase() + methodNameWithoutPrefix.substring(1)
    }

    private fun eachMethodIn(intf:Class<*>, reference: AtomicReference<DynamicType.Builder<T>>, registry:HashSet<String>) {
        for (method in intf.methods.sortedBy { it.name }) {
            val name = getFieldName(method)
            if (method.isDefault)
                continue
            if (method.isAnnotationPresent(Explicit::class.java))
                continue
            if (method.name.startsWith("get") && !registry.contains(name)) {
                registry.add(name)
                reference.set(reference.get()
                        .defineField(getFieldName(method), method.returnType, Visibility.PRIVATE)
                        .annotateField(*method.annotations))
            }
            if (method.name.startsWith("set") && !registry.contains(name)) {
                registry.add(name)
                reference.set(reference.get()
                        .defineField(getFieldName(method), method.parameterTypes[0], Visibility.PRIVATE)
                        .annotateField(*method.annotations))
            }
            if (method.name.startsWith("is") && !registry.contains(getFieldName(method, 2))) {
                registry.add(getFieldName(method, 2))
                reference.set(reference.get()
                        .defineField(getFieldName(method), method.returnType, Visibility.PRIVATE)
                        .annotateField(*method.annotations))
            }
        }
    }

}
