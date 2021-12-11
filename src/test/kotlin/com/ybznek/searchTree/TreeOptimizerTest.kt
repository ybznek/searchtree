package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ybznek.searchTree.SearchTree.SearchRef
import com.ybznek.searchTree.node.ImmutableNode
import com.ybznek.searchTree.node.PrefixTreeNode
import org.junit.jupiter.api.Test

class TreeOptimizerTest {

    @Test
    fun prefixValidNode() {
        val nextNode = ImmutableNode(null, mapOf('3' to ImmutableNode(3, emptyMap())))
        val prefixNode = PrefixTreeNode("prefix", nextNode)
        val ref = SearchRef<Int>()
        prefixNode.getNextRootOrNode("012prefix", 3, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(nextNode)
        assertThat(ref.shift).isEqualTo(6)
    }

    @Test
    fun prefixInvalid() {
        val nextNode = ImmutableNode(null, mapOf('3' to ImmutableNode(3, emptyMap())))
        val prefixNode = PrefixTreeNode("prefix", nextNode)
        val ref = SearchRef<Int>()
        prefixNode.getNextRootOrNode("012prefix", 2, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(null)

    }
}