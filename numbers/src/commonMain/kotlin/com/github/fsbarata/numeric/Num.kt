package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Rational

interface Num<T>: Comparable<T>, Subtractive<T>, Multiplicative<T> {
	fun toRational(): Rational
	fun toDouble(): Double

	operator fun plus(addend: Int): T
	operator fun minus(sub: Int): T
	operator fun times(multiplier: Int): T

	interface OpScope<T>: Comparator<T>, Subtractive.Scope<T>, Multiplicative.Scope<T> {
		operator fun T.compareTo(other: T): Int = compare(this, other)

		fun toDouble(a: T): Double
		fun toRational(a: T): Rational
	}

	interface Scope<T>: OpScope<T> {
		val ZERO: T get() = fromInt(0)
		val ONE: T get() = fromInt(1)

		fun fromInt(int: Int): T = fromLong(int.toLong())
		fun fromLong(long: Long): T = fromInt128(Int128.fromLong(long))
		fun fromLongOrNull(long: Long): T?
		fun fromInt128(int128: Int128): T = fromBigInt(BigInt.fromInt128(int128))
		fun fromInt128OrNull(int128: Int128): T?
		fun fromBigInt(bigInt: BigInt): T
		fun fromBigIntOrNull(bigInt: BigInt): T?

		operator fun T.plus(addend: Int): T = addInt(this, addend)
		fun addInt(a: T, b: Int): T = add(a, fromInt(b))
		operator fun T.minus(sub: Int): T = subtractInt(this, sub)
		fun subtractInt(a: T, b: Int): T = subtract(a, fromInt(b))
		operator fun T.times(multiplier: Int): T = multiplyInt(this, multiplier)
		fun multiplyInt(a: T, b: Int): T = multiply(a, fromInt(b))

		override fun pow(base: T, exp: Int): T {
			require(exp >= 0)
			return when {
				exp == 0 || base == ONE -> ONE
				else -> super.pow(base, exp)
			}
		}
	}

	companion object {
		fun <T: Num<T>> delegateOpScope(): OpScope<T> =
			object: OpScope<T>,
				Subtractive.Scope<T> by Subtractive.delegateScope(),
				Multiplicative.Scope<T> by Multiplicative.delegateScope(),
				Comparator<T> {
				override fun compare(a: T, b: T): Int = a.compareTo(b)
				override fun toRational(a: T): Rational = a.toRational()
				override fun toDouble(a: T): Double = a.toDouble()
			}
	}
}


interface ExactNumScope<T>: Num.Scope<T> {
	fun addOrNull(a: T, b: T): T?
	fun negateOrNull(a: T): T?
	fun multiplyOrNull(a: T, b: T): T?
	fun absOrNull(a: T): T? {
		return if (signum(a) < 0) negateOrNull(a) else a
	}

	fun subtractOrNull(a: T, b: T): T? {
		return addOrNull(a, negateOrNull(b) ?: return null)
	}

	fun sqrOrNull(a: T): T? = multiplyOrNull(a, a)

	fun addOrNull(a: T, b: Int): T? = addOrNull(a, fromInt(b))
	fun subtractOrNull(a: T, b: Int): T? = subtractOrNull(a, fromInt(b))
	fun multiplyOrNull(a: T, b: Int): T? = multiplyOrNull(a, fromInt(b))

	override fun add(a: T, b: T) = addExact(a, b)
	override fun subtract(a: T, b: T) = subtractExact(a, b)
	override fun negate(a: T) = negateExact(a)
	override fun multiply(a: T, b: T) = multiplyExact(a, b)
	override fun abs(a: T) = absExact(a)
	override fun sqr(a: T) = sqrExact(a)

	override fun addInt(a: T, b: Int) = addIntExact(a, b)
	override fun subtractInt(a: T, b: Int) = subtractIntExact(a, b)
	override fun multiplyInt(a: T, b: Int) = multiplyIntExact(a, b)
}

fun <T> ExactNumScope<T>.addExact(a: T, b: T) =
	addOrNull(a, b) ?: throw ArithmeticException("Could not add $a + $b")

fun <T> ExactNumScope<T>.subtractExact(a: T, b: T) =
	subtractOrNull(a, b) ?: throw ArithmeticException("Could not subtract $a - $b")

fun <T> ExactNumScope<T>.negateExact(a: T) =
	negateOrNull(a) ?: throw ArithmeticException("Could not negate $a")

fun <T> ExactNumScope<T>.absExact(a: T) =
	absOrNull(a) ?: throw ArithmeticException("Could not negate $a")

fun <T> ExactNumScope<T>.multiplyExact(a: T, b: T) =
	multiplyOrNull(a, b) ?: throw ArithmeticException("Could not multiply $a * $b")

fun <T> ExactNumScope<T>.sqrExact(a: T) =
	sqrOrNull(a) ?: throw ArithmeticException("Could not square $a ^ 2")

fun <T> ExactNumScope<T>.addIntExact(a: T, b: Int) =
	addOrNull(a, b) ?: throw ArithmeticException("Could not add $a + $b")

fun <T> ExactNumScope<T>.subtractIntExact(a: T, b: Int) =
	subtractOrNull(a, b) ?: throw ArithmeticException("Could not subtract $a - $b")

fun <T> ExactNumScope<T>.multiplyIntExact(a: T, b: Int) =
	multiplyOrNull(a, b) ?: throw ArithmeticException("Could not multiply $a * $b")
