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
import implicit.marker.IMappable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ToMapTest {

    @Test
    fun testToMap() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier = factory.getSupplier(IPojo::class.java)

        val pojo = supplier.get()
        pojo.setPartitionKey(UUID.randomUUID().toString())
        pojo.setSortingKey(UUID.randomUUID().toString())

        Assertions.assertTrue(pojo is IMappable)
        Assertions.assertNotNull((pojo as IMappable).toMap())
    }

    interface IPojo {
        fun getPartitionKey():String?
        fun setPartitionKey(@NotNull str: String?)
        fun getSortingKey(): String?
        fun setSortingKey(@NotNull str: String?)
    }

}
