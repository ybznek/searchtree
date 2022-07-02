# SearchTree

Kotlin search tree structure

Build search tree which can effectively search multiple strings in one large string.

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.ybznek:searchtree:1.5'
	}

Examples:
=========

SearchTree:
-----------
Constructs search tree, basic node of mutable tree:

	internal sealed interface Node<V> {
		val value: V?
		val tree: Map<Char, Node<V>> // HashMap
	}

*Optimized tree* uses *multiple classes* for node representation, e.g. class for value-only Node. Some nodes uses different Map<> implementations according to tree structure.

	internal class ValueOnlyNode<V>(override val value: V?) : ImmutableNode<V> {
	override val tree: Map<Char, Node<V>>
		get() = emptyMap()
	}

Example of usage

	val mutableSearchTree = MutableSearchTree<Int>()
	mutableSearchTree.add("large", 111) // add value 111 under key "large"
	mutableSearchTree.add("string", 222) // add value 222 under key "string"
	val firstResult = mutableSearchTree.searchSequence("large string").toList()[0].value // 111
	val immutableSearchTree = mutableSearchTree.optimized() // converts to immutable, optimized structure
	val secondResult = immutableSearchTree.searchSequence("large string").toList()[1].value // 222

StringList:
----------
Wrapper around SearchTree. Provides ability to check occurence of multiple strings in one large string. Useful in case when there is large constant set of strings and string to search is large

	val list = StringList.of("monkey", "notebooks")
	assertThat(list isIn "test | notebooks | something").isTrue()

