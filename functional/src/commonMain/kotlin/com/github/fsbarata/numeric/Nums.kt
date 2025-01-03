package com.github.fsbarata.numeric

import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.collection.runningReduceNel
import com.github.fsbarata.functional.data.list.NonEmptyList

fun <T> Additive.Scope<T>.sumSemigroup() = Semigroup.Scope(::add)
fun <T> Multiplicative.Scope<T>.productSemigroup() = Semigroup.Scope(::multiply)

fun <T> Num.Scope<T>.sumMonoid(): Monoid<T> = monoidOf(ZERO, ::add)
fun <T> Num.Scope<T>.productMonoid(): Monoid<T> = monoidOf(ONE, ::multiply)


inline fun <T: Additive<T>> NonEmptyCollection<T>.sum(): T = reduce { a, b -> a + b }
inline fun <A, R: Additive<R>> NonEmptyCollection<A>.sumOf(f: (A) -> R): R = tail.fold(f(head)) { r, a -> r + f(a) }

inline fun <T: Additive<T>> NonEmptyCollection<T>.cumsum(): NonEmptyList<T> = runningReduceNel { a, b -> a + b }

inline fun <A, R> Fractional.Scope<R>.averageOf(collection: NonEmptyCollection<A>, f: (A) -> R): R {
	val head = f(collection.head)
	if (collection.tail.isEmpty()) return head
	return (head + collection.tail.foldMap(sumMonoid(), f)) / collection.size
}

inline fun <A> Fractional.Scope<A>.average(collection: NonEmptyCollection<A>): A = averageOf(collection, ::id)


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
