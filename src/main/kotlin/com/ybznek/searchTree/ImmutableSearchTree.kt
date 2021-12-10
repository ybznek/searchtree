package com.ybznek.searchTree

internal class ImmutableSearchTree<V : Any>(private val root: Node<V>) : SearchTree<V>() {
    override fun searchSequence(str: String, startSearchIndex: IntRange): Sequence<Result<V>> {
        return sequenceSearch(str, root, startSearchIndex)
    }

    override fun searchSequence(str: String, indices: Sequence<Int>): Sequence<Result<V>> {
        return sequenceSearch(str, root, indices)
    }

    override fun getValues(): List<Result<V>> {
        return this.getValues(root)
    }

}