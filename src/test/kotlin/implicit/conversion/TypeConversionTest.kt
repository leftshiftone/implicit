package implicit.conversion

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal

internal class TypeConversionTest {

    @TestFactory
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "USELESS_CAST")
    fun `can convert`() = listOf(
            Fixture("test", String::class.java, "test"),
            Fixture(BigDecimal("1"), Int::class.java, 1),
            Fixture(BigDecimal("1"), java.lang.Integer::class.java, 1),
            Fixture(BigDecimal("3.14"), Float::class.java, 3.14f),
            Fixture(BigDecimal("3.14"), java.lang.Float::class.java, 3.14f),
            Fixture(3.14f as Float, Float::class.java, 3.14f),
            Fixture(3.14f as java.lang.Float, Float::class.java, 3.14f),
            Fixture(1 as Int, Int::class.java, 1),
            Fixture(1 as Int, java.lang.Integer::class.java, 1),
            Fixture(java.lang.Integer(1), Int::class.java, 1),
            Fixture(java.lang.Integer(1), java.lang.Integer::class.java, 1),
            Fixture(java.lang.Long("1"), Int::class.java, 1),
            Fixture(java.lang.Long("1"), java.lang.Integer::class.java, 1),
            Fixture(mapOf("a" to 1), Map::class.java, mapOf("a" to 1))
    ).map { fixture ->
        DynamicTest.dynamicTest("can convert ${fixture.value} to ${fixture.clazz}") {
            val result = TypeConversion.convert(fixture.value, fixture.clazz)
            assertThat(fixture.expectation).isEqualTo(result)
        }
    }

    @Test
    fun `throws on unsupported conversions`() {
        val fixture = Fixture(BigDecimal("1"), String::class.java, 1)

        assertThatThrownBy {
            TypeConversion.convert(fixture.value, fixture.clazz)
        }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Can not convert BigDecimal to String")
    }

    internal inner class Fixture(
            val value: Any?,
            val clazz: Class<*>,
            val expectation: Any?
    )
}
