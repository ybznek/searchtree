package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test

internal class TranslationBuilderTest {

    @Test
    fun emptyTree() {
        val tree = TranslationBuilder.build { }

        assertThat(tree.getValues())
            .isEmpty()
    }

}