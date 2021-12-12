package com.ybznek.searchTree


/**
 * Space efficient Map, if keys is continuous sequence of characters
 * Todo : null & notnull values are not handled
 */
internal class CharIntervalMap<Value>(private val minChar: Char, private val items: Array<Value>) : Map<Char, Value> {

    val maxChar: Char
        get() = minChar + items.size - 1

    override val entries: Set<Map.Entry<Char, Value>>
        get() = items.withIndex().map { (index, value) ->
            val char = minChar + index
            object : Map.Entry<Char, Value> {
                override val key: Char = char
                override val value: Value = value
            }
        }.toSet()
    override val keys: Set<Char>
        get() = (minChar..maxChar).toSet()
    override val size: Int
        get() = items.size
    override val values: Collection<Value>
        get() = items.asList()

    override fun containsValue(value: Value): Boolean {
        return items.any { it == value }
    }

    override fun get(key: Char): Value? {
        val requestedIndex = key.code - minChar.code

        return if (requestedIndex >= 0 && requestedIndex < items.size) {
            items[requestedIndex]
        } else {
            null
        }
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun containsKey(key: Char): Boolean {
        return key in minChar..maxChar
    }

    override fun toString(): String {
        val usedCharacters = keys.filter { chr -> get(chr) != null }.toCharArray()
        val usedCharactersString = String(usedCharacters)
        return "CharIntervalMap(keys=$usedCharactersString)"
    }


}