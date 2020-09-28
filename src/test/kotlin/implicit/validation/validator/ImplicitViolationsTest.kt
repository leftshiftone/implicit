package implicit.validation.validator

import implicit.Implicit
import implicit.annotation.validation.NotBlank
import implicit.annotation.validation.NotEmpty
import implicit.annotation.validation.NotNull
import implicit.exception.ImplicitException
import implicit.exception.ImplicitViolations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class ImplicitViolationsTest {

    @Test
    fun happyPath() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val map= mapOf(
                "notBlankName" to "name1",
                "notNullName" to "name2",
                "notEmptySet" to setOf("value")
        )

        val instance = factory.instantiate(ITest::class.java, map)

        Assertions.assertTrue(instance.getNotBlankName()!!.isNotBlank())
        Assertions.assertTrue(instance.getNotNullName()!=null)
        Assertions.assertTrue(instance.getNotEmptySet()!!.size>0)
    }

    @Test
    fun oneViolationDetected() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val map= mapOf(
                "notBlankName" to "  ",
                "notNullName" to "name2",
                "notEmptySet" to setOf("value")
        )
        try{
            factory.instantiate(ITest::class.java, map)
        }catch (ex: ImplicitException ) {
            when (ex) {
                is ImplicitViolations -> {
                    assertThat(ex.violations).hasSize(1)
                    assertThat(ex.message).isEqualTo("Implicit violations were detected: [value of field 'setNotBlankName' is empty]")
                }
                else -> Assertions.fail("Implicit violation is expected")
            }
        }
    }

    @Test
    fun multipleViolationsWhereDetected() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val map= mapOf(
                "notBlankName" to "  ",
                "notEmptySet" to setOf<String>()
        )
        try{
            factory.instantiate(ITest::class.java, map)
        }catch (ex: ImplicitException ) {
            when (ex) {
                is ImplicitViolations -> {
                    assertThat(ex.violations).hasSize(3)
                }
                else -> Assertions.fail("Implicit violation is expected")
            }
        }
    }




    interface ITest {
        fun setNotNullName(@NotNull notNullName: String?)
        fun getNotNullName(): String?

        fun setNotBlankName(@NotBlank notBlankName: String?)
        fun getNotBlankName(): String?

        fun setNotEmptySet(@NotEmpty notEmptySet: Set<Any>?)
        fun getNotEmptySet(): Set<Any>?

    }

}
