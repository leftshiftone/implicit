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

import implicit.annotation.generator.GenericType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapInitializationWithGenericsTest {

    @Test
    fun `can convert types with list of nested types`() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IPojoAlpha::class.java)
        val instance = function.apply(mapOf(
                "partitionKey" to "abc",
                "contentList" to listOf(mapOf("partitionKey" to "123"), mapOf("partitionKey" to "456"))
        ))

        assertThat(instance.getContentList()).hasSize(2)
        assertThat(instance.getContentList()).extracting("partitionKey").contains("123","456")
    }

    @Test
    fun `can convert types with set of nested types`() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IPojoAlpha::class.java)
        val instance = function.apply(mapOf(
                "partitionKey" to "abc",
                "contentSet" to setOf(mapOf("partitionKey" to "ABC"), mapOf("partitionKey" to "DEF"))
        ))
        assertThat(instance.getContentSet()).hasSize(2)
        assertThat(instance.getContentSet()).extracting("partitionKey").contains("ABC","DEF")
    }

    @Test
    fun `can convert types with array of nested types (SET)`() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IPojoAlpha::class.java)
        val instance = function.apply(mapOf(
                "partitionKey" to "abc",
                "contentSet" to arrayOf(mapOf("partitionKey" to "ABC"), mapOf("partitionKey" to "DEF"))
        ))
        assertThat(instance.getContentSet()).hasSize(2)
        assertThat(instance.getContentSet()).extracting("partitionKey").contains("ABC","DEF")
    }



    @Test
    fun `can convert types with array of nested types (LIST)`() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val function = factory.getFunction(IPojoAlpha::class.java)
        val instance = function.apply(mapOf(
                "partitionKey" to "abc",
                "contentList" to arrayOf(mapOf("partitionKey" to "123"), mapOf("partitionKey" to "456"))
        ))
        assertThat(instance.getContentList()).hasSize(2)
        assertThat(instance.getContentList()).extracting("partitionKey").contains("123","456")
    }

    interface IPojoAlpha {
        fun getContentList(): List<IPojoBeta>
        @GenericType(IPojoBeta::class)
        fun setContentList(content: List<IPojoBeta>)

        fun getContentSet(): Set<IPojoBeta>
        @GenericType(IPojoBeta::class)
        fun setContentSet(content: Set<IPojoBeta>)

    }

    interface IPojoBeta {
        fun getPartitionKey(): String
        fun setPartitionKey(str: String)
    }
}
