package implicit.validation.validator

import implicit.Implicit
import implicit.annotation.validation.NotNull
import implicit.exception.ImplicitException
import implicit.exception.ImplicitValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class NotNullValidatorTest {

    @Test
    fun callingSetterWithNullValue() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(ITest::class.java)

        val counter = AtomicInteger()

        val instance = supplier.get()

        try {
            instance.setName(null)
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        try {
            instance.setName("test")
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNotNull(instance.getName())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun instantiatingAnEntityWithAMapWhereValueIsNull() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }

        val counter = AtomicInteger()
        var instance = factory.getSupplier(ITest::class.java).get()
        val map = mapOf<String, Any?>("name" to null)
        try {
            instance = factory.instantiate(ITest::class.java, map)
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNull(instance.getName())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun instantiatingAnEntityWithAMapWhereKeyIsNotPresent() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }

        val counter = AtomicInteger()
        var instance = factory.getSupplier(ITest::class.java).get()
        val map = mapOf<String, Any?>()
        try {
            instance = factory.instantiate(ITest::class.java, map)
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNull(instance.getName())
        Assertions.assertEquals(counter.get(), 1)
    }

    interface ITest {
        fun setName(@NotNull name: String?)
        fun getName(): String?
    }

}
