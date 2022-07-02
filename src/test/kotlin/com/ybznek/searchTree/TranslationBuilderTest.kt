package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class TranslationBuilderTest {

    @Test
    fun emptyTree() {
        val tree = TranslationBuilder.build { }

        assertThat(tree.getValues())
            .isEmpty()
    }

    @Test
    fun testEmptyInput() {
        val tree = TranslationBuilder.build {
            from() to "str3"
        }

        assertThat(tree.getValues())
            .isEmpty()
    }

    @Test
    fun testTranslation() {
        val tree = TranslationBuilder.build {
            from("str1", "str2") to "str3"
        }

        val result = tree
            .searchSequence(" str1 str2 str3 str4")
            .map { x -> x.value }
            .toList()

        assertThat(result).isEqualTo(
            listOf(
                SearchTree.ValueWithKey("str1", "str3"),
                SearchTree.ValueWithKey("str2", "str3"),
            )
        )
    }
}