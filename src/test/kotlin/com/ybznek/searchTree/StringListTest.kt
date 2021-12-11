package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class StringListTest {

    @Test
    fun isNotIn() {
        val list = StringList.of("monkey", "wallet")
        assertThat(list isNotIn "test | notebooks | wallet").isFalse()
    }

    @Test
    fun empty() {
        assertThat(StringList.EMPTY isIn "").isFalse()
        assertThat(StringList.of() isIn "").isFalse()
        assertThat(StringList.ofLowerCase() isIn "").isFalse()
    }

    @Test
    fun emptySingleton() {
        assertThat(StringList.EMPTY === StringList.of()).isTrue()
        assertThat(StringList.EMPTY === StringList.ofLowerCase()).isTrue()

    }

    @Test
    fun isIn() {
        val list = StringList.of("monkey", "notebooks")
        assertThat(list isIn "test | notebooks | something").isTrue()
    }

    @Test
    fun isInIfNotIn() {
        val list = StringList.of("monkey", "aligator")
        assertThat(list isIn "test | notebooks | something").isFalse()
    }

    @Test
    fun isPrefixOf() {
        val list = StringList.of("monkey", "notebooks", "test", "ono", "monky")
        assertThat(list isPrefixOf "test | notebooks | something").isTrue()
    }

    @Test
    fun isNotPrefixOf() {
        val list = StringList.of("monkey", "notebooks")
        assertThat(list isPrefixOf "test | notebooks | something").isFalse()
    }

    @Test
    fun testIsInWithCommonPrefix() {
        val list = StringList.of("notebook", "notebooks")
        assertThat(list isIn " notebooks | something").isTrue()

        val list2 = StringList.of("notebook")
        assertThat(list2 isIn " notebooks | something").isTrue()

        val list3 = StringList.of("notebooks")
        assertThat(list3 isIn " notebooks | something").isTrue()

        val list4 = StringList.of("notebooks")
        assertThat(list4 isIn " notebook | something").isFalse()
    }

}