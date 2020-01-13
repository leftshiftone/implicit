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

package implicit.validation.validator;

import implicit.Implicit
import implicit.annotation.validation.Pattern
import implicit.exception.ImplicitException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class RegexValidatorTest {

    @Test
    fun test() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(IPojo::class.java)

        val counter = AtomicInteger()

        val pojo = supplier.get()

        try {
            pojo.setEmail(null)
        } catch (ex:ImplicitException) {
            counter.incrementAndGet()
        }
        try {
            pojo.setEmail("abcd")
        } catch (ex:ImplicitException) {
            counter.incrementAndGet()
        }

        Assertions.assertEquals(counter.get(), 2)
    }

    interface IPojo {
        fun setEmail(@Pattern("[a-z]*@[a-z]*\\.[a-z]*") str: String?)
    }

}
