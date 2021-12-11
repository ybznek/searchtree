package com.ybznek.searchTree

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.ybznek.searchTree.node.Node
import com.ybznek.searchTree.node.PrefixTreeNodeGeneral
import com.ybznek.searchTree.node.PrefixTreeNodeNodeOnly
import com.ybznek.searchTree.node.PrefixTreeNodeValueOnly
import com.ybznek.searchTree.node.TreeOnlyNode
import com.ybznek.searchTree.node.ValueOnlyNode
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.reflect.KClass

class SearchTreeTest {

    @Test
    fun testIndex() {
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
    fun testPrefixSimple() {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()
        searchTree.addKeyValue("123", "match")
        val o = searchTree.optimized() as ImmutableSearchTree<SearchTree.ValueWithKey<String>>
        val root = getChecked<PrefixTreeNodeValueOnly<SearchTree.ValueWithKey<String>>>(o.root)
        assertThat(root.prefix).isEqualTo("123")

        val afterPrefxNode = root.tree['1']!!.tree['2']!!.tree['3']
        val valueNode = getChecked<ValueOnlyNode<SearchTree.ValueWithKey<String>>>(afterPrefxNode)
        assertThat(valueNode.value!!.value).isEqualTo("match")
        o.searchBest("123")!!.testValue("match")
    }

    @Test
    fun testPrefixSplitInto2() {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()
        searchTree.addKeyValue("1234A", "match")
        searchTree.addKeyValue("1234B", "match2")
        val o = searchTree.optimized() as ImmutableSearchTree<SearchTree.ValueWithKey<String>>
        val root = getChecked<PrefixTreeNodeNodeOnly<SearchTree.ValueWithKey<String>>>(o.root)
        assertThat(root.prefix).isEqualTo("1234")

        val treeNode = getChecked<TreeOnlyNode<SearchTree.ValueWithKey<String>>>(root.tree['1']!!.tree['2']!!.tree['3']!!.tree['4'])

        o.searchBest("1234A")!!.let { value ->
            value.testValue("match")
            value.testIndex(0)
        }
        o.searchBest("1234B")!!.let { value ->
            value.testValue("match2")
            value.testIndex(0)
        }
    }

    @Test
    fun testPrefixValueAndSplit() {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()
        searchTree.addKeyValue("1234", "match")
        searchTree.addKeyValue("1234B", "match2")
        val o = searchTree.optimized() as ImmutableSearchTree<SearchTree.ValueWithKey<String>>
        val root = getChecked<PrefixTreeNodeGeneral<SearchTree.ValueWithKey<String>>>(o.root)
        assertThat(root.prefix).isEqualTo("1234")

        val treeNode = getChecked<TreeOnlyNode<SearchTree.ValueWithKey<String>>>(root.tree['1']!!.tree['2']!!.tree['3']!!.tree['4'])

        o.searchBest("1234")!!.testValue("match")
        o.searchBest("1234B")!!.testValue("match2")
    }

    @Test
    fun noPurePrefix() {
        val searchTree = MutableSearchTree<SearchTree.ValueWithKey<String>>()
        searchTree.addKeyValue("1", "match")
        searchTree.addKeyValue("12", "match2")
        searchTree.addKeyValue("123", "match3")
        val o = searchTree.optimized() as ImmutableSearchTree<SearchTree.ValueWithKey<String>>
        o.searchBest("1")!!.testValue("match")
        o.searchBest("12")!!.testValue("match2")
        o.searchBest("123")!!.testValue("match3")
    }


    private inline fun <reified T : Node<*>> getChecked(root: Node<*>?) = getChecked(root, T::class)

    private fun <T : Node<*>> getChecked(root: Node<*>?, kClass: KClass<T>): T {
        if (root == null) {
            assertThat(root).isNotNull()
            throw IllegalStateException()
        }
        val java = root::class.java
        assertThat(kClass.java.isAssignableFrom(java), name = "${java.simpleName} is instance of ${kClass.java.simpleName}").isTrue()
        return root as T
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

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>?.testIndex(int: Int) {
        if (this == null) {
            throwEmptyResult()
        } else {
            assertThat(this::index).isEqualTo(int)
        }
    }

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>?.testKey(str: String) {
        if (this == null) {
            throwEmptyResult()
        } else {
            assertThat(this.value::key).isEqualTo(str)
        }
    }

    fun SearchTree.Result<SearchTree.ValueWithKey<String>>?.testValue(str: String) {
        if (this == null) {
            throwEmptyResult()
        } else {
            assertThat(this.value::value).isEqualTo(str)
        }
    }

    private fun throwEmptyResult() {
        throw NullPointerException("Empty result")
    }

}