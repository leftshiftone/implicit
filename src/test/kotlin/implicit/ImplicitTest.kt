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
import java.util.*

class ImplicitTest {

    @Test
    fun test() {
        val factory = Implicit { "implicit.test.implicit.${it.simpleName}" }
        val supplier = factory.getSupplier(IPojo::class.java)

        val pojo = supplier.get()
        pojo.setPartitionKey(UUID.randomUUID().toString())
        pojo.setSortingKey(UUID.randomUUID().toString())

        pojo::class.java.declaredFields.forEach(System.out::println)

        Assertions.assertNotNull(pojo.getPartitionKey())
        Assertions.assertNotNull(pojo.getSortingKey())
        Assertions.assertTrue(pojo::class.java.annotations.map { it.annotationClass.simpleName }.contains("Entity"))
    }

    @Test
    fun `map initialization`() {
        val factory = Implicit { "implicit.test.implicit.${it.simpleName}" }
        val function = factory.getFunction(IPojo::class.java)

        val pojo = function.apply(mapOf("partitionKey" to UUID.randomUUID().toString(),
                "sortingKey" to UUID.randomUUID().toString()))

        Assertions.assertNotNull(pojo.getPartitionKey())
        Assertions.assertNotNull(pojo.getSortingKey())
        Assertions.assertTrue(pojo::class.java.annotations.map { it.annotationClass.simpleName }.contains("Entity"))
    }

    @Test
    fun `create one million instances`() {
        val factory = Implicit { "implicit.test.implicit.${it.simpleName}" }
        val supplier = factory.getSupplier(factory.create(IPojo::class.java))

        (1..1000000).forEach {
            val pojo = supplier.get()
            pojo.setPartitionKey(it.toString())
            pojo.setSortingKey(it.toString())

            Assertions.assertNotNull(pojo.getPartitionKey())
            Assertions.assertNotNull(pojo.getSortingKey())
        }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    annotation class Entity(val name:String)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class PartitionKey()

    @Entity("EntityName")
    interface IPojo {
        // @PartitionKey
        fun getPartitionKey(): String
        fun setPartitionKey(str: String)

        fun getSortingKey(): String
        fun setSortingKey(str: String): Unit
    }

}
