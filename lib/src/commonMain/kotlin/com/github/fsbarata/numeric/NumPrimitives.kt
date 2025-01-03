package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Rational
import com.github.fsbarata.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.sign

abstract class BaseIntNumScope: Integral.Scope<Int> {
	override val ZERO: Int = 0
	override val ONE: Int = 1

	override fun signum(a: Int): Int = a.sign

	override fun compare(a: Int, b: Int): Int = a.compareTo(b)

	// Keeps compiler happy
	abstract override fun addInt(a: Int, b: Int): Int
	abstract override fun subtractInt(a: Int, b: Int): Int
	abstract override fun multiplyInt(a: Int, b: Int): Int
	override fun divRem(a: Int, b: Int): Pair<Int, Int> = a.divRem(b)
	override fun divide(a: Int, b: Int): Int = a / b
	override fun mod(a: Int, b: Int): Int = a % b

	@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
	override operator fun Int.plus(addend: Int): Int = addInt(this, addend)

	@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
	override operator fun Int.minus(sub: Int): Int = subtractInt(this, sub)

	@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
	override operator fun Int.times(multiplier: Int): Int = multiplyInt(this, multiplier)

	@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
	override operator fun Int.div(divisor: Int): Int = divide(this, divisor)

	@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
	override operator fun Int.rem(divisor: Int): Int = mod(this, divisor)

	override fun fromInt(int: Int): Int = int

	override fun fromLong(long: Long): Int = long.toInt()
	override fun fromLongOrNull(long: Long): Int? = long.toIntOrNull()

	override fun fromInt128(int128: Int128): Int = int128.toInt()
	override fun fromInt128OrNull(int128: Int128): Int? = int128.toIntOrNull()

	override fun fromBigInt(bigInt: BigInt): Int = bigInt.toInt()
	override fun fromBigIntOrNull(bigInt: BigInt): Int? = bigInt.toIntOrNull()

	override fun toDouble(a: Int): Double = a.toDouble()

	override fun toInt(a: Int): Int = a
	override fun toIntOrNull(a: Int): Int = a
	override fun toLong(a: Int): Long = a.toLong()
	override fun toLongOrNull(a: Int): Long = a.toLong()
	override fun toInt128(a: Int): Int128 = Int128.fromInt(a)
	override fun toInt128OrNull(a: Int): Int128 = toInt128(a)
	override fun toBigInt(a: Int): BigInt = BigInt.fromInt(a)
}

object IntNumScope: BaseIntNumScope(), Integral.Scope<Int>, Serializable {
	private fun readResolve(): Any = IntNumScope

	override fun compare(a: Int, b: Int): Int = a.compareTo(b)

	override fun add(a: Int, b: Int): Int = a + b
	override fun subtract(a: Int, b: Int): Int = a - b
	override fun negate(a: Int): Int = -a
	override fun multiply(a: Int, b: Int): Int = a * b

	override fun addInt(a: Int, b: Int): Int = a + b
	override fun subtractInt(a: Int, b: Int): Int = a - b
	override fun multiplyInt(a: Int, b: Int): Int = a * b
}

object IntExactNumScope: BaseIntNumScope(), ExactIntegralScope<Int>, Serializable {
	private fun readResolve(): Any = IntExactNumScope

	override fun compare(a: Int, b: Int): Int = a.compareTo(b)

	override fun addInt(a: Int, b: Int): Int = addExact(a, b)
	override fun subtractInt(a: Int, b: Int): Int = subtractExact(a, b)
	override fun multiplyInt(a: Int, b: Int): Int = multiplyExact(a, b)

	override fun addOrNull(a: Int, b: Int): Int? = com.github.fsbarata.numeric.addOrNull(a, b)
	override fun negateOrNull(a: Int): Int? = com.github.fsbarata.numeric.negateOrNull(a)
	override fun subtractOrNull(a: Int, b: Int): Int? = com.github.fsbarata.numeric.subtractOrNull(a, b)
	override fun multiplyOrNull(a: Int, b: Int): Int? = com.github.fsbarata.numeric.multiplyOrNull(a, b)
}

abstract class BaseLongNumScope: Integral.Scope<Long>, Serializable {
	override val ZERO: Long = 0
	override val ONE: Long = 1

	override fun signum(a: Long): Int = a.sign

	override fun compare(a: Long, b: Long): Int = a.compareTo(b)

	override fun fromInt(int: Int): Long = int.toLong()

	override fun fromLong(long: Long): Long = long
	override fun fromLongOrNull(long: Long): Long = long

	override fun fromInt128(int128: Int128): Long = int128.toLong()
	override fun fromInt128OrNull(int128: Int128): Long? = int128.toLongOrNull()

	override fun fromBigInt(bigInt: BigInt): Long = bigInt.toLong()
	override fun fromBigIntOrNull(bigInt: BigInt): Long? = bigInt.toLongOrNull()

	override fun toDouble(a: Long): Double = a.toDouble()

	override fun divRem(a: Long, b: Long): Pair<Long, Long> = a.divRem(b)
	override fun divide(a: Long, b: Long): Long = a / b
	override fun mod(a: Long, b: Long): Long = a % b

	override fun toIntOrNull(a: Long): Int? = a.toIntOrNull()
	override fun toLong(a: Long): Long = a
	override fun toLongOrNull(a: Long): Long = a
	override fun toInt128(a: Long): Int128 = Int128.fromLong(a)
	override fun toInt128OrNull(a: Long): Int128 = toInt128(a)
	override fun toBigInt(a: Long): BigInt = BigInt.fromLong(a)

	override fun toInt(a: Long): Int = a.toInt()
}

object LongNumScope: BaseLongNumScope(), Integral.Scope<Long>, Serializable {
	private fun readResolve(): Any = LongNumScope

	override fun compare(a: Long, b: Long): Int = a.compareTo(b)

	override fun add(a: Long, b: Long): Long = a + b
	override fun subtract(a: Long, b: Long): Long = a - b
	override fun negate(a: Long): Long = -a
	override fun multiply(a: Long, b: Long): Long = a * b
}

object LongExactNumScope: BaseLongNumScope(), ExactIntegralScope<Long>, Serializable {
	private fun readResolve(): Any = LongExactNumScope

	override fun compare(a: Long, b: Long): Int = a.compareTo(b)

	override fun addOrNull(a: Long, b: Long): Long? = com.github.fsbarata.numeric.addOrNull(a, b)
	override fun negateOrNull(a: Long): Long? = com.github.fsbarata.numeric.negateOrNull(a)
	override fun multiplyOrNull(a: Long, b: Long): Long? = com.github.fsbarata.numeric.multiplyOrNull(a, b)
}

object DoubleNumScope: Floating.Scope<Double>, Serializable {
	private fun readResolve(): Any = DoubleNumScope

	override val ZERO: Double = 0.0
	override val ONE: Double = 1.0
	override val PI: Double = kotlin.math.PI
	override val E: Double = kotlin.math.E

	override fun add(a: Double, b: Double): Double = a + b

	override fun signum(a: Double): Int = a.signum()

	override fun negate(a: Double): Double = -a

	override fun multiply(a: Double, b: Double): Double = a * b
	override fun compare(a: Double, b: Double): Int = a.compareTo(b)

	override fun recip(a: Double): Double = 1.0 / a
	override fun divide(a: Double, b: Double): Double = a / b

	override fun fromInt(int: Int): Double = int.toDouble()
	override fun fromLong(long: Long): Double = long.toDouble()
	override fun fromLongOrNull(long: Long): Double? =
		if (canConvertLongToDoubleExact(long)) long.toDouble()
		else null

	override fun fromInt128(int128: Int128) = int128.toDouble()
	override fun fromInt128OrNull(int128: Int128): Double? = int128.toDoubleOrNull()
	override fun fromBigInt(bigInt: BigInt): Double = bigInt.toDouble()
	override fun fromBigIntOrNull(bigInt: BigInt): Double? = bigInt.toDoubleOrNull()
	override fun fromRational(rational: Rational): Double = rational.toDouble()

	override fun toDouble(a: Double): Double = a

	override fun toRational(a: Double): Rational {
		val (long, exponent) = frexp(a)
		val pow2 = BigInt.fromInt(2).pow(exponent.absoluteValue)
		return when {
			exponent < 0 -> Rational.create(BigInt.fromLong(long) * pow2, BigInt.ONE)
			exponent == 0 -> Rational.fromLong(long)
			else -> Rational.create(BigInt.fromLong(long), pow2)
		}
	}

	override fun exp(a: Double): Double = kotlin.math.exp(a)
	override fun ln(a: Double): Double = kotlin.math.ln(a)

	override fun log(a: Double, base: Double): Double = kotlin.math.log(a, base)

	override fun sin(a: Double): Double = kotlin.math.sin(a)
	override fun cos(a: Double): Double = kotlin.math.cos(a)
	override fun tan(a: Double): Double = kotlin.math.tan(a)

	override fun asin(a: Double): Double = kotlin.math.asin(a)
	override fun acos(a: Double): Double = kotlin.math.acos(a)
	override fun atan(a: Double): Double = kotlin.math.atan(a)

	override fun sinh(a: Double): Double = kotlin.math.sinh(a)
	override fun cosh(a: Double): Double = kotlin.math.cosh(a)
	override fun tanh(a: Double): Double = kotlin.math.tanh(a)

	override fun asinh(a: Double): Double = kotlin.math.asinh(a)
	override fun acosh(a: Double): Double = kotlin.math.acosh(a)
	override fun atanh(a: Double): Double = kotlin.math.atanh(a)

	override fun atan2(x: Double, y: Double): Double = kotlin.math.atan2(x, y)
}
