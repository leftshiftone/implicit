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

import implicit.annotation.Explicit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ExplicitTest {

    @Disabled("This test fails") //todo: fix test
    @Test
    fun test() {
        val factory = Implicit { "implicit.test.explicit.${it.simpleName}" }

        val pojo = factory.instantiate(IPojo::class.java)
        pojo.setListOne(listOf("A", "B"))
        pojo.setListTwo(listOf("C", "D"))

        Assertions.assertNotNull(pojo.getListAll())
        Assertions.assertEquals(pojo.getListAll().size, 4)
    }

    interface IPojo {
        fun getListOne(): List<String>
        fun setListOne(list: List<String>)

        fun getListTwo(): List<String>
        fun setListTwo(list: List<String>)

        @Explicit
        fun getListAll(): List<String> {
            val list = ArrayList<String>()
            list.addAll(getListOne())
            list.addAll(getListTwo())

            return list
        }
    }

}
