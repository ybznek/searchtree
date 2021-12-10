package com.ybznek.searchTree

import com.ybznek.searchTree.node.ImmutableNode
import com.ybznek.searchTree.node.Node
import com.ybznek.searchTree.node.TreeNode
import com.ybznek.searchTree.node.ValueNode

internal object TreeOptimizer {
    private val unitNode = ValueNode(Unit)

    fun <V : Any> optimize(root: Node<V>): SearchTree<V> {
        return if (root.tree.isEmpty())
            @Suppress("UNCHECKED_CAST")
            EmptySearchTree as SearchTree<V>
        else
            ImmutableSearchTree(optimizeNode(root))
    }

    private fun <V> optimizeNode(node: Node<V>): Node<V> {
        if (node.tree.isEmpty()) {
            return createValueNode(node)
        }

        return when (node.tree.size) {
            1 -> createSingleBranchTree(node)
            else -> when (node.value) {
                null -> TreeNode(createMultiBranchTree(node))
                else -> ImmutableNode(node.value, createMultiBranchTree(node))
            }
        }
    }

    private fun <V> createValueNode(node: Node<V>): Node<V> {
        return when {
            node.value === Unit ->
                @Suppress("UNCHECKED_CAST")
                unitNode as Node<V>
            else ->
                ValueNode(node.value)
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

        return CharIntervalMap(min.toChar(), arr as Array<Node<V>>)
    }

    private fun <V> createSingleBranchTree(node: Node<V>): Node<V> {
        val entry = node.tree.entries.single()
        val entryValue = entry.value
        /*
        if (node.value == null) {
            if (entryValue is Node.PrefixTreeNode) {
                return Node.PrefixTreeNode(entry.key + entryValue.prefix, entryValue.node)
            } else if (entryValue.tree.size == 1) {

                val entr = entryValue.tree.entries.single()
                val optimizedLeaf = optimizeNode(entry.value)
                if (entr.value.value != null) {
                    if (optimizedLeaf is Node.PrefixTreeNode) {
                        return Node.PrefixTreeNode(entry.key.toString() + optimizedLeaf.prefix, optimizedLeaf.node)
                    } else {
                        return Node.PrefixTreeNode(entry.key.toString() + entr.key.toString(), entr.value)
                    }
                }

            }
        }*/
        val tree = mapOf(entry.key to optimizeNode(entryValue))
        return when (node.value) {
            null -> TreeNode(tree)
            else -> ImmutableNode(node.value, tree)
        }
    }
}

