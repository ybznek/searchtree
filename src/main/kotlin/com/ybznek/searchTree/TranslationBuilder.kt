package com.ybznek.searchTree

/**
 * Helper for building of translations via SearchTree
 */
public class TranslationBuilder private constructor() {
    private val tree = MutableSearchTree<SearchTree.ValueWithKey<String>>()

    inner class FromMapping(private val sourceStrings: Array<out String>) {
        infix fun to(target: String) {
            for (sourceString in sourceStrings) {
                tree.addKeyValue(sourceString, target)
            }
        }
    }

    fun from(vararg sourceStrings: String): FromMapping {
        return FromMapping(sourceStrings);
    }

    companion object {
        fun build(body: TranslationBuilder.() -> Unit): SearchTree<SearchTree.ValueWithKey<String>> {
            val translatorBuilder = TranslationBuilder()
            body(translatorBuilder)
            return translatorBuilder.tree.optimized()
        }
    }
}