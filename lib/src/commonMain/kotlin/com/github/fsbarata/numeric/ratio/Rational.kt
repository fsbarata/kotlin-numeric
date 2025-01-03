package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.RoundingType
import com.github.fsbarata.numeric.divide
import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.io.Serializable
import kotlin.jvm.JvmName

class Rational private constructor(
	override val numerator: BigInt,
	override val denominator: BigInt,
): Fraction<BigInt, Rational>(),
	Ratio<BigInt> {
	init {
		require(!denominator.isNegative())
	}

	override val scope get() = BigInt

	override fun equals(other: Any?) = other is Rational && equalTo(other)
	override fun hashCode(): Int = hash()

	override fun createUnsafe(numerator: BigInt, denominator: BigInt) = Rational(numerator, denominator)

	override fun compareByMultiplication(other: Rational): Int {
		return (numerator * other.denominator).compareTo(other.numerator * denominator)
	}

	fun toIntFraction(): IntFraction =
		toIntFractionOrNull() ?: reduced().toIntFractionOrThrow()

	fun toIntFractionOrNull(): IntFraction? {
		return IntFraction.create(
			numerator.toIntOrNull() ?: return null,
			denominator.toIntOrNull() ?: return null,
		)
	}

	fun toIntFractionOrThrow(): IntFraction {
		return IntFraction.create(
			numerator.toIntOrNull() ?: throw ArithmeticException("Cannot convert numerator $numerator to int"),
			denominator.toIntOrNull() ?: throw ArithmeticException("Cannot convert denominator $denominator to int"),
		)
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
			numerator.toLongOrNull() ?: throw ArithmeticException("Cannot convert numerator $numerator to long"),
			denominator.toLongOrNull() ?: throw ArithmeticException("Cannot convert denominator $denominator to long"),
		).also { if (isReduced()) it.markReduced() }
	}

	fun toInt128Fraction(): Int128Fraction =
		toInt128FractionOrNull() ?: reduced().toInt128FractionOrThrow()

	fun toInt128FractionOrNull(): Int128Fraction? {
		return Int128Fraction.create(
			numerator.toInt128OrNull() ?: return null,
			denominator.toInt128OrNull() ?: return null,
		).also { if (isReduced()) it.markReduced() }
	}

	fun toInt128FractionOrThrow(): Int128Fraction {
		return Int128Fraction.create(
			numerator.toInt128OrNull() ?: throw ArithmeticException("Cannot convert numerator $numerator to int128"),
			denominator.toInt128OrNull()
				?: throw ArithmeticException("Cannot convert denominator $denominator to int128"),
		).also { if (isReduced()) it.markReduced() }
	}

	override fun plus(n: BigInt, d: BigInt): Rational = Rational.add(numerator, denominator, n, d)
	override fun times(n: BigInt, d: BigInt): Rational = Rational.multiply(numerator, denominator, n, d)

	operator fun plus(addend: Long) = plus(fromLong(addend))
	operator fun minus(sub: Long) = minus(fromLong(sub))
	operator fun times(multiplier: Long) = times(fromLong(multiplier))
	operator fun div(divisor: Long) = div(fromLong(divisor))

	@JvmName("plusLongRatio")
	operator fun plus(addend: Ratio<Long>): Rational =
		plus(BigInt.fromLong(addend.numerator), BigInt.fromLong(addend.denominator))

	@JvmName("minusLongRatio")
	operator fun minus(sub: Ratio<Long>): Rational =
		minus(BigInt.fromLong(sub.numerator), BigInt.fromLong(sub.denominator))

	@JvmName("timesLongRatio")
	operator fun times(multiplier: Ratio<Long>): Rational =
		times(BigInt.fromLong(multiplier.numerator), BigInt.fromLong(multiplier.denominator))

	@JvmName("divLongRatio")
	operator fun div(divisor: Ratio<Long>) =
		div(BigInt.fromLong(divisor.numerator), BigInt.fromLong(divisor.denominator))

	override fun toRational(): Rational = this

	fun roundToInt(roundingType: RoundingType = RoundingType.HALF_UP): Int =
		round(roundingType).toIntOrNull() ?: when {
			numerator.isPositive() -> Int.MAX_VALUE
			else -> Int.MIN_VALUE
		}

	fun roundToLong(roundingType: RoundingType = RoundingType.HALF_UP): Long =
		round(roundingType).toLongOrNull() ?: when {
			numerator.isPositive() -> Long.MAX_VALUE
			else -> Long.MIN_VALUE
		}

	fun round(roundingType: RoundingType = RoundingType.HALF_UP): BigInt =
		BigInt.divide(numerator, denominator, roundingType)

	fun roundOrNull(roundingType: RoundingType = RoundingType.HALF_UP): BigInt? =
		if (denominator.isZero()) null else round(roundingType)

	companion object: FractionScope<BigInt, Rational>(BigInt), Serializable {
		val NAN = Rational(BigInt.ZERO, BigInt.ZERO)
		override val ZERO = Rational(BigInt.ZERO, BigInt.ONE)
		override val ONE = Rational(BigInt.ONE, BigInt.ONE)

		override fun fromBigInt(bigInt: BigInt): Rational =
			Rational(bigInt, BigInt.ONE).also { it.markReduced() }

		override fun fromRatio(ratio: Ratio<BigInt>) =
			if (ratio is Rational) ratio
			else create(ratio.numerator, ratio.denominator)

		override fun fromRational(rational: Rational) = rational
		override fun toRational(a: Rational): Rational = a

		override fun createUnsafe(numerator: BigInt, denominator: BigInt) = Rational(numerator, denominator)

		fun fromIntFraction(fraction: IntFraction): Rational = fraction.toRational()

		@JvmName("fromIntRatio")
		fun fromRatio(ratio: Ratio<Int>): Rational = create(ratio.numerator, ratio.denominator)
		fun create(numerator: Int, denominator: Int): Rational =
			create(BigInt.fromInt(numerator), BigInt.fromInt(denominator))

		fun fromLongFraction(fraction: LongFraction): Rational = fraction.toRational()

		@JvmName("fromLongRatio")
		fun fromRatio(ratio: Ratio<Long>): Rational = create(ratio.numerator, ratio.denominator)
		fun create(numerator: Long, denominator: Long): Rational =
			create(BigInt.fromLong(numerator), BigInt.fromLong(denominator))

		fun fromString(text: String): Rational {
			val index = text.indexOf('/')
			if (index < 0) throw IllegalArgumentException("Text $text is not a fraction string")
			val numerator = text.substring(0, index).let(BigInt::parseOrNull)
				?: throw IllegalArgumentException("Numerator is not an integer in $text")
			val denominator = text.substring(index + 1).let(BigInt::parseOrNull)
				?: throw IllegalArgumentException("Denominator denominator is not an integer in $text")
			return numerator over denominator
		}

		fun parseOrNull(text: String): Rational? {
			val terms = text.split("/")
			if (terms.size != 2) return null

			val numerator = BigInt.parseOrNull(terms[0]) ?: return null
			val denominator = BigInt.parseOrNull(terms[1]) ?: return null
			return numerator over denominator
		}

		fun fromScaledBigInt(unscaled: BigInt, scale: Int): Rational {
			return when {
				scale == 0 -> fromBigInt(unscaled)
				scale < 0 -> fromBigInt(unscaled * BigInt.TEN.pow(-scale))
				else -> createUnsafe(unscaled, BigInt.TEN.pow(scale))
			}
		}
	}
}