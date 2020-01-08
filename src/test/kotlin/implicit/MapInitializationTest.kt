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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MapInitializationTest {

    @Test
    fun test() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val clazz = factory.create(IPojo::class.java)

        val instance = clazz.getDeclaredConstructor(Map::class.java)
                .newInstance(mapOf("partitionKey" to "abc", "sortingKey" to "xyz"))

        Assertions.assertEquals(instance.getPartitionKey(), "abc")
        Assertions.assertEquals(instance.getSortingKey(), "xyz")
    }

    @Test
    fun test2() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IPojo::class.java)

        val instance = function.apply(mapOf("partitionKey" to "abc", "sortingKey" to "xyz"))

        Assertions.assertEquals(instance.getPartitionKey(), "abc")
        Assertions.assertEquals(instance.getSortingKey(), "xyz")
    }

    interface IPojo {
        // @PartitionKey
        fun getPartitionKey(): String
        fun setPartitionKey(str: String)

        fun getSortingKey(): String
        fun setSortingKey(str: String): Unit
    }

}
