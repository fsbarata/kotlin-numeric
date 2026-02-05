package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.io.Serializable
import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ints.toInt128

class IntFraction private constructor(
	override val numerator: Int,
	override val denominator: Int,
): Fraction<Int, IntFraction>(), Serializable {
	init {
		require(denominator >= 0)
	}

	override inline val scope get() = IntExactNumScope

	override fun equals(other: Any?) = other is IntFraction && equalTo(other)
	override fun hashCode(): Int = hash()

	override fun createUnsafe(numerator: Int, denominator: Int): IntFraction =
		IntFraction(numerator, denominator)

	override fun compareByMultiplication(other: IntFraction): Int {
		return (numerator.toLong() * other.denominator).compareTo(other.numerator.toLong() * denominator)
	}

	fun toLongFraction(): LongFraction {
		return LongFraction.createUnsafeInternal(numerator.toLong(), denominator.toLong())
			.also { if (isReduced()) it.markReduced() }
	}

	fun toInt128Fraction(): Int128Fraction {
		return Int128Fraction.createUnsafeInternal(numerator.toInt128(), denominator.toInt128())
			.also { if (isReduced()) it.markReduced() }
	}

	override fun plus(n: Int, d: Int): IntFraction {
		return try {
			IntFraction.add(numerator, denominator, n, d)
		} catch (error: ArithmeticException) {
			val result = LongFraction.add(numerator.toLong(), denominator.toLong(), n.toLong(), d.toLong())
			return result.toIntFractionOrNull()
				?: result.reduced().toIntFractionOrThrow()
		}
	}

	override fun times(n: Int, d: Int): IntFraction {
		val result = LongFraction.create(numerator.toLong() * n.toLong(), denominator.toLong() * d.toLong())
		return result.toIntFractionOrNull()
			?: result.reduced().toIntFractionOrThrow()
	}

	fun approxNumerator(denominator: Int, roundingType: RoundingType = RoundingType.TRUNCATE): Int? {
		if (this.denominator == 0) throw ArithmeticException("Cannot approxNumerator on Infinite or NaN")
		return approxNumeratorLong(denominator, roundingType).toIntOrNull()
	}

	fun approxNumeratorLong(denominator: Int, roundingType: RoundingType = RoundingType.TRUNCATE): Long {
		if (this.denominator == 0) throw ArithmeticException("Cannot approxNumerator on Infinite or NaN")
		if (denominator == this.denominator) return numerator.toLong()
		return LongNumScope.divide(
			this.numerator.toLong() * denominator,
			this.denominator.toLong(),
			roundingType,
		)
	}

	fun roundToInt(roundingType: RoundingType): Int = IntNumScope.divide(numerator, denominator, roundingType)

	// Keeps compiler happy:
	override operator fun plus(addend: Int) = plus(addend, 1)
	override operator fun minus(sub: Int) = minus(sub, 1)
	override operator fun times(multiplier: Int) = times(multiplier, 1)
	override operator fun div(divisor: Int) = div(divisor, 1)

	companion object: FractionScope<Int, IntFraction>(IntExactNumScope), Serializable {
		val NAN = IntFraction(0, 0)
		override val ZERO = fromInt(0)
		override val ONE = fromInt(1)

		override fun createUnsafe(numerator: Int, denominator: Int): IntFraction =
			IntFraction(numerator, denominator)

		override fun fromInt(int: Int): IntFraction =
			IntFraction(int, 1).also { it.markReduced() }

		override fun fromRatio(ratio: Ratio<Int>): IntFraction =
			if (ratio is IntFraction) ratio
			else create(ratio.numerator, ratio.denominator)

		fun fromString(text: String): IntFraction {
			val index = text.indexOf('/')
			if (index < 0) throw IllegalArgumentException("Text $text is not a fraction string")
			val numerator = text.substring(0, index).toIntOrNull()
				?: throw IllegalArgumentException("Numerator is not an integer in $text")
			val denominator = text.substring(index + 1).toIntOrNull()
				?: throw IllegalArgumentException("Denominator denominator is not an integer in $text")
			return numerator over denominator
		}

		fun parseOrNull(text: String): IntFraction? {
			val index = text.indexOf('/')
			if (index < 0) return null
			val numerator = text.substring(0, index).toIntOrNull() ?: return null
			val denominator = text.substring(index + 1).toIntOrNull() ?: return null
			return numerator over denominator
		}
	}
}
