package com.ybznek.searchTree

/**
 * Provides search structure for strings
 */
class StringList private constructor(private val tree: SearchTree<Unit>) {

    infix fun isNotIn(inputString: String): Boolean {
        return tree.searchSequence(inputString).none()
    }

    infix fun isIn(inputString: String): Boolean {
        return tree.searchSequence(inputString).any()
    }

    infix fun isPrefixOf(inputString: String): Boolean {
        return tree.searchSequence(inputString, 0..0).any()
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        val EMPTY = StringList(EmptySearchTree as SearchTree<Unit>)

        fun ofLowerCase(vararg text: String): StringList {
            return of(text, toLowerCase = true)
        }

        fun of(vararg text: String): StringList {
            return of(text, toLowerCase = false)
        }

        private fun of(text: Array<out String>, toLowerCase: Boolean): StringList {
            if (text.isEmpty())
                return EMPTY

            val tree = MutableSearchTree<Unit>()
            if (toLowerCase) {
                for (t in text) {
                    tree.add(t.lowercase(), Unit)
                }
            } else {
                for (t in text) {
                    tree.add(t, Unit)
                }
            }

            return StringList(tree.optimized())
        }
    }
}