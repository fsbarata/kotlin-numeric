package com.github.fsbarata.numeric

import kotlin.math.absoluteValue

fun <T> Integral.Scope<T>.greatestCommonDenominator(a: T, b: T): T =
	greatestCommonDenominatorRec(abs(a), abs(b))

private tailrec fun <T> Integral.Scope<T>.greatestCommonDenominatorRec(a: T, b: T): T =
	if (b == ZERO) a
	else greatestCommonDenominatorRec(b, if (b > a) a else a % b)


inline fun Int.divRem(other: Int): Pair<Int, Int> {
	val d = this / other
	val r = this - d * other
	return Pair(d, r)
}

inline fun Long.divRem(other: Long): Pair<Long, Long> {
	val d = this / other
	val r = this - d * other
	return Pair(d, r)
}

inline fun Long.divRem(other: Int): Pair<Long, Int> {
	val d = this / other
	val r = (this - d * other).toInt()
	return Pair(d, r)
}

inline fun Long.toIntOrNull(): Int? {
	val int = toInt()
	return if (this != int.toLong()) null else int
}

inline fun Double.signum(): Int = when {
	this > 0.0 -> 1
	this < 0.0 -> -1
	else -> 0
}

internal fun canConvertLongToDoubleExact(value: Long): Boolean {
	// Check if the value is within 2^53
	return value >= -9007199254740992L && value <= 9007199254740992L
}

fun frexp(a: Double): Pair<Long, Int> {
	if (a == 0.0) return Pair(0L, 0)

	val bits = a.toBits()
	val isPositive = bits and Long.MIN_VALUE == 0L
	val exponent = ((bits ushr 52) and 0x7FF).toInt() - 1023 - 52
	val mantissaBits = bits and 0xFFFFFFFFFFFFFL or (1L shl 52)
	return Pair(if (isPositive) mantissaBits else -mantissaBits, exponent)
}


fun addOrNull(x: Int, y: Int): Int? {
	val r = x + y
	return if (!addOverflows(x, y, r)) r else null
}

internal inline fun addOverflows(x: Int, y: Int, r: Int): Boolean {
	return (x xor y) >= 0 && (r xor x) < 0
}

fun addOrNull(x: Long, y: Long): Long? {
	val r = x + y
	return if (!addOverflows(x, y, r)) r else null
}

internal inline fun addOverflows(x: Long, y: Long, r: Long): Boolean {
	return (x xor y) >= 0 && (r xor x) < 0
}

fun subtractOrNull(x: Int, y: Int): Int? {
	return addOrNull(x, negateOrNull(y) ?: return null)
}

fun subtractOrNull(x: Long, y: Long): Long? {
	return addOrNull(x, negateOrNull(y) ?: return null)
}

fun negateOrNull(x: Int): Int? {
	return if (x == Int.MIN_VALUE) null else -x
}

fun negateOrNull(x: Long): Long? {
	return if (x == Long.MIN_VALUE) null else -x
}

fun multiplyOrNull(x: Int, y: Int): Int? {
	return (x.toLong() * y.toLong()).toIntOrNull()
}

// From java.lang.Math.multiplyExact
fun multiplyOrNull(x: Long, y: Long): Long? {
	val r = x * y
	val ax = x.absoluteValue
	val ay = y.absoluteValue
	if (((ax or ay) ushr 31 != 0L)) {
		if (((y != 0L) && (r / y != x)) || (x == Long.MIN_VALUE && y == -1L)) return null
	}
	return r
}


expect fun scalb(a: Double, scaleFactor: Int): Double

fun Int.iLog2(): Int = toLong().iLog2()
fun Long.iLog2(): Int {
	if (this <= 0) throw ArithmeticException("Cannot compute log2 of $this")
	return 63 - countLeadingZeroBits()
}

fun Int.iLog2abs(): Int = toLong().iLog2abs()
fun Long.iLog2abs(): Int {
	return when {
		this == Long.MIN_VALUE -> 63
		else -> absoluteValue.iLog2()
	}
}

fun Int.iLog10(): Int = toLong().iLog10()
fun Long.iLog10(): Int = when {
	this <= 0 -> throw ArithmeticException("Cannot compute iLog10 of $this")
	this < 10 -> 0
	this < 100 -> 1
	this < 1000 -> 2
	this < 10_000 -> 3
	this < 100_000 -> 4
	this < 1000_000 -> 5
	this < 10_000_000 -> 6
	this < 100_000_000 -> 7
	this < 1000_000_000 -> 8
	this < 10_000_000_000 -> 9
	this < 100_000_000_000 -> 10
	this < 1000_000_000_000 -> 11
	this < 10_000_000_000_000 -> 12
	this < 100_000_000_000_000 -> 13
	this < 1000_000_000_000_000 -> 14
	this < 10_000_000_000_000_000 -> 15
	this < 100_000_000_000_000_000 -> 16
	this < 1000_000_000_000_000_000 -> 17
	else -> 18
}


internal val TEN_POWERS_INT by lazy {
	intArrayOf(
		1, 10, 100,
		1000, 10_000, 100_000,
		1000_000, 10_000_000, 100_000_000,
		1000_000_000,
	)
}

internal val TEN_POWERS_LONG by lazy {
	longArrayOf(
		1L, 10L, 100L,
		1_000L, 10_000L, 100_000L,
		1_000_000L, 10_000_000L, 100_000_000L,
		1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
		1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
		1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
		1_000_000_000_000_000_000L,
	)
}