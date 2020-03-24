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

package implicit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

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

    @Test
    fun `can convert types if necessary`() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IAnotherPojo::class.java)

        val instance = function.apply(mapOf(
                "partitionKey" to "abc",
                "sortingKey" to "xyz",
                "myCoolNumber" to BigDecimal(1),
                "someMap" to mapOf("hello" to "world"),
                "someList" to listOf(1,2,3,4)
                ))

        Assertions.assertEquals(instance.getMyCoolNumber(), 1)
    }

    interface IPojo {
        // @PartitionKey
        fun getPartitionKey(): String
        fun setPartitionKey(str: String)

        fun getSortingKey(): String
        fun setSortingKey(str: String): Unit
    }

    interface IAnotherPojo {
        // @PartitionKey
        fun getPartitionKey(): String
        fun setPartitionKey(str: String)

        fun getSortingKey(): String
        fun setSortingKey(str: String): Unit

        fun getMyCoolNumber(): Int
        fun setMyCoolNumber(i: Int)

        fun getSomeMap(): Map<String, String>
        fun setSomeMap(m: Map<String, String>)

        fun getSomeList(): List<Int>
        fun setSomeList(l: List<Int>)
    }
}
