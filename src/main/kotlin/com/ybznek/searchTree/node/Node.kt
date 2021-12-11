package com.ybznek.searchTree.node

import com.ybznek.searchTree.SearchTree.SearchRef

internal sealed class Node<V> {
    abstract val value: V?
    abstract val tree: Map<Char, Node<V>>

    open fun getNextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {

        if (index >= str.length) {
            ref.value = value
            return
        }
        val char = str[index]

        ref.node = tree[char]
        ref.value = tree[char]?.value
    }
}

internal class PrefixTreeNode<V> constructor(internal val prefix: String, internal val node: Node<V>?) : Node<V>() {

    fun withExtraPrefix(prefix: Char): PrefixTreeNode<V> {
        return PrefixTreeNode(prefix + this.prefix, node)
    }

    override val tree: Map<Char, Node<V>>
        get() {
            val nextNode = if (prefix.length == 1) {
                node ?: return emptyMap()
            } else {
                PrefixTreeNode(prefix.substring(1), node)
            }

            return mapOf(prefix[0] to nextNode)
        }

    override fun getNextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {
        if (str.regionMatches(index, prefix, 0, prefix.length)) {
            ref.node = node
            //ref.value = value
            ref.shift = str.length - index
        }
    }

    override val value: V?
        get() = null
}

internal class ImmutableNode<V>(
    override val value: V? = null,
    override val tree: Map<Char, Node<V>>
) : Node<V>()

internal class MutableNode<V>(
    override var value: V? = null,
    override var tree: HashMap<Char, MutableNode<V>> = HashMap()
) : Node<V>()

internal class ValueNode<V>(override val value: V?) : Node<V>() {
    override val tree: Map<Char, Node<V>>
        get() = emptyMap()
}

internal class TreeNode<V>(override var tree: Map<Char, Node<V>>) : Node<V>() {
    override val value: V?
        get() = null
}