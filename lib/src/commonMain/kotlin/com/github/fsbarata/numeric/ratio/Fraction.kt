package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.io.Serializable
import kotlin.jvm.JvmName
import kotlin.jvm.Transient

abstract class FractionBase<T>: Ratio<T> {
	protected abstract val scope: Integral.Scope<T>

	fun isNaN(): Boolean = scope.isZero(numerator) && scope.isZero(denominator)
	fun isInfinity(): Boolean = scope.isNotZero(numerator) && scope.isZero(denominator)

	fun approxNumerator(denominator: BigInt, roundingType: RoundingType = RoundingType.TRUNCATE): BigInt {
		if (scope.isZero(this.denominator)) throw ArithmeticException("Cannot approxNumerator on Infinite or NaN")
		val thisNumeratorBigInt = scope.toBigInt(this.numerator)
		val thisDenominatorBigInt = scope.toBigInt(this.denominator)
		if (denominator == thisDenominatorBigInt) return thisNumeratorBigInt
		return BigInt.divide(denominator * thisNumeratorBigInt, thisDenominatorBigInt, roundingType)
	}

	fun approxUnscaled(scale: Int, roundingType: RoundingType = RoundingType.HALF_UP): BigInt {
		val numeratorBigInt = scope.toBigInt(this.numerator)
		val denominatorBigInt = scope.toBigInt(this.denominator)
		return when {
			scale == 0 -> BigInt.divide(numeratorBigInt, denominatorBigInt, roundingType)
			scale < 0 -> BigInt.divide(numeratorBigInt, denominatorBigInt * BigInt.TEN.pow(-scale), roundingType)
			else -> approxNumerator(BigInt.TEN.pow(scale), roundingType)
		}
	}
}

abstract class Fraction<T, R: Fraction<T, R>>: FractionBase<T>(),
	Fractional<R>,
	Serializable {
	protected abstract fun createUnsafe(numerator: T, denominator: T): R

	private fun createSafe(numerator: T, denominator: T): R =
		if (scope.signum(denominator) >= 0) createUnsafe(numerator, denominator)
		else createUnsafe(scope.negate(numerator), scope.negate(denominator))

	@Transient
	private var reduced: R? = null

	override fun toString(): String {
		return "$numerator/$denominator"
	}

	fun equalTo(other: Fraction<T, R>): Boolean {
		if (denominator == other.denominator)
			return numerator == other.numerator

		try {
			return scope.multiply(numerator, other.denominator) == scope.multiply(denominator, other.numerator)
		} catch (error: ArithmeticException) {
			val reduced = reduced()
			val otherReduced = other.reduced()
			return reduced.numerator == otherReduced.numerator && reduced.denominator == otherReduced.denominator
		}
	}

	fun hash(): Int = when {
		denominator == scope.ZERO -> scope.ZERO
		numerator == denominator -> scope.ONE
		numerator == scope.ZERO -> scope.ZERO
		scope.signum(numerator) > 0 -> when {
			scope.compare(numerator, denominator) > 0 -> scope.divide(numerator, denominator)
			else -> scope.divide(denominator, numerator)
		}

		else -> when (val r = scope.divide(numerator, denominator)) {
			scope.ZERO -> scope.divide(denominator, numerator)
			else -> r
		}
	}.hashCode()

	override fun compareTo(other: R): Int = with(scope) {
		when {
			denominator == other.denominator ->
				if (denominator == ZERO) signum(numerator).compareTo(signum(other.numerator))
				else numerator.compareTo(other.numerator)

			denominator == ZERO ->
				if (signum(numerator) <= 0) -1 // this == -infinity or NaN
				else 1 // this == +infinity

			other.denominator == ZERO ->
				if (signum(other.numerator) <= 0) 1 // other == -infinity or NaN
				else -1 // other == +infinity

			numerator == ZERO -> ZERO.compareTo(other.numerator)
			other.numerator == ZERO -> numerator.compareTo(ZERO)

			numerator == other.numerator -> other.denominator.compareTo(denominator)

			else -> {
				val on = other.numerator
				val signComparison = signum().compareTo(scope.signum(on))
				if (signComparison != 0) signComparison
				else compareByMultiplication(other)
			}
		}
	}

	protected abstract fun compareByMultiplication(other: R): Int

	override fun toDouble(): Double {
		return if (scope.isZero(denominator)) {
			when (scope.signum(numerator)) {
				-1 -> Double.NEGATIVE_INFINITY
				1 -> Double.POSITIVE_INFINITY
				else -> Double.NaN
			}
		} else scope.toDouble(numerator) / scope.toDouble(denominator)
	}

	fun toDoubleOrNull(): Double? = if (scope.isZero(denominator)) null else toDouble()

	override fun toRational(): Rational {
		return Rational.create(scope.toBigInt(numerator), scope.toBigInt(denominator))
			.also { if (isReduced()) it.markReduced() }
	}

	fun reduced(): R {
		return reduced
			?: scope.reduce(numerator, denominator, ::createUnsafe)
				.also { reduced ->
					this.reduced = reduced
					reduced.markReduced()
				}
	}

	internal fun markReduced() {
		reduced = this as R
	}

	fun isReduced() = reduced != null

	override fun signum(): Int = scope.signum(numerator)

	override fun abs(): R = createUnsafe(scope.abs(numerator), denominator)

	override operator fun plus(addend: R): R {
		return plus(addend.numerator, addend.denominator)
	}

	protected abstract fun plus(n: T, d: T): R

	override operator fun minus(sub: R): R {
		return minus(sub.numerator, sub.denominator)
	}

	protected inline fun minus(n: T, d: T): R = plus(scope.negate(n), d)

	override operator fun unaryMinus(): R = createUnsafe(scope.negate(numerator), denominator)

	override operator fun times(multiplier: R): R {
		return times(multiplier.numerator, multiplier.denominator)
	}

	protected abstract fun times(n: T, d: T): R

	override operator fun div(divisor: R): R {
		return div(divisor.numerator, divisor.denominator)
	}

	protected inline fun div(n: T, d: T): R = times(d, n)

	override fun recip(): R = createSafe(denominator, numerator)

	override operator fun plus(addend: Int) = plus(scope.fromInt(addend))
	override operator fun minus(sub: Int) = minus(scope.fromInt(sub))
	override operator fun times(multiplier: Int) = times(scope.fromInt(multiplier))
	override operator fun div(divisor: Int) = div(scope.fromInt(divisor))

	open operator fun plus(addend: T) = plus(addend, scope.ONE)
	open operator fun minus(sub: T) = plus(scope.negate(sub), scope.ONE)
	open operator fun times(multiplier: T) = times(multiplier, scope.ONE)
	open operator fun div(divisor: T) = times(scope.ONE, divisor)

	@JvmName("plusIntRatio")
	operator fun plus(addend: Ratio<Int>) = plus(scope.fromInt(addend.numerator), scope.fromInt(addend.denominator))

	@JvmName("minusIntRatio")
	operator fun minus(sub: Ratio<Int>) = minus(scope.fromInt(sub.numerator), scope.fromInt(sub.denominator))

	@JvmName("timesIntRatio")
	operator fun times(multiplier: Ratio<Int>) =
		times(scope.fromInt(multiplier.numerator), scope.fromInt(multiplier.denominator))

	@JvmName("divIntRatio")
	operator fun div(divisor: Ratio<Int>) = div(scope.fromInt(divisor.numerator), scope.fromInt(divisor.denominator))
}

private fun <T, R> Integral.Scope<T>.reduce(numerator: T, denominator: T, create: (T, T) -> R): R {
	if (
		denominator == ONE || denominator == ZERO || denominator == -ONE ||
		numerator == ONE || numerator == ZERO || numerator == -ONE
	) return create(numerator, denominator)

	val gcd = greatestCommonDenominator(numerator, denominator)
	return create(
		if (gcd == ONE) numerator else (numerator / gcd),
		if (gcd == ONE) denominator else (denominator / gcd),
	)
}

abstract class FractionScope<T, R: Fraction<T, R>>(
	val scope: Integral.Scope<T>,
): Fractional.Scope<R>,
	Fractional.OpScope<R> by Fractional.delegateOpScope() {

	protected abstract fun createUnsafe(numerator: T, denominator: T): R
	abstract fun fromRatio(ratio: Ratio<T>): R

	internal fun createUnsafeInternal(numerator: T, denominator: T) = createUnsafe(numerator, denominator)

	fun create(numerator: T, denominator: T): R =
		if (scope.signum(denominator) >= 0) createUnsafe(numerator, denominator)
		else createUnsafe(scope.negate(numerator), scope.negate(denominator))

	fun createReduced(numerator: T, denominator: T): R {
		return scope.reduce(numerator, denominator, ::create)
			.also { it.markReduced() }
	}

	override fun fromInt(int: Int) = createUnsafe(scope.fromInt(int), scope.ONE)
	override fun fromLong(long: Long): R = createUnsafe(scope.fromLong(long), scope.ONE)
	override fun fromLongOrNull(long: Long): R? = scope.fromLongOrNull(long)?.let { createUnsafe(it, scope.ONE) }
	override fun fromInt128(int128: Int128): R = createUnsafe(scope.fromInt128(int128), scope.ONE)
	override fun fromInt128OrNull(int128: Int128): R? =
		scope.fromInt128OrNull(int128)?.let { createUnsafe(it, scope.ONE) }

	override fun fromBigInt(bigInt: BigInt) = createUnsafe(scope.fromBigInt(bigInt), scope.ONE)
	override fun fromBigIntOrNull(bigInt: BigInt): R? =
		scope.fromBigIntOrNull(bigInt)?.let { createUnsafe(it, scope.ONE) }

	override fun fromRational(rational: Rational) =
		createUnsafe(scope.fromBigInt(rational.numerator), scope.fromBigInt(rational.denominator))

	internal fun add(num1: T, den1: T, num2: T, den2: T): R = with(scope) {
		return when {
			den1 == den2 -> createUnsafe(num2 + num1, den1)
			else -> create(
				(num2 * den1) + (num1 * den2),
				den2 * den1,
			)
		}
	}

	internal fun multiply(num1: T, den1: T, num2: T, den2: T): R = with(scope) {
		return create(num1 * num2, den1 * den2)
	}
}



