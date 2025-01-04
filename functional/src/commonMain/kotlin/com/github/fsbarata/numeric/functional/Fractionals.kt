package com.github.fsbarata.numeric.functional

import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.numeric.Fractional

inline fun <A, R> Fractional.Scope<R>.averageOf(collection: NonEmptyCollection<A>, f: (A) -> R): R {
	val head = f(collection.head)
	if (collection.tail.isEmpty()) return head
	return (head + collection.tail.foldMap(sumMonoid(), f)) / collection.size
}

inline fun <A> Fractional.Scope<A>.average(collection: NonEmptyCollection<A>): A = averageOf(collection, ::id)
