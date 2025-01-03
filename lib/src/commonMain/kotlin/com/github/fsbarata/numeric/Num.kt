package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Ratio
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

interface Integral<T>: Num<T>, Divisible<T> {
	fun toInt(): Int
	fun toIntOrNull(): Int?
	fun toLong(): Long
	fun toLongOrNull(): Long?
	fun toInt128(): Int128
	fun toInt128OrNull(): Int128?
	fun toBigInt(): BigInt

	override operator fun div(divisor: T): T
	operator fun rem(divisor: T): T
	fun divRem(divisor: T): Pair<T, T> = Pair(div(divisor), rem(divisor))

	operator fun div(divisor: Int): T
	operator fun rem(divisor: Int): Int
	fun divRem(divisor: Int): Pair<T, Int> = Pair(div(divisor), rem(divisor))

	interface Scope<T>: Num.Scope<T>, OpScope<T> {
		operator fun T.div(divisor: Int): T = divide(this, divisor)
		fun divide(a: T, b: Int): T = divide(a, fromInt(b))

		fun divRem(a: T, b: Int): Pair<T, Int> {
			val (d, r) = divRem(a, fromInt(b))
			return Pair(
				d,
				toIntOrNull(r) ?: throw IllegalStateException("Remainder of division $a / $b must be smaller than $b"),
			)
		}

		operator fun T.rem(divisor: Int): Int = mod(this, divisor)
		fun mod(a: T, b: Int): Int =
			toIntOrNull(mod(a, fromInt(b)))
				?: throw IllegalStateException("Remainder of division $a / $b must be smaller than $b")
	}

	interface OpScope<T>: Num.OpScope<T>, Divisible.Scope<T> {
		fun divRem(a: T, b: T): Pair<T, T> = Pair(a / b, a % b)

		operator fun T.rem(divisor: T): T = mod(this, divisor)
		fun mod(a: T, b: T): T

		fun toInt(a: T): Int
		fun toIntOrNull(a: T): Int?
		fun toLong(a: T): Long
		fun toLongOrNull(a: T): Long?
		fun toInt128(a: T): Int128
		fun toInt128OrNull(a: T): Int128?
		fun toBigInt(a: T): BigInt

		override fun toRational(a: T): Rational = Rational.fromBigInt(toBigInt(a))
	}

	companion object {
		fun <T: Integral<T>> delegateOpScope(): OpScope<T> =
			object: OpScope<T>, Num.OpScope<T> by Num.delegateOpScope(),
				Divisible.Scope<T> by Divisible.delegateScope() {
				override fun divRem(a: T, b: T): Pair<T, T> = a.divRem(b)
				override fun mod(a: T, b: T): T = a % b

				override fun toInt(a: T): Int = a.toInt()
				override fun toIntOrNull(a: T): Int? = a.toIntOrNull()
				override fun toLong(a: T): Long = a.toLong()
				override fun toLongOrNull(a: T): Long? = a.toLongOrNull()
				override fun toInt128(a: T): Int128 = a.toInt128()
				override fun toInt128OrNull(a: T): Int128? = a.toInt128OrNull()
				override fun toBigInt(a: T): BigInt = a.toBigInt()

				override fun toRational(a: T): Rational = a.toRational()
			}
	}
}

interface Fractional<T>: Num<T>, Divisible<T> {
	override operator fun div(divisor: T): T
	fun recip(): T

	operator fun div(divisor: Int): T

	interface Scope<T>: Num.Scope<T>, OpScope<T> {
		override fun divideInt(a: T, b: Int) = divide(a, fromInt(b))

		fun fromIntRatio(ratio: Ratio<Int>): T =
			divide(fromInt(ratio.numerator), fromInt(ratio.denominator))

		fun fromLongRatio(ratio: Ratio<Long>): T =
			divide(fromLong(ratio.numerator), fromLong(ratio.denominator))

		fun fromInt128Ratio(ratio: Ratio<Int128>): T =
			divide(fromInt128(ratio.numerator), fromInt128(ratio.denominator))

		fun fromRational(rational: Rational): T =
			divide(fromBigInt(rational.numerator), fromBigInt(rational.denominator))

		override fun pow(base: T, exp: Int): T {
			return when {
				exp < 0 -> recip(super<Num.Scope>.pow(base, -exp))
				else -> super<Num.Scope>.pow(base, exp)
			}
		}
	}

	interface OpScope<T>: Num.OpScope<T>, Divisible.Scope<T> {
		operator fun T.div(divisor: Int): T = divideInt(this, divisor)
		fun divideInt(a: T, b: Int): T

		override fun divide(a: T, b: T): T = multiply(a, recip(b))
		fun recip(a: T): T
	}

	companion object {
		fun <T: Fractional<T>> delegateOpScope(): OpScope<T> =
			object: OpScope<T>, Num.OpScope<T> by Num.delegateOpScope(), Divisible.Scope<T> {
				override fun divideInt(a: T, b: Int) = a / b
				override fun divide(a: T, b: T) = a / b
				override fun recip(a: T): T = a.recip()
			}
	}
}

object Floating {
	interface Scope<T>: Fractional.Scope<T> {
		val PI: T
		val E: T

		fun exp(a: T): T
		fun ln(a: T): T

		fun log(a: T, base: T): T

		fun sin(a: T): T
		fun cos(a: T): T
		fun tan(a: T): T

		fun asin(a: T): T
		fun acos(a: T): T
		fun atan(a: T): T

		fun sinh(a: T): T
		fun cosh(a: T): T
		fun tanh(a: T): T

		fun asinh(a: T): T
		fun acosh(a: T): T
		fun atanh(a: T): T

		fun atan2(x: T, y: T): T
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

interface ExactIntegralScope<T>: ExactNumScope<T>, Integral.Scope<T>