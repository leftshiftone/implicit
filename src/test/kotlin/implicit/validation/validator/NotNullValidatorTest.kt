package implicit.validation.validator

import implicit.Implicit
import implicit.annotation.validation.NotNull
import implicit.exception.ImplicitException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class NotNullValidatorTest {

    @Test
    fun test() {
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

    interface ITest {
        fun setName(@NotNull name: String?)
        fun getName(): String?
    }

}
