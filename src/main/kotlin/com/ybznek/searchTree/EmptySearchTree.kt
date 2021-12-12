package com.ybznek.searchTree

internal object EmptySearchTree : SearchTree<Any>() {
    override fun searchSequence(str: String, startSearchIndex: IntRange): Sequence<Result<Any>> = emptySequence()
    override fun searchSequence(str: String, indices: Sequence<Int>): Sequence<Result<Any>> = emptySequence()
    override fun getValues(): List<Result<Any>> = emptyList()
    override fun toString() = "EmptySearchTree"
}