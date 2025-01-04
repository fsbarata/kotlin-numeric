package com.github.fsbarata.numeric.ints

import com.github.fsbarata.io.Serializable
import com.github.fsbarata.numeric.BigIntegerNumScope
import com.github.fsbarata.numeric.Bitwise
import com.github.fsbarata.numeric.ExactIntegralScope
import com.github.fsbarata.numeric.Integral
import com.github.fsbarata.numeric.ratio.Rational
import java.math.BigDecimal
import java.math.BigInteger

actual class BigInt(val bigInteger: BigInteger): Integral<BigInt>, Bitwise<BigInt>, Serializable {
	constructor(int: Int): this(BigInteger.valueOf(int.toLong()))
	constructor(long: Long): this(BigInteger.valueOf(long))

	actual constructor(bytesBE: ByteArray, offset: Int, length: Int):
			this(BigInteger(if (length == 0) byteArrayOf(0) else bytesBE, offset, length))

	constructor(string: String): this(BigInteger(string, 10))

	actual fun toByteArrayBE(): ByteArray {
		if (bigInteger.signum() == 0) return byteArrayOf()
		return bigInteger.toByteArray()
	}

	fun toByteArrayLE(): ByteArray = toByteArrayBE().reversedArray()

	fun toIntArrayLE(): IntArray {
		val list = mutableListOf<Int>()
		var a = bigInteger
		while (a.signum() != 0) {
			val v = a.toInt()
			list += v
			if (a == BigInteger.valueOf(-1)) break
			a = a.shiftRight(32)
		}
		return list.toIntArray()
	}

	actual override fun not(): BigInt = BigInt(bigInteger.not())
	actual override fun and(other: BigInt): BigInt = BigInt(bigInteger.and(other.bigInteger))
	actual override fun or(other: BigInt): BigInt = BigInt(bigInteger.or(other.bigInteger))
	actual override fun xor(other: BigInt): BigInt = BigInt(bigInteger.xor(other.bigInteger))
	actual override fun shl(bitCount: Int): BigInt = BigInt(bigInteger.shiftLeft(bitCount))
	actual override fun shr(bitCount: Int): BigInt = BigInt(bigInteger.shiftRight(bitCount))

	actual override fun highestSetBitIndex(): Int = when {
		isZero() -> 0
		isNegative() -> bigInteger.bitLength()
		else -> bigInteger.bitLength() - 1
	}

	actual override fun lowestSetBitIndex(): Int = bigInteger.getLowestSetBit()


	fun iLog2(): Int {
		return if (!isPositive()) throw ArithmeticException("Cannot compute iLog2 of negative number")
		else bigInteger.bitLength() - 1
	}

	actual override fun compareTo(other: BigInt) = bigInteger.compareTo(other.bigInteger)

	actual override operator fun plus(addend: BigInt) =
		BigInt(bigInteger.add(addend.bigInteger))

	actual override operator fun minus(sub: BigInt) =
		BigInt(bigInteger.subtract(sub.bigInteger))

	actual override operator fun unaryMinus() = BigInt(bigInteger.negate())
	actual override operator fun times(multiplier: BigInt) =
		BigInt(bigInteger.multiply(multiplier.bigInteger))

	override fun abs() = BigInt(bigInteger.abs())
	actual override fun signum(): Int = bigInteger.signum()

	actual override operator fun div(divisor: BigInt) = BigInt(bigInteger.divide(divisor.bigInteger))
	actual override operator fun rem(divisor: BigInt) = BigInt(bigInteger.remainder(divisor.bigInteger))

	actual override fun divRem(divisor: BigInt): Pair<BigInt, BigInt> {
		val divRem = bigInteger.divideAndRemainder(divisor.bigInteger)
		return Pair(BigInt(divRem[0]), BigInt(divRem[1]))
	}

	actual override fun toInt(): Int = bigInteger.toInt()
	actual override fun toIntOrNull(): Int? = BigIntegerNumScope.toIntOrNull(bigInteger)

	actual override fun toLong(): Long = bigInteger.toLong()
	actual override fun toLongOrNull(): Long? = BigIntegerNumScope.toLongOrNull(bigInteger)

	actual override fun toInt128(): Int128 = Int128.fromBigInt(this)
	actual override fun toInt128OrNull(): Int128? = Int128.fromBigIntOrNull(this)

	actual override fun toBigInt(): BigInt = this

	actual override fun toDouble(): Double = bigInteger.toDouble()
	actual fun toDoubleOrNull(): Double? = when {
		bigInteger.bitLength() < 53 -> bigInteger.toDouble()
		else -> null
	}

	actual override fun toRational(): Rational = Rational.fromBigInt(this)
	fun toBigDecimal(): BigDecimal = BigDecimal(bigInteger)

	actual override operator fun plus(addend: Int): BigInt = plus(BigInt(addend))
	actual operator fun plus(addend: Long): BigInt = plus(BigInt(addend))

	actual override operator fun minus(sub: Int): BigInt = minus(BigInt(sub))
	actual operator fun minus(sub: Long): BigInt = minus(BigInt(sub))

	actual override operator fun times(multiplier: Int): BigInt = times(BigInt(multiplier))
	actual operator fun times(multiplier: Long): BigInt = times(BigInt(multiplier))

	actual override operator fun div(divisor: Int): BigInt = div(BigInt(divisor))
	actual operator fun div(divisor: Long): BigInt = div(BigInt(divisor))

	actual override operator fun rem(divisor: Int): Int = rem(BigInt(divisor)).toInt()
	actual operator fun rem(divisor: Long): Long = rem(BigInt(divisor)).toLong()
	actual override fun divRem(divisor: Int): Pair<BigInt, Int> {
		val (div, rem) = divRem(BigInt(divisor))
		return Pair(div, rem.bigInteger.intValueExact())
	}

	actual fun divRem(divisor: Long): Pair<BigInt, Long> {
		val (div, rem) = divRem(BigInt(divisor))
		return Pair(div, rem.bigInteger.longValueExact())
	}

	actual fun pow(exponent: Int): BigInt {
		return when {
			exponent == 0 -> ONE
			bigInteger == BigInteger.ONE -> this
			bigInteger == -BigInteger.ONE -> if (exponent.and(1) == 0) ONE else this
			else -> BigInt(bigInteger.pow(exponent))
		}
	}

	override fun toString(): String = toString(10)
	actual fun toString(radix: Int): String = bigInteger.toString(radix)

	override fun equals(other: Any?) =
		(other is BigInt) && other.bigInteger == bigInteger

	override fun hashCode() = bigInteger.hashCode()

	actual companion object:
		Integral.Scope<BigInt>, ExactIntegralScope<BigInt>,
		Bitwise.Scope<BigInt> by Bitwise.delegateScope(),
		Integral.OpScope<BigInt> by Integral.delegateOpScope(),
		Serializable {
		actual override val ZERO = BigInt(BigInteger.ZERO)
		actual override val ONE = BigInt(BigInteger.ONE)
		actual val TWO = BigInt(BigInteger.valueOf(2))
		actual val TEN = BigInt(BigInteger.TEN)

		actual override fun compare(a: BigInt, b: BigInt): Int = a.compareTo(b)

		actual override fun fromInt(int: Int) = BigInt(int)
		actual override fun fromLong(long: Long) = BigInt(long)
		actual override fun fromLongOrNull(long: Long): BigInt? = BigInt(long)
		actual override fun fromInt128(int128: Int128): BigInt = int128.toBigInt()
		actual override fun fromInt128OrNull(int128: Int128): BigInt? = int128.toBigInt()
		actual override fun fromBigInt(bigInt: BigInt) = bigInt
		actual override fun fromBigIntOrNull(bigInt: BigInt): BigInt? = bigInt

		actual fun fromIntArrayLE(array: IntArray): BigInt {
			if (array.isEmpty()) return ZERO

			return BigInt(
				array.foldIndexed(BigInteger.ZERO) { index, a, b ->
					(if (index == array.lastIndex) BigInteger.valueOf(b.toLong())
					else BigInteger.valueOf(unsignedAsLong(b)))
						.shiftLeft(index * 32) or a
				}
			)
		}

		actual fun fromString(string: String) = BigInt(string)
		actual fun fromString(string: String, radix: Int) = BigInt(BigInteger(string, radix))
		actual fun parseOrNull(string: String): BigInt? = parseOrNull(string, 10)
		actual fun parseOrNull(string: String, radix: Int): BigInt? =
			try {
				BigInt(string)
			} catch (_: Throwable) {
				null
			}

		actual override fun toInt(a: BigInt): Int = a.toInt()
		actual override fun toIntOrNull(a: BigInt): Int? = a.toIntOrNull()
		actual override fun toLong(a: BigInt): Long = a.toLong()
		actual override fun toLongOrNull(a: BigInt): Long? = a.toLongOrNull()
		actual override fun toInt128(a: BigInt): Int128 = a.toInt128()
		actual override fun toInt128OrNull(a: BigInt): Int128? = a.toInt128OrNull()
		actual override fun toBigInt(a: BigInt): BigInt = a.toBigInt()

		actual override fun toDouble(a: BigInt): Double = a.toDouble()

		actual override fun divRem(a: BigInt, b: BigInt): Pair<BigInt, BigInt> = a.divRem(b)
		actual override fun mod(a: BigInt, b: BigInt): BigInt = a % b

		actual override fun addOrNull(a: BigInt, b: BigInt): BigInt? = add(a, b)
		actual override fun subtractOrNull(a: BigInt, b: BigInt): BigInt? = subtract(a, b)
		actual override fun multiplyOrNull(a: BigInt, b: BigInt): BigInt? = multiply(a, b)
		actual override fun negateOrNull(a: BigInt): BigInt? = negate(a)
	}
}