package com.github.fsbarata.numeric

inline fun <A, R> Fractional.Scope<R>.averageOfOrNull(iterable: Iterable<A>, f: (A) -> R): R? {
	var sum = ZERO
	var count = 0
	for (element in iterable) {
		sum += f(element)
		count++
	}
	return when (count) {
		0 -> null
		1 -> sum
		else -> sum / count
	}
}

inline fun <A, R> Fractional.Scope<R>.averageOfOrNull(collection: Collection<A>, f: (A) -> R): R? =
	if (collection.isEmpty()) null else (collection.fold(ZERO, { acc, a -> acc + f(a) })) / collection.size

inline fun <A, R> Fractional.Scope<R>.averageOfOrNull(array: Array<A>, f: (A) -> R): R? =
	if (array.isEmpty()) null else (array.asIterable().fold(ZERO, { acc, a -> acc + f(a) })) / array.size

inline fun <R> Fractional.Scope<R>.averageOrNull(iterable: Iterable<R>): R? =
	averageOfOrNull(iterable) { it }
