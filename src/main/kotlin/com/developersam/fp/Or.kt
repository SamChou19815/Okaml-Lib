package com.developersam.fp

/**
 * [Or] represents a choice between two alternatives.
 *
 * @param A type of data in the first alternative.
 * @param B type of data in the second alternative.
 */
sealed class Or<out A, out B> {

    /**
     * [First] represents the first choice with [data].
     *
     * @param data data associated with this alternative.
     * @param A type of data in the first alternative.
     */
    data class First<A>(val data: A) : Or<A, Nothing>()

    /**
     * [Second] represents the second choice with [data].
     *
     * @param data data associated with this alternative.
     * @param B type of data in the second alternative.
     */
    data class Second<B>(val data: B): Or<Nothing, B>()

}
