package com.ybznek.searchTree.node

import com.ybznek.searchTree.SearchTree.SearchRef

internal sealed class Node<V> {
    abstract val value: V?
    abstract val tree: Map<Char, Node<V>>

    open fun nextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {

        if (index >= str.length) {
            ref.value = value
            return
        }
        val char = str[index]

        ref.node = tree[char]
        ref.value = tree[char]?.value
    }
}

internal sealed class ImmutableNode<V> : Node<V>() {
}

internal open class PrefixTreeNodeGeneral<V> constructor(internal val prefix: String, internal val node: Node<V>?, override val value: V?) : ImmutableNode<V>() {

    fun withExtraPrefix(prefix: Char): PrefixTreeNodeGeneral<V> {
        return withNewPrefix(prefix + this.prefix)
    }

    open fun withNewPrefix(newPrefix: String): PrefixTreeNodeGeneral<V> {
        return PrefixTreeNodeGeneral(newPrefix, node, value)
    }

    override val tree: Map<Char, Node<V>>
        get() {
            val nextNode = if (prefix.length == 1) {
                node
            } else {
                withNewPrefix(prefix.substring(1))
            }

            return if (nextNode == null) {
                mapOf(prefix[0] to ValueOnlyNode(value))
            } else {
                mapOf(prefix[0] to nextNode)
            }
        }

    override fun nextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {
        if (str.regionMatches(index, prefix, 0, prefix.length)) {
            ref.node = node
            ref.value = value
            ref.shift = prefix.length
        }
    }
}

internal class PrefixTreeNodeNodeOnly<V> constructor(prefix: String, node: Node<V>?) : PrefixTreeNodeGeneral<V>(prefix, node, null) {

    override fun withNewPrefix(newPrefix: String): PrefixTreeNodeNodeOnly<V> {
        return PrefixTreeNodeNodeOnly(newPrefix, node)
    }

    override fun nextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {
        if (str.regionMatches(index, prefix, 0, prefix.length)) {
            ref.node = node
            ref.shift = prefix.length
        }
    }
}

internal class PrefixTreeNodeValueOnly<V> constructor(prefix: String, override val value: V?) : PrefixTreeNodeGeneral<V>(prefix, null, value) {

    override fun withNewPrefix(newPrefix: String): PrefixTreeNodeValueOnly<V> {
        return PrefixTreeNodeValueOnly(newPrefix, value)
    }

    override fun nextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {
        if (str.regionMatches(index, prefix, 0, prefix.length)) {
            ref.value = value
            ref.shift = prefix.length
        }
    }
}


internal class ImmutableNodeGeneric<V>(
    override val value: V? = null,
    override val tree: Map<Char, Node<V>>
) : ImmutableNode<V>()

internal class MutableNode<V>(
    override var value: V? = null,
    override var tree: HashMap<Char, MutableNode<V>> = HashMap()
) : Node<V>()

internal class ValueOnlyNode<V>(override val value: V?) : ImmutableNode<V>() {
    override val tree: Map<Char, Node<V>>
        get() = emptyMap()
}

internal object UnitNode : ImmutableNode<Unit>() {
    override val tree: Map<Char, Node<Unit>>
        get() = emptyMap()
    override val value
        get() = Unit

    fun <V> asTypedNode(): ImmutableNode<V> {
        @Suppress("UNCHECKED_CAST")
        return this as ImmutableNode<V>
    }
}

internal class TreeOnlyNode<V>(override var tree: Map<Char, Node<V>>) : ImmutableNode<V>() {
    override val value: V?
        get() = null
}