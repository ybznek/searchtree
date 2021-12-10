package com.ybznek.searchTree

import com.ybznek.searchTree.node.MutableNode
import com.ybznek.searchTree.node.Node

class MutableSearchTree<V : Any> : SearchTree<V>() {
    private val root = MutableNode<V>()

    fun add(key: String, value: V) {
        check(value !is Node<*>) { "value cannot be instance of Node" }
        check(key.isNotEmpty()) { "Empty keys are not supported" }
        var prevNode = root
        for (c in key) {
            prevNode = prevNode.tree.computeIfAbsent(c) { MutableNode<V>() }
        }
        prevNode.value = value
    }

    override fun searchSequence(str: String, startSearchIndex: IntRange): Sequence<Result<V>> {
        return sequenceSearch(str, root, startSearchIndex)
    }

    fun optimized(): SearchTree<V> {
        return TreeOptimizer.optimize(this.root)
    }

    override fun getValues(): List<Result<V>> {
        return this.getValues(root)
    }

    override fun searchSequence(str: String, indices: Sequence<Int>): Sequence<Result<V>> {
        return sequenceSearch(str, root, indices)
    }
}

fun <V> MutableSearchTree<SearchTree.ValueWithKey<V>>.addKeyValue(kv: SearchTree.ValueWithKey<V>) {
    this.add(kv.key, kv)
}



