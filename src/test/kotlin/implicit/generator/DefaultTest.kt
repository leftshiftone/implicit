package implicit.generator

import implicit.Implicit
import implicit.annotation.generator.Default
import implicit.annotation.validation.NotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DefaultTest {

    @Test
    fun `create instance with getter annotation`() {
        val factory = Implicit { "implicit.generator.default_.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java, true)

        val pojo = supplier.get()
        Assertions.assertEquals("defaultValue", pojo.getAbc())

        pojo.setAbc("test")
        Assertions.assertEquals("test", pojo.getAbc())
    }

    @Test
    fun `create instance with getter annotation in a loop`() {
        val factory = Implicit { "implicit.generator.default_.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java, true)

        (1..1000000).forEach { _ ->
            val pojo = supplier.get()
            Assertions.assertEquals("defaultValue", pojo.getAbc())
        }
    }

    @Test
    fun `test types`() {
        val factory = Implicit { "implicit.generator.default_.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity2::class.java)

        val pojo = supplier.get()
        Assertions.assertEquals("defaultValue", pojo.getStringVal())
        Assertions.assertEquals(0, pojo.getIntVal())
        Assertions.assertEquals(1.5f, pojo.getFloatVal())
        Assertions.assertEquals(1.5, pojo.getDoubleVal())
        Assertions.assertEquals(true, pojo.getBooleanVal())
        Assertions.assertEquals(0, pojo.getByteVal())
        Assertions.assertEquals(0, pojo.getShortVal())
        Assertions.assertEquals(0, pojo.getLongVal())
        Assertions.assertNotNull(pojo.getListVal())
        Assertions.assertNotNull(pojo.getMapVal())
    }

    interface Entity {
        @Default("defaultValue")
        fun getAbc(): String

        fun setAbc(@NotNull id: String)
    }

    interface Entity2 {
        @Default("defaultValue")
        fun getStringVal(): String?

        @Default("0")
        fun getIntVal(): Int?

        @Default("1.5")
        fun getFloatVal(): Float?

        @Default("1.5")
        fun getDoubleVal(): Double?

        @Default("true")
        fun getBooleanVal(): Boolean?

        @Default("0")
        fun getShortVal(): Short?

        @Default("0")
        fun getLongVal(): Long?

        @Default("0")
        fun getByteVal(): Byte?

        @Default
        fun getListVal(): List<String>?

        @Default
        fun getMapVal(): Map<String, String>?
    }

}
