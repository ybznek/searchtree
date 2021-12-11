package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ybznek.searchTree.SearchTree.SearchRef
import com.ybznek.searchTree.node.ImmutableNode
import com.ybznek.searchTree.node.ImmutableNodeGeneric
import com.ybznek.searchTree.node.PrefixTreeNodeNodeOnly
import org.junit.jupiter.api.Test

class TreeOptimizerTest {

    @Test
    fun prefixValidNode() {
        val nextNode = ImmutableNodeGeneric(null, mapOf('3' to ImmutableNodeGeneric(3, emptyMap())))
        val prefixNode = PrefixTreeNodeNodeOnly("prefix", nextNode)
        val ref = SearchRef<Int>()
        prefixNode.nextRootOrNode("012prefix", 3, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(nextNode)
        assertThat(ref.shift).isEqualTo(6)
    }

    @Test
    fun prefixInvalid() {
        val nextNode = ImmutableNodeGeneric(null, mapOf('3' to ImmutableNodeGeneric(3, emptyMap())))
        val prefixNode = PrefixTreeNodeNodeOnly("prefix", nextNode)
        val ref = SearchRef<Int>()
        prefixNode.nextRootOrNode("012prefix", 2, ref)
        assertThat(ref.value).isEqualTo(null)
        assertThat(ref.node).isEqualTo(null)

    }
}