package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class EqualObjectPoolTest {
    @Test
    fun sanityCheck() {
        val strA = buildString()
        val strB = buildString()

        assertThat(strA == strB).isTrue()
        assertThat(strA === strB).isFalse()
    }

    @Test
    fun poolTest() {
        val pool = EqualObjectPool()
        val strA = pool[buildString()]
        val strB = pool[buildString()]

        assertThat(strA == strB).isTrue()
        assertThat(strA === strB).isTrue()
    }

    @Test
    fun poolTestOverride() {
        val pool = EqualObjectPool()
        val strA = pool[buildString()]
        val strB = pool[buildString()]
        val strC = pool[buildString()]


        assertThat(strA === strB).isTrue()
        assertThat(strA === strC).isTrue()
    }

    private fun buildString() = StringBuilder().append("a").append("b").toString()
}