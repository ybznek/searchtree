package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ybznek.searchTree.node.PrefixTreeNode
import com.ybznek.searchTree.node.ValueNode
import org.junit.jupiter.api.Test

class SearchTreeTest {

    @Test
    fun prefixValidNode() {
        testTree(
            init = {
                addKeyValue("pre", "match")
            },
            test = {
                val b = searchBest("pre")!!
                b.testKey("pre")
                b.testValue("match")
                b.testIndex(0)
            }
        )
    }


    @Test
    fun testPrefix() {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()
        searchTree.addKeyValue("pr", "short match")
        searchTree.addKeyValue("pre", "match")
        val o = searchTree.optimized() as ImmutableSearchTree<SearchTree.ValueWithKey<String>>
        assertThat(o.root is PrefixTreeNode).isEqualTo(true)
        val root = (o.root as PrefixTreeNode)
        assertThat(root.prefix).isEqualTo("pr")
        val actual = root.node!!.tree['e']
        assertThat(actual is ValueNode).isEqualTo(true)
        assertThat(actual!!.value).isEqualTo("match")
        assertThat(o.searchBest("pr")!!.value.value).isEqualTo("short match")


    }

    @Test
    fun prefixNodeBetter() {
        testTree(
            init = {
                addKeyValue("pr", "short match")
                addKeyValue("pre", "match")
            },
            test = {
                val b = searchBest("pre")!!
                b.testKey("pre")
                b.testValue("short match")
                b.testIndex(0)
            }
        )
    }

    private fun testTree(init: MutableSearchTree<SearchTree.ValueWithKey<String>>.() -> Unit, test: SearchTree<SearchTree.ValueWithKey<String>>.() -> Unit) {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()

        init(searchTree)

        val e = mapOf(
            "mutable" to searchTree,
            "immutable" to searchTree.optimized()
        )
        for ((key, tree) in e) {
            println(key)
            test(tree)
        }
    }

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>.testIndex(int: Int) {
        assertThat(this::index).isEqualTo(int)
    }

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>.testKey(str: String) {
        assertThat(this.value::key).isEqualTo(str)
    }

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>.testValue(str: String) {
        assertThat(this.value::value).isEqualTo(str)
    }

}