package implicit.conversion

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class TypeConversionTest {

    @TestFactory
    fun `can convert`() = listOf(
            Fixture("test", String::class.java, "test"),
            Fixture(BigDecimal("1"), Integer::class.java, 1),
            Fixture(java.lang.Long("1"), Integer::class.java, 1),
            Fixture(java.lang.Integer(1), Integer::class.java, 1),
            Fixture(mapOf("a" to 1), Map::class.java, mapOf("a" to 1))
    ).map { fixture ->
        DynamicTest.dynamicTest("can convert ${fixture.value} to ${fixture.clazz}") {
            val result = TypeConversion.convert(fixture.value, fixture.clazz)
            assertEquals(fixture.expectation, result)
        }

    }

    @Test
    fun `throws on unsupported conversions`() {
        val fixture = Fixture(BigDecimal("1"), String::class.java, 1)

        val ex = assertThrows<IllegalArgumentException> {
            TypeConversion.convert(fixture.value, fixture.clazz)
        }

        assertEquals("Can not convert BigDecimal to String", ex.message)
    }

    internal inner class Fixture(
            val value: Any?,
            val clazz: Class<*>,
            val expectation: Any?
    )
}
