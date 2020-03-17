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

package implicit;

import implicit.annotation.validation.NotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class EqualsAndHashCodeTest {

    @Test
    fun testEqualsAndHashCode() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(IPojo::class.java)

        val pojo1 = supplier.get()
        pojo1.setPartitionKey(UUID.randomUUID().toString())
        pojo1.setSortingKey(UUID.randomUUID().toString())

        val pojo2 = supplier.get()
        pojo2.setPartitionKey(pojo1.getPartitionKey())
        pojo2.setSortingKey(pojo1.getSortingKey())

        val pojo3 = supplier.get()
        pojo3.setPartitionKey(UUID.randomUUID().toString())
        pojo3.setSortingKey(UUID.randomUUID().toString())

        Assertions.assertTrue(pojo1 == pojo2)
        Assertions.assertEquals(pojo1.hashCode(), pojo2.hashCode())

        Assertions.assertTrue(pojo1 != pojo3)
        Assertions.assertNotEquals(pojo1.hashCode(), pojo3.hashCode())
    }

    interface IPojo {
        fun getPartitionKey():String?
        fun setPartitionKey(@NotNull str: String?)
        fun getSortingKey(): String?
        fun setSortingKey(@NotNull str: String?)
    }

}
