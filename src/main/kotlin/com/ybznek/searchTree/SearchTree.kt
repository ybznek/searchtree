package com.ybznek.searchTree

import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Allows to search multiple strings in one large string
 */
public abstract class SearchTree<V : Any> {

    class ValueWithKey<V>(
        /**
         * Search key
         */
        val key: String,
        /**
         * Referenced value
         */
        val value: V
    )

    class Result<V>(
        /**
         * Index in string when key matches
         */
        val index: Int,
        /**
         * Referenced value
         */
        val value: V
    ) {
        override fun toString() = value.toString()
    }

    abstract fun searchSequence(str: String, startSearchIndex: IntRange = str.indices): Sequence<Result<V>>;
    abstract fun searchSequence(str: String, indices: Sequence<Int>): Sequence<Result<V>>;
    abstract fun getValues(): List<Result<V>>;

    internal open fun <V> sequenceSearch(str: String, root: Node<V>, startSearchIndex: IntRange): Sequence<Result<V>> {
        val first = max(0, startSearchIndex.first)
        val last = min(str.length - 1, startSearchIndex.last)
        val range = first..last

        return sequence<Result<V>> {
            val ref = SearchRef<V>()
            for (i in range) {
                searchInternal(i, root, ref, str)
            }
        }
    }

    internal fun <V> sequenceSearch(str: String, root: Node<V>, startIndexes: Sequence<Int>): Sequence<Result<V>> {
        return sequence<Result<V>> {
            val ref = SearchRef<V>()
            for (i in startIndexes) {
                searchInternal(i, root, ref, str)
            }
        }
    }

    private suspend fun <V> SequenceScope<Result<V>>.searchInternal(
        index: Int,
        root: Node<V>,
        ref: SearchRef<V>,
        str: String
    ) {
        var searchIndex = index
        var node = root

        do {
            ref.reset()
            node.getNextRootOrNode(str, searchIndex, ref)

            if (ref.value == null && ref.node == null) {
                break
            }
            if (ref.node != null) {
                node = ref.node as Node<V>
            }
            if (ref.value != null) {
                yield(Result(searchIndex, ref.value!!))
            }

            searchIndex += ref.shift


        } while (searchIndex < str.length)
    }

    internal fun getValues(root: Node<V>): List<Result<V>> {
        val list = ArrayList<Result<V>>()

        fun addToListRecursively(node: Node<V>) {
            val value = node.value
            if (value != null) {
                list.add(Result(-1, value))
            }
            for (newNode in node.tree.values) {
                if ((newNode as Node<V>?) != null) {
                    addToListRecursively(newNode)
                }
            }
        }

        addToListRecursively(root)

        return list;
    }

    internal class SearchRef<V> {
        var shift: Int = 1
        var node: Node<V>? = null
        var value: V? = null

        fun reset() {
            shift = 1
            node = null
            value = null
        }
    }
}

fun <V> SearchTree<SearchTree.ValueWithKey<V>>.searchBestValue(str: String): V? {
    return when (val best = this.searchBest(str)) {
        null -> null
        else -> best.value.value
    }
}

fun <V> SearchTree<SearchTree.ValueWithKey<V>>.searchBest(key: String): SearchTree.Result<SearchTree.ValueWithKey<V>>? {
    return this
        .searchSequence(key)
        .maxByOrNull { x -> x.value.key.length }
}