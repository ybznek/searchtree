package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class TreeOptimizerTest {

    @Test
    fun prefixValidNode() {
        val nextNode = Node.ImmutableNode(null, mapOf('3' to Node.ImmutableNode(3, emptyMap())))
        val prefixNode = Node.PrefixTreeNode("prefix", nextNode)
        val ref = SearchTree.SearchRef<Int>()
        prefixNode.getNextRootOrNode("012prefix", 3, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(nextNode)
        assertThat(ref.shift).isEqualTo(6)
    }

    @Test
    fun prefixInvalid() {
        val nextNode = Node.ImmutableNode(null, mapOf('3' to Node.ImmutableNode(3, emptyMap())))
        val prefixNode = Node.PrefixTreeNode("prefix", nextNode)
        val ref = SearchTree.SearchRef<Int>()
        prefixNode.getNextRootOrNode("012prefix", 2, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(null)

    }
}