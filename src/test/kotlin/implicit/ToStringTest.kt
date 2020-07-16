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

import implicit.annotation.generator.ToString
import implicit.annotation.validation.NotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ToStringTest {

    @Test
    fun testToString() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplierA = factory.getSupplier(IPojoA::class.java)
        val supplierB = factory.getSupplier(IPojoB::class.java)

        val pojo1 = supplierA.get()
        pojo1.setPartitionKey("A")
        pojo1.setSortingKey("B")

        val pojo2 = supplierB.get()
        pojo2.setPartitionKey("A")
        pojo2.setSortingKey("B")

        Assertions.assertEquals(pojo1.toString(), "IPojoA(partitionKey=A, sortingKey=B)")
        Assertions.assertEquals(pojo2.toString(), "IPojoB()")
    }

    interface IPojoA {
        fun getPartitionKey():String?
        fun setPartitionKey(@NotNull str: String?)
        fun getSortingKey(): String?
        fun setSortingKey(@NotNull str: String?)
    }

    interface IPojoB {
        @ToString(exclude = true)
        fun getPartitionKey():String?
        fun setPartitionKey(@NotNull str: String?)
        @ToString(exclude = true)
        fun getSortingKey(): String?
        fun setSortingKey(@NotNull str: String?)
    }

}
