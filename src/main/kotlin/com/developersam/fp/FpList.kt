package com.developersam.fp

/**
 * [FpList] is the functional list.
 *
 * @param T the type of elements in the list.
 */
sealed class FpList<out T> : Iterable<T> {

    /**
     * [Nil] is equivalent to `[]`, the nil node in the list.
     */
    object Nil : FpList<Nothing>() {
        override fun equals(other: Any?): Boolean = this === Nil
        override fun hashCode(): Int = 42
    }

    /**
     * [Node] with [data] and pointer to [next] is equivalent to [data] `::` [next].
     *
     * @param data data associated with this node.
     * @param next next node.
     * @param T the type of elements in the list.
     */
    data class Node<T>(val data: T, val next: FpList<T>) : FpList<T>()

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        private var curr: FpList<T> = this@FpList

        override fun hasNext(): Boolean = curr != Nil

        override fun next(): T {
            val immutableCurr = curr as? Node<T> ?: error(message = "Wrong use of iterator!")
            val data = immutableCurr.data
            curr = immutableCurr.next
            return data
        }
    }

    companion object {
        /**
         * [empty] creates an empty list.
         */
        fun <T> empty(): FpList<T> = Nil

        /**
         * [singleton] creates a singleton list that contains only [data].
         */
        fun <T> singleton(data: T): FpList<T> = Node(data = data, next = empty())

        /**
         * [create] creates a list from the given [values].
         */
        fun <T> create(vararg values: T): FpList<T> {
            var list = empty<T>()
            for (i in (values.size - 1) downTo 0) {
                list = values[i] cons list
            }
            return list
        }
    }

    /**
     * [isEmpty] reports whether this list is empty.
     */
    val isEmpty: Boolean get() = this == Nil

    /**
     * [size] returns the size of the list.
     */
    val size: Int get() = fold(initial = 0) { acc, _ -> acc + 1 }

    /**
     * [head] returns the head of the list, or throws [NotFoundError] if the list is empty.
     */
    val head: T
        get() = when (this) {
            Nil -> NotFoundError.raise()
            is Node<T> -> data
        }

    /**
     * [tail] returns the tail of the list, or throws [NotFoundError] if the list is empty.
     */
    val tail: FpList<T>
        get() = when (this) {
            Nil -> NotFoundError.raise()
            is Node<T> -> next
        }

    /**
     * [reverse] is the reverse of the list.
     */
    val reverse: FpList<T>
        get() = fold(initial = Nil as FpList<T>) { acc, data -> data cons acc }

    /**
     * [map] applies [transform] to each elements in the list to produce a new list.
     */
    fun <R> map(transform: (T) -> R): FpList<R> = when (this) {
        FpList.Nil -> Nil
        is Node<T> -> Node(data = transform(data), next = next.map(transform))
    }

    /**
     * [fold] reduces the list to a value from left, with the accumulator initialized to
     * be [initial] and a reducer [operation].
     */
    inline fun <reified R> fold(initial: R, crossinline operation: (R, T) -> R): R {
        var curr = this
        var acc = initial
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            acc = operation(acc, data)
            curr = next
        }
        return acc
    }

    /**
     * [foldRight] reduces the list to a value from right, with the initial value [initial] and
     * a reducer [operation].
     * Not tail-recursive.
     */
    fun <R> foldRight(initial: R, operation: (R, T) -> R): R = when (this) {
        Nil -> initial
        is Node<T> -> operation(
                next.foldRight(initial = initial, operation = operation), data
        )
    }

    /**
     * [forEach] applies [action] to each of the elements in the list.
     */
    inline fun forEach(crossinline action: (T) -> Unit) {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            action(data)
            curr = next
        }
    }

    /**
     * [all] checks whether [predicate] is satisfied by all elements.
     */
    inline fun all(crossinline predicate: (T) -> Boolean): Boolean {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            if (!predicate(data)) {
                return false
            }
            curr = next
        }
        return true
    }

    /**
     * [exists] checks whether [predicate] is satisfied by at least one element.
     */
    inline fun exists(crossinline predicate: (T) -> Boolean): Boolean {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            if (predicate(data)) {
                return true
            }
            curr = next
        }
        return false
    }

    final override fun toString(): String = toList().toString()

}

/**
 * [cons] creates a new list with data at front and everything else in this list behind data.
 */
infix fun <T> T.cons(list: FpList<T>): FpList<T> = FpList.Node(data = this, next = list)

/**
 * [append] creates a list with [another] appended to it.
 * Not tail-recursive.
 */
fun <T> FpList<T>.append(another: FpList<T>): FpList<T> =
        foldRight(initial = another) { list, data -> data cons list }

/**
 * [flatten] flattens a list of lists.
 * Not tail-recursive.
 */
val <T> FpList<FpList<T>>.flatten: FpList<T>
    get() = when (this) {
        FpList.Nil -> FpList.Nil
        is FpList.Node<FpList<T>> -> data.append(another = next.flatten)
    }

/**
 * [contains] checks whether the given [element] is in the list.
 */
operator fun <T> FpList<T>.contains(element: T): Boolean {
    var curr = this
    while (curr != FpList.Nil) {
        val (data, next) = curr as FpList.Node<T>
        if (element == data) {
            return true
        }
        curr = next
    }
    return false
}

/**
 * [FpList.toArrayList] converts the list to array list.
 */
@Suppress(names = ["UNCHECKED_CAST"])
fun <T> FpList<T>.toArrayList(): List<T> {
    val tempList = arrayListOf<T>()
    var list = this
    while (list != FpList.Nil) {
        tempList.add(list.head)
        list = list.tail
    }
    return tempList
}
