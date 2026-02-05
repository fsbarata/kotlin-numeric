@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.github.fsbarata.numeric

import com.github.fsbarata.io.Serializable
import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Rational
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign


@JvmInline
value class IntNum(val int: Int): Integral<IntNum>, Bitwise<IntNum> {
	override fun toInt(): Int = int
	override fun toIntOrNull(): Int = int
	override fun toLong(): Long = int.toLong()
	override fun toLongOrNull(): Long = toLong()
	override fun toInt128(): Int128 = Int128.fromInt(int)
	override fun toInt128OrNull(): Int128 = toInt128()
	override fun toBigInt(): BigInt = BigInt.fromInt(int)

	override fun toRational(): Rational = Rational.fromInt(int)
	override fun toDouble(): Double = int.toDouble()

	override operator fun plus(addend: IntNum): IntNum = IntNum(int + addend.int)
	override operator fun minus(sub: IntNum): IntNum = IntNum(int - sub.int)
	override operator fun times(multiplier: IntNum): IntNum = IntNum(int * multiplier.int)
	override operator fun div(divisor: IntNum): IntNum = IntNum(int / divisor.int)
	override operator fun rem(divisor: IntNum): IntNum = IntNum(int % divisor.int)

	override operator fun plus(addend: Int): IntNum = IntNum(int + addend)
	override operator fun minus(sub: Int): IntNum = IntNum(int - sub)
	override operator fun times(multiplier: Int) = IntNum(int * multiplier)
	override operator fun div(divisor: Int): IntNum = IntNum(int / divisor)
	override operator fun rem(divisor: Int): Int = int % divisor

	override fun compareTo(other: IntNum): Int = int.compareTo(other.int)

	override fun signum(): Int = int.sign
	override fun unaryMinus(): IntNum = IntNum(-int)

	override fun not(): IntNum = IntNum(int.inv())

	override fun and(other: IntNum): IntNum = IntNum(int.and(other.int))
	override fun or(other: IntNum): IntNum = IntNum(int.or(other.int))
	override fun xor(other: IntNum): IntNum = IntNum(int.xor(other.int))
	override fun shl(bitCount: Int): IntNum = IntNum(int.shl(bitCount))
	override fun shr(bitCount: Int): IntNum = IntNum(int.shr(bitCount))

	override fun highestSetBitIndex(): Int = IntNumScope.highestSetBitIndex(int)
	override fun lowestSetBitIndex(): Int = IntNumScope.lowestSetBitIndex(int)

	override fun toString(): String = int.toString()

	abstract class Scope: Integral.Scope<IntNum>,
		Integral.OpScope<IntNum> by Integral.delegateOpScope(),
		Bitwise.Scope<IntNum> by Bitwise.delegateScope() {
		override val ZERO = IntNum(0)
		override val ONE = IntNum(1)

		override fun isZero(a: IntNum): Boolean = a.int == 0
		override fun isPositive(a: IntNum): Boolean = a.int > 0
		override fun isNegative(a: IntNum): Boolean = a.int < 0

		override fun fromInt(int: Int): IntNum = IntNum(int)
		override fun fromLong(long: Long): IntNum = IntNum(long.toInt())
		override fun fromLongOrNull(long: Long): IntNum? = long.toIntOrNull()?.let(::IntNum)
		override fun fromInt128(int128: Int128): IntNum = IntNum(int128.toInt())
		override fun fromInt128OrNull(int128: Int128): IntNum? = int128.toIntOrNull()?.let(::IntNum)
		override fun fromBigInt(bigInt: BigInt): IntNum = IntNum(bigInt.toInt())
		override fun fromBigIntOrNull(bigInt: BigInt): IntNum? = bigInt.toIntOrNull()?.let(::IntNum)

		override fun toRational(a: IntNum): Rational = a.toRational()
		override fun pow(base: IntNum, exp: Int): IntNum = super<Integral.Scope>.pow(base, exp)
	}

	companion object: Scope()

	object ExactScope: Scope(), ExactIntegralScope<IntNum> {
		override fun addOrNull(a: IntNum, b: IntNum): IntNum? = addOrNull(a.int, b.int)?.let(::IntNum)
		override fun negateOrNull(a: IntNum): IntNum? = negateOrNull(a.int)?.let(::IntNum)
		override fun multiplyOrNull(a: IntNum, b: IntNum): IntNum? = multiplyOrNull(a.int, b.int)?.let(::IntNum)

		override fun add(a: IntNum, b: IntNum): IntNum = super<ExactIntegralScope>.add(a, b)
		override fun subtract(a: IntNum, b: IntNum): IntNum = super<ExactIntegralScope>.subtract(a, b)
		override fun multiply(a: IntNum, b: IntNum): IntNum = super<ExactIntegralScope>.multiply(a, b)
		override fun negate(a: IntNum): IntNum = super<ExactIntegralScope>.negate(a)
		override fun abs(a: IntNum): IntNum = super<ExactIntegralScope>.abs(a)
		override fun sqr(a: IntNum): IntNum = super<ExactIntegralScope>.sqr(a)
	}
}

abstract class BaseIntNumScope: Integral.Scope<Int>, Bitwise.Scope<Int> {
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

	override fun not(t: Int): Int = t.inv()
	override infix fun Int.and(other: Int): Int = and(other)
	override infix fun Int.or(other: Int): Int = or(other)
	override infix fun Int.xor(other: Int): Int = xor(other)
	override infix fun Int.shl(bitCount: Int): Int = shl(bitCount)
	override infix fun Int.shr(bitCount: Int): Int = shr(bitCount)
	override fun highestSetBitIndex(t: Int): Int = if (t < 0) 31 else t.iLog2()
	override fun lowestSetBitIndex(t: Int): Int = t.countTrailingZeroBits() - 1
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

@JvmInline
value class LongNum(val long: Long): Integral<LongNum>, Bitwise<LongNum> {
	override fun toInt(): Int = long.toInt()
	override fun toIntOrNull(): Int? = long.toIntOrNull()
	override fun toLong(): Long = long
	override fun toLongOrNull(): Long = long
	override fun toInt128(): Int128 = Int128.fromLong(long)
	override fun toInt128OrNull(): Int128 = toInt128()
	override fun toBigInt(): BigInt = BigInt.fromLong(long)

	override fun toRational(): Rational = Rational.fromLong(long)
	override fun toDouble(): Double = long.toDouble()

	override operator fun plus(addend: LongNum): LongNum = LongNum(long + addend.long)
	override operator fun minus(sub: LongNum): LongNum = LongNum(long - sub.long)
	override operator fun times(multiplier: LongNum): LongNum = LongNum(long * multiplier.long)
	override operator fun div(divisor: LongNum): LongNum = LongNum(long / divisor.long)
	override operator fun rem(divisor: LongNum): LongNum = LongNum(long % divisor.long)

	override operator fun plus(addend: Int): LongNum = LongNum(long + addend)
	override operator fun minus(sub: Int): LongNum = LongNum(long - sub)
	override operator fun times(multiplier: Int) = LongNum(long * multiplier)
	override operator fun div(divisor: Int): LongNum = LongNum(long / divisor)
	override operator fun rem(divisor: Int): Int = (long % divisor).toInt()

	operator fun plus(addend: Long): LongNum = LongNum(long + addend)
	operator fun minus(sub: Long): LongNum = LongNum(long - sub)
	operator fun times(multiplier: Long) = LongNum(long * multiplier)
	operator fun div(divisor: Long): LongNum = LongNum(long / divisor)
	operator fun rem(divisor: Long): Long = long % divisor

	override fun compareTo(other: LongNum): Int = long.compareTo(other.long)

	override fun signum(): Int = long.sign
	override fun unaryMinus(): LongNum = LongNum(-long)

	override fun not(): LongNum = LongNum(long.inv())

	override fun and(other: LongNum): LongNum = LongNum(long.and(other.long))
	override fun or(other: LongNum): LongNum = LongNum(long.or(other.long))
	override fun xor(other: LongNum): LongNum = LongNum(long.xor(other.long))
	override fun shl(bitCount: Int): LongNum = LongNum(long.shl(bitCount))
	override fun shr(bitCount: Int): LongNum = LongNum(long.shr(bitCount))

	override fun highestSetBitIndex(): Int = LongNumScope.highestSetBitIndex(long)
	override fun lowestSetBitIndex(): Int = LongNumScope.lowestSetBitIndex(long)

	override fun toString(): String = long.toString()

	abstract class Scope: Integral.Scope<LongNum>,
		Integral.OpScope<LongNum> by Integral.delegateOpScope(),
		Bitwise.Scope<LongNum> by Bitwise.delegateScope() {
		override val ZERO = LongNum(0)
		override val ONE = LongNum(1)

		override fun isZero(a: LongNum): Boolean = a.long == 0L
		override fun isPositive(a: LongNum): Boolean = a.long > 0L
		override fun isNegative(a: LongNum): Boolean = a.long < 0L

		override fun fromInt(int: Int): LongNum = LongNum(int.toLong())
		override fun fromLong(long: Long): LongNum = LongNum(long)
		override fun fromLongOrNull(long: Long): LongNum = LongNum(long)
		override fun fromInt128(int128: Int128): LongNum = LongNum(int128.toLong())
		override fun fromInt128OrNull(int128: Int128): LongNum? = int128.toLongOrNull()?.let(::LongNum)
		override fun fromBigInt(bigInt: BigInt): LongNum = LongNum(bigInt.toLong())
		override fun fromBigIntOrNull(bigInt: BigInt): LongNum? = bigInt.toLongOrNull()?.let(::LongNum)

		override fun toRational(a: LongNum): Rational = a.toRational()
		override fun pow(base: LongNum, exp: Int): LongNum = super<Integral.Scope>.pow(base, exp)
	}

	companion object: Scope()

	object ExactScope: Scope(), ExactIntegralScope<LongNum> {
		override fun addOrNull(a: LongNum, b: LongNum): LongNum? = addOrNull(a.long, b.long)?.let(::LongNum)
		override fun negateOrNull(a: LongNum): LongNum? = negateOrNull(a.long)?.let(::LongNum)
		override fun multiplyOrNull(a: LongNum, b: LongNum): LongNum? = multiplyOrNull(a.long, b.long)?.let(::LongNum)

		override fun add(a: LongNum, b: LongNum): LongNum = super<ExactIntegralScope>.add(a, b)
		override fun subtract(a: LongNum, b: LongNum): LongNum = super<ExactIntegralScope>.subtract(a, b)
		override fun multiply(a: LongNum, b: LongNum): LongNum = super<ExactIntegralScope>.multiply(a, b)
		override fun negate(a: LongNum): LongNum = super<ExactIntegralScope>.negate(a)
		override fun abs(a: LongNum): LongNum = super<ExactIntegralScope>.abs(a)
		override fun sqr(a: LongNum): LongNum = super<ExactIntegralScope>.sqr(a)
	}
}

abstract class BaseLongNumScope: Integral.Scope<Long>, Bitwise.Scope<Long>, Serializable {
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

	override fun not(t: Long): Long = t.inv()
	override infix fun Long.and(other: Long): Long = and(other)
	override infix fun Long.or(other: Long): Long = or(other)
	override infix fun Long.xor(other: Long): Long = xor(other)
	override infix fun Long.shl(bitCount: Int): Long = shl(bitCount)
	override infix fun Long.shr(bitCount: Int): Long = shr(bitCount)
	override fun highestSetBitIndex(t: Long): Int = if (t < 0) 63 else t.iLog2()
	override fun lowestSetBitIndex(t: Long): Int = t.countTrailingZeroBits() - 1
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

@JvmInline
value class DoubleNum(val double: Double): Fractional<DoubleNum> {
	override fun recip(): DoubleNum = DoubleNum(1.0 / double)

	override fun toRational(): Rational = DoubleNumScope.toRational(double)

	override fun toDouble(): Double = double

	override operator fun plus(addend: DoubleNum): DoubleNum = DoubleNum(double + addend.double)
	override operator fun minus(sub: DoubleNum): DoubleNum = DoubleNum(double - sub.double)
	override operator fun times(multiplier: DoubleNum): DoubleNum = DoubleNum(double * multiplier.double)
	override operator fun div(divisor: DoubleNum): DoubleNum = DoubleNum(double / divisor.double)

	operator fun plus(addend: Double): DoubleNum = DoubleNum(double + addend)
	operator fun minus(sub: Double): DoubleNum = DoubleNum(double - sub)
	operator fun times(multiplier: Double): DoubleNum = DoubleNum(double * multiplier)
	operator fun div(divisor: Double): DoubleNum = DoubleNum(double / divisor)

	override operator fun plus(addend: Int): DoubleNum = DoubleNum(double + addend)
	override operator fun minus(sub: Int): DoubleNum = DoubleNum(double - sub)
	override operator fun times(multiplier: Int): DoubleNum = DoubleNum(double * multiplier)
	override operator fun div(divisor: Int): DoubleNum = DoubleNum(double / divisor)

	override fun compareTo(other: DoubleNum): Int = double.compareTo(other.double)
	override fun signum(): Int = double.sign.toInt()
	override fun unaryMinus(): DoubleNum = DoubleNum(-double)

	override fun toString(): String = double.toString()

	companion object: Fractional.Scope<DoubleNum>,
		Fractional.OpScope<DoubleNum> by Fractional.delegateOpScope(),
		Floating.Scope<DoubleNum> {
		override fun fromInt(int: Int): DoubleNum = DoubleNum(int.toDouble())
		override fun fromLong(long: Long): DoubleNum = DoubleNum(long.toDouble())
		override fun fromLongOrNull(long: Long): DoubleNum? = DoubleNumScope.fromLongOrNull(long)?.let(::DoubleNum)

		override fun fromInt128(int128: Int128): DoubleNum = DoubleNum(int128.toDouble())
		override fun fromInt128OrNull(int128: Int128): DoubleNum? = int128.toDoubleOrNull()?.let(::DoubleNum)

		override fun fromBigInt(bigInt: BigInt): DoubleNum = DoubleNum(bigInt.toDouble())
		override fun fromBigIntOrNull(bigInt: BigInt): DoubleNum? = bigInt.toDoubleOrNull()?.let(::DoubleNum)

		override fun divideInt(a: DoubleNum, b: Int): DoubleNum = DoubleNum(a.double / b)

		override fun pow(base: DoubleNum, exp: Int): DoubleNum = DoubleNum(base.double.pow(exp))

		override val PI: DoubleNum = DoubleNum(kotlin.math.PI)
		override val E: DoubleNum = DoubleNum(kotlin.math.E)
		override fun exp(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.exp(a.double))
		override fun ln(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.ln(a.double))
		override fun log(a: DoubleNum, base: DoubleNum): DoubleNum = DoubleNum(kotlin.math.log(a.double, base.double))

		override fun sin(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.sin(a.double))
		override fun cos(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.cos(a.double))
		override fun tan(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.tan(a.double))
		override fun asin(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.asin(a.double))
		override fun acos(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.acos(a.double))
		override fun atan(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.atan(a.double))
		override fun sinh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.sinh(a.double))
		override fun cosh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.cosh(a.double))
		override fun tanh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.tanh(a.double))
		override fun asinh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.asinh(a.double))
		override fun acosh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.acosh(a.double))
		override fun atanh(a: DoubleNum): DoubleNum = DoubleNum(kotlin.math.atanh(a.double))
		override fun atan2(x: DoubleNum, y: DoubleNum): DoubleNum = DoubleNum(kotlin.math.atan2(x.double, y.double))
	}
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

	override fun pow(base: Double, exp: Int): Double = base.pow(exp)

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
