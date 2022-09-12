package implicit.generator

import implicit.Implicit
import implicit.annotation.generator.Default
import implicit.annotation.generator.Trim
import implicit.annotation.validation.NotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TrimTest {

    @Test
    fun `Set a value with empty spaces in front and after the string`() {
        val factory = Implicit { "implicit.generator.trim_.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java, true)

        val pojo = supplier.get()
        pojo.setAbc("test")
        Assertions.assertEquals("test", pojo.getAbc())

        pojo.setAbc("   test with space to trim   ")
        Assertions.assertEquals("test with space to trim", pojo.getAbc())
    }


    @Test
    fun `Test String fields with and without Trim annotations`() {
        val factory = Implicit { "implicit.generator.trim_.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity2::class.java)

        val pojo = supplier.get()
        pojo.setStringValWithoutTrimAnnotation(" This is a value with multiple empty characters at the beginning and at the end of the value    ")
        pojo.setStringValWithTrimAnnotation("\t\n This is a value with multiple empty characters at the beginning and at the end of the value. But it will be trimmed  \r\n\n  ")
        pojo.setLongVal(1L)


        Assertions.assertEquals(
            " This is a value with multiple empty characters at the beginning and at the end of the value    ",
            pojo.getStringValWithoutTrimAnnotation()
        )
        Assertions.assertEquals(
            "This is a value with multiple empty characters at the beginning and at the end of the value. But it will be trimmed",
            pojo.getStringValWithTrimAnnotation()
        )
        Assertions.assertNull(pojo.getStringNullValWithTrimAnnotation())
        Assertions.assertEquals(0, pojo.getIntVal())
        Assertions.assertEquals(1.5f, pojo.getFloatVal())
        Assertions.assertEquals(1.5, pojo.getDoubleVal())
        Assertions.assertEquals(true, pojo.getBooleanVal())
        Assertions.assertEquals(0, pojo.getShortVal())
        Assertions.assertEquals(1, pojo.getLongVal())
    }

    interface Entity {
        @Trim
        fun getAbc(): String

        fun setAbc(@NotNull id: String)
    }

    interface Entity2 {
        fun getStringValWithoutTrimAnnotation(): String?
        fun setStringValWithoutTrimAnnotation(@NotNull string: String)


        @Trim
        fun getStringValWithTrimAnnotation(): String?
        fun setStringValWithTrimAnnotation(@NotNull string: String)

        @Trim
        fun getStringNullValWithTrimAnnotation(): String?

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

        @Trim
        fun getLongVal(): Long?
        fun setLongVal(longValue: Long)


    }

}
