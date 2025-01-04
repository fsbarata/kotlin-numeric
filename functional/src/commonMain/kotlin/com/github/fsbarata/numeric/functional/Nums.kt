package com.github.fsbarata.numeric.functional

import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.collection.runningReduceNel
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.numeric.Additive
import com.github.fsbarata.numeric.Multiplicative
import com.github.fsbarata.numeric.Num

fun <T> Additive.Scope<T>.sumSemigroup() = Semigroup.Scope(::add)
fun <T> Multiplicative.Scope<T>.productSemigroup() = Semigroup.Scope(::multiply)

fun <T> Num.Scope<T>.sumMonoid(): Monoid<T> = monoidOf(ZERO, ::add)
fun <T> Num.Scope<T>.productMonoid(): Monoid<T> = monoidOf(ONE, ::multiply)


inline fun <T: Additive<T>> NonEmptyCollection<T>.sum(): T = reduce { a, b -> a + b }
inline fun <A, R: Additive<R>> NonEmptyCollection<A>.sumOf(f: (A) -> R): R = tail.fold(f(head)) { r, a -> r + f(a) }

inline fun <T: Additive<T>> NonEmptyCollection<T>.cumsum(): NonEmptyList<T> = runningReduceNel { a, b -> a + b }
