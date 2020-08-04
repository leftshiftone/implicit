/*
 * Copyright (c) 2016-2019, Leftshift One
 * __________________
 * [2019] Leftshift One
 * All Rights Reserved.
 * NOTICE:  All information contained herein is, and remains
 * the property of Leftshift One and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Leftshift One
 * and its suppliers and may be covered by Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Leftshift One.
 */

package implicit.validation.validator

import implicit.Implicit
import implicit.annotation.validation.Max
import implicit.annotation.validation.Min
import implicit.exception.ImplicitValidationException
import implicit.exception.ImplicitViolations
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombinedLimitsValidatorTest {


    @Test
    fun instantiatingAnEntityWhichFieldsDoNotMatchTheLimits() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }

        val map = mapOf<String, Any?>("floatValue" to 4.0f, "integerValue" to 0f)
        try {
            factory.instantiate(IPojo::class.java, map)
            Assertions.fail<String>("Validation must fail because values do not respect the limits")
        } catch (ex: ImplicitViolations) {
            assertThat(ex.violations).hasSize(2)
            assertThat(ex.violations.get(0)).extracting{ it.message}.isEqualTo("value of field 'setFloatValue' is > 3.5")
            assertThat(ex.violations.get(1)).extracting{ it.message}.isEqualTo("value of field 'setIntegerValue' is < 1.0")
        }
    }

    @Test
    fun settingAValueWhichDoesNotMatchTheLimits() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(IPojo::class.java)
        val pojo = supplier.get()

        try {
            pojo.setFloatValue(6f)
            Assertions.fail<String>("Validation must fail because values do not respect the limits")
        } catch (ex: ImplicitValidationException) {
            assertThat(ex.message).isEqualTo("value of field 'setFloatValue' is > 3.5")
        }
        try {
            pojo.setIntegerValue(10)
            Assertions.fail<String>("Validation must fail because values do not respect the limits")
        } catch (ex: ImplicitValidationException) {
            assertThat(ex.message).isEqualTo("value of field 'setIntegerValue' is > 4.0")
        }


        try {
            pojo.setFloatValue(1f)
            Assertions.fail<String>("Validation must fail because values do not respect the limits")
        } catch (ex: ImplicitValidationException) {
            assertThat(ex.message).isEqualTo("value of field 'setFloatValue' is < 1.5")
        }
        try {
            pojo.setIntegerValue(0)
            Assertions.fail<String>("Validation must fail because values do not respect the limits")
        } catch (ex: ImplicitValidationException) {
            assertThat(ex.message).isEqualTo("value of field 'setIntegerValue' is < 1.0")
        }

    }


    interface IPojo {

        fun getFloatValue() : Float
        fun setFloatValue(@Min(1.5f) @Max(3.5f) number:Float)
        fun getIntegerValue() : Int
        fun setIntegerValue(@Min(1f) @Max(4f) number:Int)
    }

}
