package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.Bitwise
import com.github.fsbarata.numeric.ExactIntegralScope
import com.github.fsbarata.numeric.Integral
import com.github.fsbarata.numeric.ratio.Rational
import com.github.fsbarata.io.Serializable

expect class BigInt: Integral<BigInt>, Bitwise<BigInt>, Serializable {
	constructor(bytesBE: ByteArray, offset: Int = 0, length: Int = bytesBE.size)

	fun toByteArrayBE(): ByteArray

	fun toString(radix: Int): String

	override fun compareTo(other: BigInt): Int

	override fun toInt(): Int
	override fun toIntOrNull(): Int?
	override fun toLong(): Long
	override fun toLongOrNull(): Long?
	override fun toInt128(): Int128
	override fun toInt128OrNull(): Int128?
	override fun toBigInt(): BigInt
	override fun toDouble(): Double
	fun toDoubleOrNull(): Double?

	override fun toRational(): Rational

	override fun not(): BigInt
	override fun and(other: BigInt): BigInt
	override fun or(other: BigInt): BigInt
	override fun xor(other: BigInt): BigInt
	override fun shl(bitCount: Int): BigInt
	override fun shr(bitCount: Int): BigInt

	override fun signum(): Int
	override fun unaryMinus(): BigInt

	override operator fun plus(addend: Int): BigInt
	operator fun plus(addend: Long): BigInt
	override operator fun plus(addend: BigInt): BigInt
	override operator fun minus(sub: Int): BigInt
	operator fun minus(sub: Long): BigInt
	override operator fun minus(sub: BigInt): BigInt
	override operator fun times(multiplier: Int): BigInt
	operator fun times(multiplier: Long): BigInt
	override operator fun times(multiplier: BigInt): BigInt
	override operator fun div(divisor: Int): BigInt
	operator fun div(divisor: Long): BigInt
	override operator fun div(divisor: BigInt): BigInt
	override operator fun rem(divisor: Int): Int
	operator fun rem(divisor: Long): Long
	override operator fun rem(divisor: BigInt): BigInt
	override fun divRem(divisor: Int): Pair<BigInt, Int>
	fun divRem(divisor: Long): Pair<BigInt, Long>
	override fun divRem(divisor: BigInt): Pair<BigInt, BigInt>

	fun pow(exponent: Int): BigInt

	companion object: Integral.Scope<BigInt>, ExactIntegralScope<BigInt> {
		override val ZERO: BigInt
		override val ONE: BigInt
		val TWO: BigInt
		val TEN: BigInt

		fun fromIntArrayLE(array: IntArray): BigInt

		fun fromString(string: String): BigInt
		fun fromString(string: String, radix: Int): BigInt
		fun parseOrNull(string: String): BigInt?
		fun parseOrNull(string: String, radix: Int): BigInt?

		override fun compare(a: BigInt, b: BigInt): Int

		override fun fromInt(int: Int): BigInt
		override fun fromLong(long: Long): BigInt
		override fun fromLongOrNull(long: Long): BigInt?
		override fun fromInt128(int128: Int128): BigInt
		override fun fromInt128OrNull(int128: Int128): BigInt?
		override fun fromBigInt(bigInt: BigInt): BigInt
		override fun fromBigIntOrNull(bigInt: BigInt): BigInt?

		override fun toInt(a: BigInt): Int
		override fun toIntOrNull(a: BigInt): Int?
		override fun toLong(a: BigInt): Long
		override fun toLongOrNull(a: BigInt): Long?
		override fun toInt128(a: BigInt): Int128
		override fun toInt128OrNull(a: BigInt): Int128?
		override fun toBigInt(a: BigInt): BigInt
		override fun toDouble(a: BigInt): Double

		override fun signum(a: BigInt): Int
		override fun negate(a: BigInt): BigInt
		override fun add(a: BigInt, b: BigInt): BigInt
		override fun subtract(a: BigInt, b: BigInt): BigInt
		override fun multiply(a: BigInt, b: BigInt): BigInt
		override fun divRem(a: BigInt, b: BigInt): Pair<BigInt, BigInt>
		override fun divide(a: BigInt, b: BigInt): BigInt
		override fun mod(a: BigInt, b: BigInt): BigInt

		override fun addOrNull(a: BigInt, b: BigInt): BigInt?
		override fun subtractOrNull(a: BigInt, b: BigInt): BigInt?
		override fun multiplyOrNull(a: BigInt, b: BigInt): BigInt?
		override fun negateOrNull(a: BigInt): BigInt?
	}
}
