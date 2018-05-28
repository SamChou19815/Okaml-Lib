package com.developersam.fp

import java.util.LinkedList

/**
 * [FpList] is the functional list.
 *
 * @param T the type of elements in the list.
 */
sealed class FpList<out T> {

    /**
     * [Nil] is equivalent to `[]`, the nil node in the list.
     */
    object Nil : FpList<Nothing>() {
        override fun toString(): String = "Nil"
        override fun equals(other: Any?): Boolean = this === Nil
        override fun hashCode(): Int = 42
    }

    /**
     * [Node] with [data] and pointer to [next] is equivalent to [data] `::` [next].
     */
    data class Node<T>(val data: T, val next: FpList<T>) : FpList<T>()

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
    val isEmpty: Boolean
        get() = this == Nil

    /**
     * [size] returns the size of the list.
     */
    val size: Int
        get() = reduceFromLeft(acc = 0) { acc, _ -> acc + 1 }

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
        get() = when (this) {
            Nil -> Nil
            is Node<T> -> reduceFromLeft(acc = Nil as FpList<T>) { acc, data ->
                data cons acc
            }
        }

    /**
     * [map] applies [f] to each elements in the list to produce a new list.
     */
    fun <R> map(f: (T) -> R): FpList<R> = when (this) {
        Nil -> Nil
        is Node<T> -> Node(data = f(data), next = next.map(f))
    }

    /**
     * [reduceFromLeft] reduces the list to a value from left, with the accumulator initialized to
     * be [acc] and a reducer [f].
     */
    fun <R> reduceFromLeft(acc: R, f: (R, T) -> R): R = when (this) {
        Nil -> acc
        is Node<T> -> next.reduceFromLeft(acc = f(acc, data), f = f)
    }

    /**
     * [reduceFromLeft] reduces the list to a value from right, with the initial value [init] and
     * a reducer [f].
     */
    fun <R> reduceFromRight(init: R, f: (T, R) -> R): R = when (this) {
        Nil -> init
        is Node<T> -> f(data, next.reduceFromRight(init = init, f = f))
    }

    /**
     * [forEach] applies [f] to each of the elements in the list.
     */
    fun forEach(f: (T) -> Unit): Unit = when (this) {
        Nil -> Unit
        is Node<T> -> {
            f(data)
            next.forEach(f = f)
        }
    }

    /**
     * [forAll] checks whether [f] is satisfied by all elements.
     */
    fun forAll(f: (T) -> Boolean): Boolean = when (this) {
        Nil -> true
        is Node<T> -> if (!f(data)) false else next.forAll(f = f)
    }

    /**
     * [exists] checks whether [f] is satisfied by at least one element.
     */
    fun exists(f: (T) -> Boolean): Boolean = when (this) {
        Nil -> false
        is Node<T> -> if (f(data)) true else next.exists(f = f)
    }

}

/**
 * [cons] creates a new list with data at front and everything else in this list behind data.
 */
infix fun <T> T.cons(list: FpList<T>): FpList<T> = FpList.Node(data = this, next = list)

/**
 * [append] creates a list with [another] appended to it.
 */
fun <T> FpList<T>.append(another: FpList<T>): FpList<T> =
        reduceFromRight(init = another) { d, l -> d cons l }

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
operator fun <T> FpList<T>.contains(element: T): Boolean = when (this) {
    FpList.Nil -> false
    is FpList.Node<T> -> if (element == data) true else next.contains(element = element)
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
