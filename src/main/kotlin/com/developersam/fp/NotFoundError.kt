package com.developersam.fp

/**
 * [NotFoundError] is a common error for collections.
 */
class NotFoundError : RuntimeException("NotFound!") {
    companion object {
        /**
         * [raise] throws [NotFoundError].
         */
        fun raise(): Nothing = throw NotFoundError()
    }
}
