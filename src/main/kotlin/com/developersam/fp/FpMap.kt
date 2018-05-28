package com.developersam.fp

/**
 * [FpMap] is the functional map.
 *
 * @param K the type of keys in the map.
 * @param V the type of values in the map.
 */
sealed class FpMap<K : Comparable<K>, V> {

    /**
     * [Leaf] represents the leaf node.
     */
    private object Leaf : FpMap<Nothing, Nothing>() {
        override val height: Int = 0
        override fun toString(): String = "Leaf"
        override fun equals(other: Any?): Boolean = this === Leaf
        override fun hashCode(): Int = 42
    }

    /**
     * [Node] represents a node in the AVL tree, where
     * - [key] [value] is a key-value pair.
     * - [left] and [right] are two children.
     * - [height] records the current height.
     */
    private data class Node<K : Comparable<K>, V>(
            val left: FpMap<K, V>, val key: K, val value: V, val right: FpMap<K, V>,
            override val height: Int
    ) : FpMap<K, V>() {
        /**
         * Creates the node without knowing height.
         */
        constructor(left: FpMap<K, V>, key: K, value: V, right: FpMap<K, V>) : this(
                left = left, key = key, value = value, right = right,
                height = if (left.height >= right.height) left.height + 1 else right.height + 1
        )
    }

    /**
     * [bstInvariantHolds] reports whether BST invariant holds for the tree.
     */
    private val bstInvariantHolds: Boolean
        get() {
            val bindings = bindings.toArrayList()
            // check BST invariant
            for (i in 0 until (bindings.size - 1)) {
                val k1 = bindings[i].first
                val k2 = bindings[i + 1].first
                if (k1 > k2) {
                    return false
                }
            }
            return true
        }

    /**
     * [bstInvariantHolds] reports whether AVL invariant holds for the tree.
     */
    private val avlInvariantHolds: Boolean
        get() = when (this) {
            Leaf -> true
            is Node<K, V> -> Math.abs(left.height - right.height) < 2
                        && left.avlInvariantHolds
                        && right.avlInvariantHolds
        }

    /**
     * [invariantHolds] internally reports whether the invariant holds.
     */
    internal val invariantHolds: Boolean
        get() = bstInvariantHolds && avlInvariantHolds

    /**
     * [isEmpty] reports whether this map is empty.
     */
    val isEmpty: Boolean
        get() = this == Leaf

    /**
     * [height] returns height of the tree.
     */
    protected abstract val height: Int

    /**
     * [size] reports the size of the map.
     */
    val size: Int
        get() = when (this) {
            Leaf -> 0
            is Node<K, V> -> 1 + left.size + right.size
        }

    /**
     * [contains] reports whether the map contains [key].
     */
    operator fun contains(key: K): Boolean = when (this) {
        Leaf -> false
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> true
                c < 0 -> left.contains(key = key)
                else -> right.contains(key = key)
            }
        }
    }

    /**
     * [get] returns the optional value associated with [key].
     */
    operator fun get(key: K): V? = when (this) {
        Leaf -> null
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> value
                c < 0 -> left[key]
                else -> right[key]
            }
        }
    }

    /**
     * [put] creates a new map with additional [key] [value] pair.
     */
    fun put(key: K, value: V): FpMap<K, V> = when (this) {
        Leaf -> Node(left = empty(), key = key, value = value, right = empty(), height = 1)
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            if (c == 0) {
                if (value == this.value) {
                    this
                } else {
                    Node(left = left, key = key, value = value, right = right, height = height)
                }
            } else if (c < 0) {
                balance(left = left.put(key = key, value = value), right = right,
                        key = this.key, value = this.value)
            } else {
                balance(left = left, right = right.put(key = key, value = value),
                        key = this.key, value = this.value)
            }
        }
    }

    /**
     * [removeFirstBinding] creates a new map without the first binding.
     */
    private fun removeFirstBinding(): FpMap<K, V> = when (this) {
        Leaf -> empty()
        is Node<K, V> -> when (left) {
            Leaf -> right
            else -> balance(left = left.removeFirstBinding(), right = right,
                    key = key, value = value)
        }
    }

    /**
     * [remove] creates another map without the specified [key].
     */
    fun remove(key: K): FpMap<K, V> = when (this) {
        Leaf -> empty()
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> when {
                    left == Leaf -> right
                    right == Leaf -> left
                    else -> {
                        val rightMin = right.firstBinding!!
                        balance(left = left, right = right.removeFirstBinding(),
                                key = rightMin.first, value = rightMin.second)
                    }
                }
                c < 0 -> balance(left = left.remove(key = key), right = right,
                        key = this.key, value = this.value)
                else -> balance(left = left, right = right.remove(key = key),
                        key = this.key, value = this.value)
            }
        }
    }

    /**
     * [forEach] applies [f] to every key-value pair in increasing order.
     */
    fun forEach(f: (K, V) -> Unit): Unit = when (this) {
        Leaf -> Unit
        is Node<K, V> -> {
            left.forEach(f = f)
            f(key, value)
            right.forEach(f = f)
        }
    }

    /**
     * [reduce] reduces the map to a value by applying [f] in increasing order, with the starting
     * accumulator [acc].
     */
    fun <R> reduce(acc: R, f: (K, V, R) -> R): R = when (this) {
        Leaf -> acc
        is Node<K, V> -> right.reduce(acc = f(key, value, left.reduce(acc = acc, f = f)), f = f)
    }

    /**
     * [exists] checks whether [f] is satisfied by at least one key-value pair.
     */
    fun exists(f: (K, V) -> Boolean): Boolean = when (this) {
        Leaf -> false
        is Node<K, V> -> f(key, value) || left.exists(f = f) || right.exists(f = f)
    }

    /**
     * [forAll] checks whether [f] is satisfied by all key-value pairs.
     */
    fun forAll(f: (K, V) -> Boolean): Boolean = when (this) {
        Leaf -> true
        is Node<K, V> -> f(key, value) && left.forAll(f = f) && right.forAll(f = f)
    }

    /**
     * [filter] creates a new map with all the key-value pairs that satisfies [f].
     */
    fun filter(f: (K, V) -> Boolean): FpMap<K, V> = reduce(acc = empty()) { k, v, acc ->
        if (f(k, v)) {
            acc.put(key = k, value = v)
        } else {
            acc
        }
    }

    /**
     * [partition] creates a pair of two maps where the first one contains all the key-value pairs
     * that satisfy [f], and the second one contains the rest.
     */
    fun partition(f: (K, V) -> Boolean): Pair<FpMap<K, V>, FpMap<K, V>> =
            reduce(acc = Pair(first = empty(), second = empty())) { k, v, acc ->
                if (f(k, v)) {
                    Pair(first = acc.first.put(key = k, value = v), second = acc.second)
                } else {
                    Pair(first = acc.first, second = acc.second.put(key = k, value = v))
                }
            }

    /**
     * [bindings] returns the list of all bindings of the given map.
     * The list is sorted according to keys.
     */
    val bindings: FpList<Pair<K, V>>
        get() = reduce(acc = FpList.empty()) { k, v, acc -> (k to v) cons acc }

    /**
     * [firstBinding] returns the optionally exist first (min) bindings as a key-value pair.
     */
    val firstBinding: Pair<K, V>?
        get() = when (this) {
            Leaf -> null
            is Node<K, V> -> if (left == Leaf) key to value else left.firstBinding
        }

    /**
     * [lastBinding] returns the optionally exist last (max) bindings as a key-value pair.
     */
    val lastBinding: Pair<K, V>?
        get() = when (this) {
            Leaf -> null
            is Node<K, V> -> if (right == Leaf) key to value else right.lastBinding
        }

    /**
     * [peek] returns an unspecified pair in the map, or `null` is the map is empty.
     */
    fun peek(): Pair<K, V>? = when (this) {
        Leaf -> null
        is Node<K, V> -> key to value
    }

    /**
     * [mapByKey] creates a new map where each key is replaced by a new one computed by applying [f]
     * to the old key.
     */
    fun <K2 : Comparable<K2>> mapByKey(f: (K) -> K2): FpMap<K2, V> =
            reduce(acc = empty()) { k, v, acc -> acc.put(key = f(k), value = v) }

    /**
     * [mapByValue] creates a new map where each value is replaced by a new one computed by
     * applying [f] to the old value.
     */
    fun <V2> mapByValue(f: (V) -> V2): FpMap<K, V2> =
            reduce(acc = empty()) { k, v, acc -> acc.put(key = k, value = f(v)) }

    /**
     * [mapByKeyValuePair] creates a new map where each key-value pair is replaced by a new pair
     * computed by applying [f] to the old pair.
     */
    fun <K2 : Comparable<K2>, V2> mapByKeyValuePair(f: (K, V) -> Pair<K2, V2>): FpMap<K2, V2> =
            reduce(acc = empty()) { k, v, acc ->
                val result = f(k, v)
                acc.put(key = result.first, value = result.second)
            }

    companion object {

        /**
         * [empty] creates an empty map.
         */
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <K : Comparable<K>, V> empty(): FpMap<K, V> = Leaf as FpMap<K, V>

        /**
         * [singleton] creates a single key-value pair map from the given [key] and [value].
         */
        fun <K : Comparable<K>, V> singleton(key: K, value: V): FpMap<K, V> =
                FpMap.Node(left = empty(), key = key, value = value, right = empty())

        /**
         * [create] creates a map from the given [pairs] in variable arguments.
         */
        fun <K : Comparable<K>, V> create(vararg pairs: Pair<K, V>): FpMap<K, V> {
            var map: FpMap<K, V> = empty()
            for ((k, v) in pairs) {
                map = map.put(key = k, value = v)
            }
            return map
        }

        /**
         * [create] creates a map from the given [pairs] in list.
         */
        fun <K : Comparable<K>, V> create(pairs: FpList<Pair<K, V>>): FpMap<K, V> =
                pairs.reduceFromLeft(acc = empty()) { acc, (key, value) -> acc.put(key, value) }

        /**
         * [balance] tries to balance the AVL tree with the new [left] or [right] branch and the
         * old key value pair as [key] and [value].
         */
        private fun <K : Comparable<K>, V> balance(left: FpMap<K, V>, right: FpMap<K, V>,
                                                   key: K, value: V): FpMap<K, V> {
            val leftHeight = left.height
            val rightHeight = right.height
            return when {
                leftHeight > rightHeight + 2 -> when (left) {
                    Leaf -> throw Error("Impossible")
                    is Node<K, V> -> {
                        val leftLeft = left.left
                        val leftRight = left.right
                        if (leftLeft.height >= leftRight.height) {
                            val r = Node(left = leftRight, key = key, value = value, right = right)
                            Node(left = leftLeft, key = left.key, value = left.value, right = r)
                        } else when (leftRight) {
                            Leaf -> throw Error("Impossible")
                            is Node<K, V> -> {
                                val leftRightLeft = leftRight.left
                                val leftRightRight = leftRight.right
                                Node(
                                        left = Node(
                                                left = leftLeft, key = left.key,
                                                value = left.value, right = leftRightLeft
                                        ),
                                        key = leftRight.key, value = leftRight.value,
                                        right = Node(
                                                left = leftRightRight, key = key,
                                                value = value, right = right
                                        )
                                )
                            }
                        }
                    }
                }
                rightHeight > leftHeight + 2 -> when (right) {
                    Leaf -> throw Error("Impossible")
                    is Node<K, V> -> {
                        val rightLeft = right.left
                        val rightRight = right.right
                        if (rightRight.height >= rightLeft.height) {
                            val l = Node(left = left, key = key, value = value, right = rightLeft)
                            Node(left = l, key = right.key, value = right.value, right = rightRight)
                        } else when (rightLeft) {
                            Leaf -> throw Error("Impossible")
                            is Node<K, V> -> {
                                val rightLeftLeft = rightLeft.left
                                val rightLeftRight = rightLeft.right
                                Node(
                                        left = Node(
                                                left = left, key = key,
                                                value = value, right = rightLeftLeft
                                        ),
                                        key = rightLeft.key, value = rightLeft.value,
                                        right = Node(
                                                left = rightLeftRight, key = right.key,
                                                value = right.value, right = rightRight
                                        )
                                )
                            }
                        }
                    }
                }
                else -> Node(left = left, right = right, key = key, value = value)
            }
        }
    }

}
