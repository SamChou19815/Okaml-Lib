package com.developersam.fp

/**
 * [FpSet] is the functional set.
 *
 * @property m the backing map for the set.
 * @param V the type of values in the set.
 */
class FpSet<V : Comparable<V>>(val m: FpMap<V, Unit>) : Iterable<V> {

    /**
     * [isEmpty] reports whether this set is empty.
     */
    val isEmpty: Boolean get() = m.isEmpty

    /**
     * [size] reports the size of the set.
     */
    val size: Int get() = m.size

    /**
     * [contains] reports whether the set contains [value].
     */
    operator fun contains(value: V): Boolean = m.contains(key = value)

    /**
     * [add] creates a new set with additional [value].
     */
    fun add(value: V): FpSet<V> = FpSet(m = m.put(key = value, value = Unit))

    /**
     * [remove] creates another set without the specified [value].
     */
    fun remove(value: V): FpSet<V> = FpSet(m = m.remove(key = value))

    /**
     * [union] computes the union of this set and [another].
     */
    fun union(another: FpSet<V>): FpSet<V> = fold(initial = another) { acc, v -> acc.add(v) }

    /**
     * [intersection] computes the intersection of this set and [another].
     */
    fun intersection(another: FpSet<V>): FpSet<V> = fold(initial = another) { acc, v ->
        if (v in this) acc.add(v) else acc
    }

    /**
     * [minus] computes the set difference: this \ [another].
     */
    operator fun minus(another: FpSet<V>): FpSet<V> =
            fold(initial = another) { acc, v -> acc.remove(v) }

    /**
     * [isSubsetOf] checks whether this set is the subset of [another].
     */
    fun isSubsetOf(another: FpSet<V>): Boolean = forAll { it in another }

    /**
     * [map] maps each element in the set to another value by applying [transform] to each value.
     */
    inline fun <T : Comparable<T>> map(crossinline transform: (V) -> T): FpSet<T> =
            FpSet(m = m.mapByKey(transform = transform))

    /**
     * [forEach] applies [action] to every value in increasing order.
     */
    inline fun forEach(crossinline action: (V) -> Unit): Unit = m.forEach { v, _ -> action(v) }

    /**
     * [fold] reduces the set to a value by applying [operation] in increasing order, with the
     * starting accumulator [initial].
     */
    inline fun <R> fold(initial: R, crossinline operation: (R, V) -> R): R =
            m.fold(initial = initial) { v, _, a -> operation(a, v) }

    /**
     * [exists] checks whether [predicate] is satisfied by at least one value.
     */
    inline fun exists(crossinline predicate: (V) -> Boolean): Boolean =
            m.exists { v, _ -> predicate(v) }

    /**
     * [forAll] checks whether [predicate] is satisfied by all values.
     */
    inline fun forAll(crossinline predicate: (V) -> Boolean): Boolean =
            m.all { v, _ -> predicate(v) }

    /**
     * [filter] creates a new set with all the values that satisfies [predicate].
     */
    inline fun filter(crossinline predicate: (V) -> Boolean): FpSet<V> =
            FpSet(m = m.filter { v, _ -> predicate(v) })

    /**
     * [partition] creates a pair of two sets where the first one contains all the values that
     * satisfy [predicate], and the second one contains the rest.
     */
    inline fun partition(crossinline predicate: (V) -> Boolean): Pair<FpSet<V>, FpSet<V>> {
        val (first, second) = m.partition { v, _ -> predicate(v) }
        return FpSet(m = first) to FpSet(m = second)
    }

    /**
     * [elements] returns the list of all values of the given set.
     * The list is sorted according to keys.
     */
    val elements: FpList<V> get() = m.bindings.map { it.first }

    /**
     * [minElement] returns the optionally exist minimum element.
     */
    val minElement: V? get() = m.firstBinding?.first

    /**
     * [maxElement] returns the optionally exist maximum element.
     */
    val maxElement: V? get() = m.lastBinding?.first

    /**
     * [peek] returns an unspecified value in the set, or `null` is the set is empty.
     */
    fun peek(): V? = m.peek()?.first

    override fun toString(): String = toList().toString()

    override fun iterator(): Iterator<V> = m.bindings.asSequence().map { it.first }.iterator()

    companion object {

        /**
         * [empty] creates an empty set.
         */
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <V : Comparable<V>> empty(): FpSet<V> = FpSet(m = FpMap.empty())

        /**
         * [singleton] creates a single value set from the given [value].
         */
        fun <V : Comparable<V>> singleton(value: V): FpSet<V> =
                FpSet(m = FpMap.singleton(key = value, value = Unit))

        /**
         * [create] creates a set from the given [values] in variable arguments.
         */
        fun <V : Comparable<V>> create(vararg values: V): FpSet<V> {
            var set = empty<V>()
            for (v in values) {
                set = set.add(value = v)
            }
            return set
        }

        /**
         * [create] creates a set from the given [list].
         */
        fun <V : Comparable<V>> create(list: FpList<V>): FpSet<V> =
                list.fold(initial = empty()) { acc, v -> acc.add(value = v) }

    }

}
