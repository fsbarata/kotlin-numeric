package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Rational
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.math.sign

object BigIntegerNumScope: Integral.Scope<BigInteger>, Serializable {
	private fun readResolve(): Any = BigIntegerNumScope

	override fun add(a: BigInteger, b: BigInteger): BigInteger = a.add(b)
	override fun negate(a: BigInteger): BigInteger = a.negate()
	override fun signum(a: BigInteger): Int = a.signum()
	override fun subtract(a: BigInteger, b: BigInteger): BigInteger = a.subtract(b)
	override fun multiply(a: BigInteger, b: BigInteger): BigInteger = a.multiply(b)
	override fun mod(a: BigInteger, b: BigInteger): BigInteger = a.mod(b)

	override fun toInt(a: BigInteger): Int = a.toInt()
	override fun toIntOrNull(a: BigInteger): Int? = when {
		a.bitLength() < Int.SIZE_BITS -> a.toInt()
		else -> null
	}

	override fun toLong(a: BigInteger): Long = a.toLong()
	override fun toLongOrNull(a: BigInteger): Long? = when {
		a.bitLength() < Long.SIZE_BITS -> a.toLong()
		else -> null
	}

	override fun toInt128(a: BigInteger): Int128 =
		Int128(toLong(a), toLong(a.shiftRight(64)))

	override fun toInt128OrNull(a: BigInteger): Int128? = when {
		a.bitLength() < Int128.SIZE_BITS -> toInt128(a)
		else -> null
	}

	override fun toBigInt(a: BigInteger): BigInt = BigInt(a)

	override fun fromLong(long: Long): BigInteger = BigInteger.valueOf(long)
	override fun fromLongOrNull(long: Long): BigInteger? = BigInteger.valueOf(long)

	override fun fromInt128(int128: Int128): BigInteger = int128.toBigInt().bigInteger
	override fun fromInt128OrNull(int128: Int128): BigInteger? = fromInt128(int128)

	override fun fromBigInt(bigInt: BigInt): BigInteger = bigInt.bigInteger
	override fun fromBigIntOrNull(bigInt: BigInt): BigInteger? = bigInt.bigInteger

	override fun divRem(a: BigInteger, b: BigInteger): Pair<BigInteger, BigInteger> {
		val (d, r) = a.divideAndRemainder(b)
		return Pair(d, r)
	}

	override fun divide(a: BigInteger, b: BigInteger): BigInteger = a.divide(b)

	override fun compare(a: BigInteger, b: BigInteger): Int = a.compareTo(b)

	override fun toDouble(a: BigInteger): Double = a.toDouble()

}

object BigDecimalNumScope: Fractional.Scope<BigDecimal>, Serializable {
	private fun readResolve(): Any = BigDecimalNumScope

	override val ZERO: BigDecimal = BigDecimal.ZERO
	override val ONE: BigDecimal = BigDecimal.ONE

	override fun add(a: BigDecimal, b: BigDecimal): BigDecimal = a.add(b)

	override fun signum(a: BigDecimal): Int = a.signum()

	override fun negate(a: BigDecimal): BigDecimal = a.negate()

	override fun multiply(a: BigDecimal, b: BigDecimal): BigDecimal = a.multiply(b)
	override fun compare(a: BigDecimal, b: BigDecimal): Int = a.compareTo(b)

	override fun recip(a: BigDecimal): BigDecimal = divide(ONE, a)
	override fun divide(a: BigDecimal, b: BigDecimal): BigDecimal = a.divide(b, MathContext.DECIMAL64)

	override fun fromInt(int: Int): BigDecimal = int.toBigDecimal()
	override fun fromLong(long: Long): BigDecimal = BigDecimal.valueOf(long)
	override fun fromLongOrNull(long: Long): BigDecimal = fromLong(long)

	override fun fromInt128(int128: Int128): BigDecimal = fromBigInt(int128.toBigInt())
	override fun fromInt128OrNull(int128: Int128): BigDecimal? = fromBigIntOrNull(int128.toBigInt())

	override fun fromBigInt(bigInt: BigInt): BigDecimal = bigInt.bigInteger.toBigDecimal()
	override fun fromBigIntOrNull(bigInt: BigInt): BigDecimal? = fromBigInt(bigInt)

	override fun toDouble(a: BigDecimal): Double = a.toDouble()

	override fun toRational(a: BigDecimal): Rational {
		return when (a.scale().sign) {
			0 -> Rational.create(BigInt(a.unscaledValue()), BigInt.ONE)
			-1 -> Rational.create(BigInt(a.unscaledValue() * BigInteger.TEN.pow(-a.scale())), BigInt.ONE)
			else -> Rational.create(BigInt(a.unscaledValue()), BigInt(BigInteger.TEN.pow(a.scale())))
		}
	}
}
