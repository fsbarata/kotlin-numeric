package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.RoundingType
import com.github.fsbarata.numeric.divide
import com.github.fsbarata.numeric.ints.*
import com.github.fsbarata.numeric.toIntOrNull
import com.github.fsbarata.io.Serializable
import kotlin.jvm.JvmName

class Int128Fraction private constructor(
	override val numerator: Int128,
	override val denominator: Int128,
): Fraction<Int128, Int128Fraction>(), Serializable {
	init {
		require(denominator >= Int128.ZERO)
	}

	override val scope get() = Int128ExactScope

	override fun equals(other: Any?) = other is Int128Fraction && equalTo(other)
	override fun hashCode(): Int = hash()

	override fun createUnsafe(numerator: Int128, denominator: Int128): Int128Fraction =
		Int128Fraction(numerator, denominator)

	override fun compareByMultiplication(other: Int128Fraction): Int {
		return (numerator.toBigInt() * other.denominator.toBigInt())
			.compareTo(other.numerator.toBigInt() * denominator.toBigInt())
	}

	fun toIntFraction(): IntFraction =
		toIntFractionOrNull() ?: reduced().toIntFractionOrThrow()

	fun toIntFractionOrNull(): IntFraction? {
		return IntFraction.create(
			numerator.toIntOrNull() ?: return null,
			denominator.toIntOrNull() ?: return null,
		).also { if (isReduced()) it.markReduced() }
	}

	fun toIntFractionOrThrow(): IntFraction {
		return IntFraction.create(
			numerator.toIntOrNull() ?: throw ArithmeticException("Cannot convert numerator $numerator to int"),
			denominator.toIntOrNull() ?: throw ArithmeticException("Cannot convert denominator $denominator to int"),
		).also { if (isReduced()) it.markReduced() }
	}

	fun toLongFraction(): LongFraction =
		toLongFractionOrNull() ?: reduced().toLongFractionOrThrow()

	fun toLongFractionOrNull(): LongFraction? {
		return LongFraction.create(
			numerator.toLongOrNull() ?: return null,
			denominator.toLongOrNull() ?: return null,
		).also { if (isReduced()) it.markReduced() }
	}

	fun toLongFractionOrThrow(): LongFraction {
		return LongFraction.create(
			numerator.toLongOrNull() ?: throw ArithmeticException("Cannot convert numerator $numerator to int"),
			denominator.toLongOrNull() ?: throw ArithmeticException("Cannot convert denominator $denominator to int"),
		).also { if (isReduced()) it.markReduced() }
	}

	override fun plus(n: Int128, d: Int128): Int128Fraction {
		return try {
			Int128Fraction.add(numerator, denominator, n, d)
		} catch (error: ArithmeticException) {
			Rational.add(numerator.toBigInt(), denominator.toBigInt(), n.toBigInt(), d.toBigInt())
				.reduced().toInt128FractionOrThrow()
		}
	}

	override fun times(n: Int128, d: Int128): Int128Fraction {
		return try {
			Int128Fraction.multiply(numerator, denominator, n, d)
		} catch (error: ArithmeticException) {
			Rational.multiply(numerator.toBigInt(), denominator.toBigInt(), n.toBigInt(), d.toBigInt())
				.reduced().toInt128FractionOrThrow()
		}
	}

	fun approxNumerator(denominator: Int128, roundingType: RoundingType = RoundingType.TRUNCATE): Int128? =
		approxNumeratorBigInt(denominator, roundingType).toInt128OrNull()

	fun approxNumeratorBigInt(denominator: Int128, roundingType: RoundingType = RoundingType.TRUNCATE): BigInt {
		if (denominator == this.denominator) return numerator.toBigInt()
		return approxNumerator(denominator.toBigInt(), roundingType)
	}

	fun roundToInt(roundingType: RoundingType): Int =
		roundToLong(roundingType).toIntOrNull() ?: when {
			numerator.isPositive() -> Int.MAX_VALUE
			else -> Int.MIN_VALUE
		}

	fun roundToLong(roundingType: RoundingType): Long =
		roundToInt128(roundingType).toLongOrNull() ?: when {
			numerator.isPositive() -> Long.MAX_VALUE
			else -> Long.MIN_VALUE
		}

	fun roundToInt128(roundingType: RoundingType): Int128 = Int128.divide(numerator, denominator, roundingType)
	fun roundToBigInt(roundingType: RoundingType): BigInt = roundToInt128(roundingType).toBigInt()

	companion object: FractionScope<Int128, Int128Fraction>(Int128ExactScope), Serializable {
		val NAN = Int128Fraction(Int128.ZERO, Int128.ZERO)
		override val ZERO = fromInt128(Int128.ZERO)
		override val ONE = fromInt128(Int128.ONE)

		override fun createUnsafe(numerator: Int128, denominator: Int128): Int128Fraction =
			Int128Fraction(numerator, denominator)

		override fun fromInt128(int128: Int128): Int128Fraction =
			Int128Fraction(int128, Int128.ONE).also { it.markReduced() }

		override fun fromRatio(ratio: Ratio<Int128>): Int128Fraction =
			if (ratio is Int128Fraction) ratio
			else create(ratio.numerator, ratio.denominator)

		fun fromString(text: String): Int128Fraction {
			val index = text.indexOf('/')
			if (index < 0) throw IllegalArgumentException("Text $text is not a fraction string")
			val numerator = Int128.parseOrNull(text.substring(0, index))
				?: throw IllegalArgumentException("Numerator is not an integer in $text")
			val denominator = Int128.parseOrNull(text.substring(index + 1))
				?: throw IllegalArgumentException("Denominator denominator is not an integer in $text")
			return create(numerator, denominator)
		}

		fun parseOrNull(text: String): Int128Fraction? {
			val index = text.indexOf('/')
			if (index < 0) return null
			val numerator = Int128.parseOrNull(text.substring(0, index)) ?: return null
			val denominator = Int128.parseOrNull(text.substring(index + 1)) ?: return null
			return create(numerator, denominator)
		}

		fun fromIntFraction(fraction: IntFraction): Int128Fraction = fraction.toInt128Fraction()
		fun fromLongFraction(fraction: LongFraction): Int128Fraction = fraction.toInt128Fraction()

		@JvmName("fromIntRatio")
		fun fromRatio(ratio: Ratio<Int>): Int128Fraction = create(ratio.numerator, ratio.denominator)

		@JvmName("fromLongRatio")
		fun fromRatio(ratio: Ratio<Long>): Int128Fraction = create(ratio.numerator, ratio.denominator)

		fun create(numerator: Int, denominator: Int): Int128Fraction =
			create(numerator.toInt128(), denominator.toInt128())

		fun create(numerator: Long, denominator: Long): Int128Fraction =
			create(numerator.toInt128(), denominator.toInt128())

		fun fromScaledInt128(unscaled: Int128, scale: Int): Int128Fraction {
			return when {
				unscaled.isZero() -> ZERO
				scale < -Int128.MAX_POW10 ->
					throw OverflowError("Cannot compute numerator from Scaled128 ($unscaled, $scale)")

				scale < 0 -> fromInt128(unscaled * Int128.tenPower(-scale))
				scale <= Int128.MAX_POW10 -> createUnsafe(unscaled, Int128.tenPower(scale))
				scale <= Int128.MAX_POW10 + Int128.MAX_POW10 ->
					createUnsafe(
						Int128ExactScope.multiplyOrNull(unscaled, Int128.tenPower(scale - Int128.MAX_POW10))
							?: throw UnderflowError("Cannot compute denominator from Scaled128 ($unscaled, $scale)"),
						Int128.tenPower(Int128.MAX_POW10),
					)

				else -> throw UnderflowError("Cannot compute denominator from Scaled128 ($unscaled, $scale)")
			}
		}
	}
}
