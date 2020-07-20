package implicit.validation.validator

import implicit.Implicit
import implicit.annotation.validation.NotEmpty
import implicit.exception.ImplicitException
import implicit.exception.ImplicitValidationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class NotEmptyValidatorTest {

    @Test
    fun callingStringSetterWithNullValue() {
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
    fun callingCollectionSetterWithNullValue() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(ITest::class.java)

        val counter = AtomicInteger()

        val instance = supplier.get()

        try {
            instance.setCollectionList(null)
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        try {
            instance.setCollectionList(listOf("abc","def"))
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNotNull(instance.getCollectionList())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun callingStringSetterWithEmptyValue() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(ITest::class.java)

        val counter = AtomicInteger()

        val instance = supplier.get()

        try {
            instance.setName("")
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        try {
            instance.setName("test")
            instance.setCollectionList(listOf("abc"))
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNotNull(instance.getName())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun callingCollectionSetterWithEmptyList() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(ITest::class.java)

        val counter = AtomicInteger()

        val instance = supplier.get()

        try {
            instance.setCollectionList(listOf<Any>())
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        try {
            instance.setName("test")
            instance.setCollectionList(listOf("abc","def"))

        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNotNull(instance.getCollectionList())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun instantiatingAnEntityWhichFieldInMapIsNull() {
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
    fun instantiatingAnEntityWhichFieldInMapIsNotPresent() {
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
        Assertions.assertNull(instance.getCollectionList())
        Assertions.assertEquals(counter.get(), 1)
    }

    @Test
    fun instantiatingAnEntityWhichFieldInMapIsEmpty() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }

        val counter = AtomicInteger()
        var instance = factory.getSupplier(ITest::class.java).get()
        val map = mapOf<String, Any?>("name" to "abc", "list" to mapOf<String,Any>())
        try {
            instance = factory.instantiate(ITest::class.java, map)
        } catch (ex: ImplicitException) {
            counter.incrementAndGet()
        }
        Assertions.assertNull(instance.getName())
        Assertions.assertEquals(counter.get(), 1)
    }


    interface ITest {
        fun setName(@NotEmpty name: String?)
        fun getName(): String?
        fun setCollectionList(@NotEmpty collectionList: List<Any>?)
        fun getCollectionList(): List<Any>?
    }

}
