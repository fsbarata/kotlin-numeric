package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.io.Serializable
import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ints.multiplyFull
import com.github.fsbarata.numeric.ints.toInt128
import kotlin.jvm.JvmName

class LongFraction private constructor(
	override val numerator: Long,
	override val denominator: Long,
): Fraction<Long, LongFraction>(), Serializable {
	init {
		require(denominator >= 0)
	}

	override inline val scope get() = LongExactNumScope

	override fun equals(other: Any?) = other is LongFraction && equalTo(other)
	override fun hashCode(): Int = hash()

	override fun createUnsafe(numerator: Long, denominator: Long): LongFraction =
		LongFraction(numerator, denominator)

	override fun compareByMultiplication(other: LongFraction): Int {
		return multiplyFull(numerator, other.denominator).compareTo(multiplyFull(other.numerator, denominator))
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

	fun toInt128Fraction(): Int128Fraction {
		return Int128Fraction.createUnsafeInternal(numerator.toInt128(), denominator.toInt128())
			.also { if (isReduced()) it.markReduced() }
	}

	override fun plus(n: Long, d: Long): LongFraction {
		return try {
			LongFraction.add(numerator, denominator, n, d)
		} catch (error: ArithmeticException) {
			Int128Fraction.add(numerator.toInt128(), denominator.toInt128(), n.toInt128(), d.toInt128())
				.reduced().toLongFractionOrThrow()
		}
	}

	override fun times(n: Long, d: Long): LongFraction {
		return try {
			LongFraction.multiply(numerator, denominator, n, d)
		} catch (error: ArithmeticException) {
			Int128Fraction.multiply(numerator.toInt128(), denominator.toInt128(), n.toInt128(), d.toInt128())
				.reduced().toLongFractionOrThrow()
		}
	}

	fun approxNumerator(denominator: Long, roundingType: RoundingType = RoundingType.TRUNCATE): Long? {
		if (this.denominator == 0L) throw ArithmeticException("Cannot approxNumerator on Infinite or NaN")
		if (denominator == this.denominator) return numerator
		return approxNumeratorInt128(denominator, roundingType).toLongOrNull()
	}

	fun approxNumeratorInt128(denominator: Long, roundingType: RoundingType = RoundingType.TRUNCATE): Int128 {
		if (this.denominator == 0L) throw ArithmeticException("Cannot approxNumerator on Infinite or NaN")
		if (denominator == this.denominator) return Int128.fromLong(numerator)
		val multiplication = multiplyFull(this.numerator, denominator)
		return Int128.divide(multiplication, Int128.fromLong(this.denominator), roundingType)
	}

	fun roundToInt(roundingType: RoundingType): Int =
		roundToLong(roundingType).toIntOrNull() ?: when {
			numerator > 0L -> Int.MAX_VALUE
			else -> Int.MIN_VALUE
		}

	fun roundToLong(roundingType: RoundingType): Long = LongNumScope.divide(numerator, denominator, roundingType)

	companion object: FractionScope<Long, LongFraction>(LongExactNumScope), Serializable {
		val NAN = LongFraction(0, 0)
		override val ZERO = fromLong(0L)
		override val ONE = fromLong(1L)

		override fun createUnsafe(numerator: Long, denominator: Long): LongFraction =
			LongFraction(numerator, denominator)

		override fun fromLong(long: Long): LongFraction =
			LongFraction(long, 1).also { it.markReduced() }

		override fun fromRatio(ratio: Ratio<Long>): LongFraction =
			if (ratio is LongFraction) ratio
			else create(ratio.numerator, ratio.denominator)

		fun fromString(text: String): LongFraction {
			val index = text.indexOf('/')
			if (index < 0) throw IllegalArgumentException("Text $text is not a fraction string")
			val numerator = text.substring(0, index).toLongOrNull()
				?: throw IllegalArgumentException("Numerator is not an integer in $text")
			val denominator = text.substring(index + 1).toLongOrNull()
				?: throw IllegalArgumentException("Denominator denominator is not an integer in $text")
			return create(numerator, denominator)
		}

		fun parseOrNull(text: String): LongFraction? {
			val index = text.indexOf('/')
			if (index < 0) return null
			val numerator = text.substring(0, index).toLongOrNull() ?: return null
			val denominator = text.substring(index + 1).toLongOrNull() ?: return null
			return create(numerator, denominator)
		}

		fun fromIntFraction(fraction: IntFraction) = fraction.toLongFraction()

		@JvmName("fromIntRatio")
		fun fromRatio(ratio: Ratio<Int>): LongFraction = create(ratio.numerator, ratio.denominator)

		fun create(numerator: Int, denominator: Int): LongFraction = create(numerator.toLong(), denominator.toLong())
	}
}
