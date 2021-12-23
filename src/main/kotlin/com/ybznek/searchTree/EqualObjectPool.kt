package com.ybznek.searchTree

/**
 * Ensure that all equal objects are identical
 */
class EqualObjectPool {
    private val set: MutableMap<Any, Any> = HashMap()

    operator fun <T : Any?> get(value: T): T {
        @Suppress("UNCHECKED_CAST")
        return if (value == null)
            value
        else
            set.compute(value) { _, oldValue -> oldValue ?: value } as T
    }
}