package com.github.fsbarata.numeric

import kotlin.math.ceil
import kotlin.math.sqrt

inline fun <A> Integral.Scope<A>.divideInt(dividend: A, divisor: Int, roundingType: RoundingType): A =
	divide(dividend, fromInt(divisor), roundingType)

fun <A> Integral.Scope<A>.divide(dividend: A, divisor: A, roundingType: RoundingType): A {
	val divisorSignum = signum(divisor)
	if (divisorSignum == 0) throw ArithmeticException("Cannot divide by 0")

	val dividendSignum = signum(dividend)
	if (dividendSignum == 0) return ZERO

	if (divisor == ONE) return dividend

	fun awayFromZero(): A {
		val (quo, rem) = divRem(dividend, divisor)
		return roundAwayFromZero(dividendSignum, divisorSignum, quo, rem)
	}

	fun roundHalfUp(): A {
		val (quo, rem) = divRem(dividend, divisor)
		return roundHalfUp(dividendSignum, divisor, quo, rem)
	}

	fun truncate(): A = dividend / divisor

	return when (roundingType) {
		RoundingType.HALF_UP -> roundHalfUp()
		RoundingType.FLOOR -> if (dividendSignum != divisorSignum) awayFromZero() else truncate()
		RoundingType.CEIL -> if (dividendSignum != divisorSignum) truncate() else awayFromZero()
		RoundingType.AWAY_FROM_ZERO -> awayFromZero()
		RoundingType.TRUNCATE -> truncate()
	}
}

fun <A> Integral.Scope<A>.roundAwayFromZero(dividendSignum: Int, divisorSignum: Int, quo: A, rem: A): A {
	return roundAwayFromZero(quo, rem, if (dividendSignum != divisorSignum) -1 else 1)
}

fun <A> Integral.Scope<A>.roundAwayFromZero(quo: A, rem: A, quoSign: Int): A =
	if (signum(rem) == 0) quo else quo + quoSign

fun <A> Integral.Scope<A>.floor(dividendSignum: Int, divisorSignum: Int, quo: A, rem: A): A {
	return floor(quo, rem, if (dividendSignum != divisorSignum) -1 else 1)
}

fun <A> Integral.Scope<A>.floor(quo: A, rem: A, quoSign: Int): A {
	return if (quoSign < 0) roundAwayFromZero(quo, rem, quoSign) else quo
}

fun <A> Integral.Scope<A>.ceil(dividendSignum: Int, divisorSignum: Int, quo: A, rem: A): A {
	return ceil(quo, rem, if (dividendSignum != divisorSignum) -1 else 1)
}

fun <A> Integral.Scope<A>.ceil(quo: A, rem: A, quoSign: Int): A {
	return if (quoSign < 0) quo else roundAwayFromZero(quo, rem, quoSign)
}

fun <A> Integral.Scope<A>.roundHalfUp(dividendSignum: Int, divisor: A, quo: A, rem: A): A {
	if (signum(rem) == 0) return quo
	val divisorSign = signum(divisor)
	val quoNegative = dividendSignum != divisorSign
	val r2 = rem + rem
	val comp = compare(if (quoNegative) -r2 else r2, divisor)
	return when {
		divisorSign > 0 && comp >= 0 -> quo + if (quoNegative) -1 else 1
		divisorSign < 0 && comp <= 0 -> quo + if (quoNegative) -1 else 1
		else -> quo
	}
}

inline fun <A, R> Integral.Scope<R>.averageOfOrNull(
	iterable: Iterable<A>,
	roundingType: RoundingType,
	f: (A) -> R,
): R? {
	var sum = ZERO
	var count = 0
	for (element in iterable) {
		sum += f(element)
		count++
	}
	return when (count) {
		0 -> null
		1 -> sum
		else -> divideInt(sum, count, roundingType)
	}
}

inline fun <A, R> Integral.Scope<R>.averageOfOrNull(
	collection: Collection<A>,
	roundingType: RoundingType,
	f: (A) -> R,
): R? =
	if (collection.isEmpty()) null
	else divideInt(collection.fold(ZERO, { acc, a -> acc + f(a) }), collection.size, roundingType)

inline fun <A, R> Integral.Scope<R>.averageOfOrNull(array: Array<A>, roundingType: RoundingType, f: (A) -> R): R? =
	if (array.isEmpty()) null
	else divideInt(array.asIterable().fold(ZERO, { acc, a -> acc + f(a) }), array.size, roundingType)

inline fun <A> Integral.Scope<A>.averageOrNull(iterable: Iterable<A>, roundingType: RoundingType): A? =
	averageOfOrNull(iterable, roundingType) { it }

inline fun <A, I> I.isqrt(a: A): A where I: Bitwise.Scope<A>, I: Integral.Scope<A> {
	require(!isNegative(a))
	if (isZero(a)) return ZERO

	val highestSetBitIndex = highestSetBitIndex(a)
	val shiftsEven = when {
		highestSetBitIndex < 63 -> 0
		highestSetBitIndex.and(1) == 1 -> highestSetBitIndex - 61
		else -> highestSetBitIndex - 62
	}

	val long = toLong(if (highestSetBitIndex < 64) a else a.shr(shiftsEven))
	if (long <= 3) return ONE

	val estimateLong = ceil(sqrt(long.toDouble())).toLong()

	var xi = fromLong(estimateLong).shl(shiftsEven.shr(1))
	while (true) {
		// Newton's algorithm
		val xj = (xi + a / xi).shr(1)
		if (xj >= xi) return xi
		xi = xj
	}
}
