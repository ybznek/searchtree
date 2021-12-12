package com.ybznek.searchTree.node

import com.ybznek.searchTree.SearchTree.SearchRef

internal sealed interface Node<V> {
    val value: V?
    val tree: Map<Char, Node<V>>

    open fun nextRootOrNode(str: String, index: Int, ref: SearchRef<V>) {

        if (index >= str.length) {
            ref.value = value
            return
        }
        val char = str[index]

        val node = tree[char] ?: return

        ref.node = node
        if (node !is PrefixTreeNodeBase) {
            ref.value = node.value
        }
    }
}

internal sealed interface ImmutableNode<V> : Node<V>

internal sealed class PrefixTreeNodeBase<V> constructor(internal val prefix: String) : ImmutableNode<V> {

    abstract val node: Node<V>?

    fun withExtraPrefix(prefix: Char): PrefixTreeNodeBase<V> {
        return withNewPrefix(prefix + this.prefix)
    }

    abstract fun withNewPrefix(newPrefix: String): PrefixTreeNodeBase<V>

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

internal class PrefixTreeNodeNodeAndValue<V>(prefix: String, override val node: Node<V>, override val value: V?) : PrefixTreeNodeBase<V>(prefix = prefix) {
    override fun withNewPrefix(newPrefix: String): PrefixTreeNodeBase<V> {
        return PrefixTreeNodeNodeAndValue(newPrefix, node, value)
    }

    override fun toString() = "PrefixTreeNodeNodeAndValue(str='$prefix', node=$node, value=$value)"
}

internal class PrefixTreeNodeNodeOnly<V> constructor(prefix: String, override val node: Node<V>?) : PrefixTreeNodeBase<V>(prefix) {

    override val value: V?
        get() = null

    override fun withNewPrefix(newPrefix: String): PrefixTreeNodeNodeOnly<V> {
        return PrefixTreeNodeNodeOnly(newPrefix, node)
    }

    override fun toString() = "PrefixTreeNodeNodeOnly(prefix='$prefix', node=$node)"
}

internal class PrefixTreeNodeValueOnly<V> constructor(prefix: String, override val value: V?) : PrefixTreeNodeBase<V>(prefix) {
    override val node: Node<V>?
        get() = null

    override fun withNewPrefix(newPrefix: String) = PrefixTreeNodeValueOnly(newPrefix, value)
    override fun toString() = "PrefixTreeNodeValueOnly(prefix='$prefix', value=$value)"
}

internal class PrefixTreeNodeUnitOnly constructor(prefix: String) : PrefixTreeNodeBase<Unit>(prefix) {
    override val node: Node<Unit>?
        get() = null

    override fun withNewPrefix(newPrefix: String) = PrefixTreeNodeUnitOnly(newPrefix)
    override fun toString() = "PrefixTreeNodeUnitOnly(prefix='$prefix')"
    override val value: Unit
        get() = Unit

    inline fun <V> type(): ImmutableNode<V> {
        @Suppress("UNCHECKED_CAST")
        return this as ImmutableNode<V>
    }
}

internal class ImmutableNodeGeneric<V>(
    override val value: V? = null,
    override val tree: Map<Char, Node<V>>
) : ImmutableNode<V> {
    override fun toString() = "ImmutableNodeGeneric(value=$value, tree=$tree)"
}

internal class MutableNode<V>(
    override var value: V? = null,
    override var tree: HashMap<Char, MutableNode<V>> = HashMap()
) : Node<V> {
    override fun toString() = "MutableNode(value=$value, tree=$tree)"
}

internal class ValueOnlyNode<V>(override val value: V?) : ImmutableNode<V> {
    override val tree: Map<Char, Node<V>>
        get() = emptyMap()

    override fun toString() = "ValueOnlyNode(value=$value)"
}

internal object UnitNode : ImmutableNode<Unit> {
    override val tree: Map<Char, Node<Unit>>
        get() = emptyMap()

    override val value
        get() = Unit

    fun <V> asTypedNode(): ImmutableNode<V> {
        @Suppress("UNCHECKED_CAST")
        return this as ImmutableNode<V>
    }

    override fun toString() = "UnitNode"
}

internal class TreeOnlyNode<V>(override var tree: Map<Char, Node<V>>) : ImmutableNode<V> {
    override val value: V?
        get() = null

    override fun toString() = "TreeOnlyNode(tree=$tree)"
}