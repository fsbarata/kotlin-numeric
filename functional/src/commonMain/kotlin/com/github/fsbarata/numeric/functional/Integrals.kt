package com.github.fsbarata.numeric.functional

import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.numeric.Integral
import com.github.fsbarata.numeric.RoundingType
import com.github.fsbarata.numeric.divide

inline fun <A, R> Integral.Scope<R>.averageOf(
	collection: NonEmptyCollection<A>,
	roundingType: RoundingType,
	f: (A) -> R,
): R {
	val head = f(collection.head)
	if (collection.tail.isEmpty()) return head
	return divide(head + collection.tail.foldMap(sumMonoid(), f), fromInt(collection.size), roundingType)
}

inline fun <A> Integral.Scope<A>.average(collection: NonEmptyCollection<A>, roundingType: RoundingType): A =
	averageOf(collection, roundingType, ::id)
