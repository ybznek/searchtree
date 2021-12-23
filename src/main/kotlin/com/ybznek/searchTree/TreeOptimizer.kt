package com.ybznek.searchTree

import com.ybznek.searchTree.node.ImmutableNode
import com.ybznek.searchTree.node.ImmutableNodeGeneric
import com.ybznek.searchTree.node.Node
import com.ybznek.searchTree.node.PrefixTreeNodeBase
import com.ybznek.searchTree.node.PrefixTreeNodeNodeAndValue
import com.ybznek.searchTree.node.PrefixTreeNodeNodeOnly
import com.ybznek.searchTree.node.PrefixTreeNodeUnitOnly
import com.ybznek.searchTree.node.PrefixTreeNodeValueOnly
import com.ybznek.searchTree.node.TreeOnlyNode
import com.ybznek.searchTree.node.UnitNode
import com.ybznek.searchTree.node.ValueOnlyNode

internal class TreeOptimizer private constructor(private val optimizeValues: Boolean) {
    private val equalObjectPool = EqualObjectPool()

    private fun <V : Any> optimize(root: Node<V>): SearchTree<V> {
        return if (root.tree.isEmpty())
            @Suppress("UNCHECKED_CAST")
            EmptySearchTree as SearchTree<V>
        else
            ImmutableSearchTree(optimizeNode(root))
    }

    fun <V : Any?> optimizeValue(value: V): V {
        if (!optimizeValues)
            return value

        return when (value) {
            is SearchTree.ValueWithKey<*> -> {
                val kvKey = value.key
                val kvValue = value.value
                equalObjectPool[SearchTree.ValueWithKey(equalObjectPool[kvKey], equalObjectPool[kvValue]) as V]
            }
            else -> equalObjectPool[value]
        }
    }

    private fun <V> optimizeNode(node: Node<V>): ImmutableNode<V> {
        fun <V> optimizeNodeInternal(node: Node<V>): ImmutableNode<V> {
            if (node.tree.isEmpty()) {
                return createValueNode(node)
            }

            return when (node.tree.size) {
                1 -> createSingleBranchTree(node)
                else -> when (node.value) {
                    null -> TreeOnlyNode(createMultiBranchTree(node))
                    else -> ImmutableNodeGeneric(node.value, createMultiBranchTree(node))
                }
            }
        }

        return equalObjectPool[optimizeNodeInternal(node)]
    }

    private fun <V> createValueNode(node: Node<V>): ImmutableNode<V> {
        return when {
            node.value === Unit -> UnitNode.asTypedNode()
            else -> ValueOnlyNode(node.value)
        }
    }

    private fun <V> createMultiBranchTree(node: Node<V>): Map<Char, Node<V>> {
        val tree = node.tree
        val keys = tree.keys
        val min = keys.minOrNull()!!.code
        val max = keys.maxOrNull()!!.code
        val count = keys.size

        val expectedByteSizeOfHashMap = (32 * count + 4 * (count * 1.33)).toInt()
        val rangeSize = max - min + 1
        val expectedByteSizeOfCharIntervalMap = rangeSize * 4 // entry ~ 4 bytes reference, but all holes needs to be defined

        // use interval map only if it is more/same effective by space as hashmap
        return if (expectedByteSizeOfCharIntervalMap <= expectedByteSizeOfHashMap)
            buildCharIntervalMap(rangeSize, tree, min)
        else
            buildHashMap(tree)
    }

    private fun <V> buildHashMap(tree: Map<Char, Node<V>>): HashMap<Char, Node<V>> {
        return tree.entries.associateByTo(
            HashMap(tree.entries.size),
            { x -> x.key },
            { x -> optimizeNode(x.value) }
        )
    }

    private fun <V> buildCharIntervalMap(rangeSize: Int, tree: Map<Char, Node<V>>, min: Int): CharIntervalMap<Node<V>> {
        val arr = arrayOfNulls<Node<V>>(rangeSize)
        for (i in 0 until rangeSize) {
            arr[i] = when (val gotNode = tree[(min + i).toChar()]) {
                null -> null
                else -> optimizeNode(gotNode)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return CharIntervalMap(min.toChar(), arr as Array<Node<V>>)
    }

    private fun <V> createSingleBranchTree(node: Node<V>): ImmutableNode<V> {
        val entry = node.tree.entries.single()

        val optimizedChildNode = optimizeNode(entry.value)
        return createSingleBranch(optimizeValue(node.value), optimizedChildNode, entry.key)
    }

    private fun <V> createSingleBranch(currentValue: V?, optimizedChildNode: ImmutableNode<V>, childPrefix: Char): ImmutableNode<V> {

        if (currentValue == null) {
            when (optimizedChildNode) {
                is PrefixTreeNodeBase -> return optimizedChildNode.withNewPrefix(equalObjectPool[childPrefix + optimizedChildNode.prefix])
                is ValueOnlyNode -> return PrefixTreeNodeValueOnly(equalObjectPool[childPrefix.toString()], optimizeValue(optimizedChildNode.value))
                is UnitNode -> return PrefixTreeNodeUnitOnly(equalObjectPool[childPrefix.toString()]).type()

                is TreeOnlyNode -> {
                    val tree = optimizedChildNode.tree.entries
                    if (tree.size == 1) {
                        val f = tree.first()
                        when (f.value) {
                            is ValueOnlyNode -> return PrefixTreeNodeValueOnly(equalObjectPool[childPrefix.toString() + f.key], optimizeValue(f.value.value))
                            is UnitNode -> return PrefixTreeNodeUnitOnly(equalObjectPool[childPrefix.toString() + f.key]).type()
                            is TreeOnlyNode -> return PrefixTreeNodeNodeOnly(equalObjectPool[childPrefix.toString() + f.key], TreeOnlyNode(f.value.tree))
                            else -> Unit
                        }

                    }
                }
                is ImmutableNodeGeneric -> {
                    val tree = optimizedChildNode.tree.entries
                    if (tree.size == 1) {
                        val f = tree.first()
                        when (f.value) {
                            is ValueOnlyNode, is UnitNode -> return PrefixTreeNodeNodeAndValue(equalObjectPool[childPrefix.toString()], TreeOnlyNode(mapOf(f.key to f.value)), optimizeValue(optimizedChildNode.value))
                            else -> Unit
                        }

                    }
                }
                is UnitNode -> Unit
            }
        }

        val tree = mapOf(childPrefix to optimizedChildNode)
        return when (currentValue) {
            null -> TreeOnlyNode(tree)
            else -> ImmutableNodeGeneric(currentValue, tree)
        }
    }


    companion object {
        fun <V : Any> optimize(root: Node<V>, optimizeValues: Boolean = true): SearchTree<V> {
            val treeOptimizer = TreeOptimizer(optimizeValues)
            val optimize = treeOptimizer.optimize(root)
            return optimize
        }
    }
}

