package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ratio.Rational
import kotlin.math.absoluteValue

object Int128ExactScope: ExactIntegralScope<Int128>, Integral.Scope<Int128> by Int128 {
	override fun addOrNull(a: Int128, b: Int128): Int128? {
		val r = a + b
		if (a.high >= 0 && b.high >= 0 && r.high < 0) return null
		if (a.high < 0 && b.high < 0 && r.high >= 0) return null
		return r
	}

	override fun addOrNull(a: Int128, b: Int): Int128? = addOrNull(a, b.toLong())

	fun addOrNull(a: Int128, b: Long): Int128? {
		val r = a + b
		if (a.high >= 0 && b > 0 && r.high < 0) return null
		if (a.high < 0 && b < 0 && r.high >= 0) return null
		return r
	}

	override fun negateOrNull(a: Int128): Int128? = if (a == Int128.MIN_VALUE) null else -a

	override fun subtractOrNull(a: Int128, b: Int128): Int128? {
		val r = a - b
		if (a.high >= 0 && b.high < 0 && r.high < 0) return null
		if (a.high < 0 && b.high >= 0 && r.high >= 0) return null
		return r
	}

	override fun subtractOrNull(a: Int128, b: Int): Int128? = subtractOrNull(a, b.toLong())

	fun subtractOrNull(a: Int128, b: Long): Int128? {
		val r = a - b
		if (a.high >= 0 && b < 0 && r.high < 0) return null
		if (a.high < 0 && b > 0 && r.high >= 0) return null
		return r
	}

	override fun multiplyOrNull(a: Int128, b: Int): Int128? = multiplyOrNull(a, b.toLong())

	fun multiplyOrNull(a: Int128, b: Long): Int128? {
		if (b == 0L) return Int128.ZERO
		if (b == 1L) return a
		if (b == -1L) return negateOrNull(a)
		return multiplyOrNull(a, Int128.fromLong(b))
	}

	override fun multiplyOrNull(a: Int128, b: Int128): Int128? {
		if (a.isZero()) return Int128.ZERO
		if (b.isZero()) return Int128.ZERO

		val aLog2 = a.iLog2abs()
		val bLog2 = b.iLog2abs()
		val rLog2 = aLog2 + bLog2
		return when {
			rLog2 < 126 -> a * b
			rLog2 == 126 ->
				if (aLog2 == bLog2) {
					val aInSqrBounds = inSqrBounds(a)
					val bInSqrBounds = inSqrBounds(b)
					if (aInSqrBounds && bInSqrBounds) a * b
					else if (!aInSqrBounds && !bInSqrBounds) null
					else multiplyOrNull63p63(a.low, b.low)
				} else if (aLog2 > bLog2) multiplyOrNull126(a.low, a.high, b.low)
				else multiplyOrNull126(b.low, b.high, a.low)

			rLog2 == 127 && a.signum() != b.signum() ->
				if (is2Power127(a, b, aLog2, bLog2)) Int128.MIN_VALUE else null

			else -> null
		}
	}

	private fun is2Power127(a: Int128, b: Int128, aLog2: Int, bLog2: Int): Boolean {
		return when {
			aLog2 > bLog2 -> {
				(if (bLog2 == 63) b.low == Long.MIN_VALUE else 1L.shl(bLog2) == b.low.absoluteValue)
						&& a.low == 0L && a.high.absoluteValue == 1L.shl(aLog2 - 64)
			}

			else -> {
				(if (aLog2 == 63) a.low == Long.MIN_VALUE else 1L.shl(aLog2) == a.low.absoluteValue)
						&& b.low == 0L && b.high.absoluteValue == 1L.shl(bLog2 - 64)
			}
		}
	}

	private fun multiplyOrNull126(aLow: Long, aHigh: Long, bLow: Long): Int128? {
		if (bLow == 1L) return Int128(aLow, aHigh)
		if (bLow == -1L) return negateOrNull(Int128(aLow, aHigh))
		val r = multiplyFull(aLow, bLow)
		val hl = multiplyFull(if (aLow < 0) aHigh + 1 else aHigh, bLow)
		if (hl.high != 0L && hl.high != -1L) return null
		val z64 = hl + r.high
		val high = z64.toLongOrNull() ?: return null
		val resultIsNegative = aHigh < 0 != bLow < 0
		if (high < 0 && !resultIsNegative) return null
		if (high >= 0 && resultIsNegative) return null
		return Int128(r.low, high)
	}

	private fun multiplyOrNull63p63(a: Long, b: Long): Int128? {
		// Assume both a and b are within [2^63,2^64[ magnitude
		// Multiply (2^64-abs(a)) * (2^64-abs(b))
		// Adjust sign
		val aa = a.absoluteValue
		val ba = b.absoluteValue
		val t = -aa - ba
		if (t < 0) return null

		val aaba = multiplyFull(aa, ba)
		val z64 = aaba.high + t
		if (z64 < 0) return null

		val r = Int128(aaba.low, z64)
		return if (a < 0 == b < 0) r else -r
	}

	override fun sqrOrNull(a: Int128): Int128? {
		return if (inSqrBounds(a)) a.sqr() else null
	}

	private fun inSqrBounds(a: Int128): Boolean {
		return when (a.high) {
			0L -> a.low !in (POSITIVE_SQR_LOW_NEGATIVE_MAX + 1)..<0
			-1L -> a.low !in 0..<NEGATIVE_SQR_LOW_POSITIVE_MIN
			else -> false
		}
	}

	override fun add(a: Int128, b: Int128): Int128 = addExact(a, b)
	override fun subtract(a: Int128, b: Int128): Int128 = subtractExact(a, b)
	override fun multiply(a: Int128, b: Int128): Int128 = multiplyExact(a, b)
	override fun negate(a: Int128): Int128 = negateExact(a)
	override fun abs(a: Int128): Int128 = absExact(a)
	override fun sqr(a: Int128): Int128 = sqrExact(a)

	override fun addInt(a: Int128, b: Int): Int128 = addIntExact(a, b)
	override fun subtractInt(a: Int128, b: Int): Int128 = subtractIntExact(a, b)
	override fun multiplyInt(a: Int128, b: Int): Int128 = multiplyIntExact(a, b)

	override fun toRational(a: Int128): Rational = a.toRational()

	private const val POSITIVE_SQR_LOW_NEGATIVE_MAX = -5402926248376769404
	private const val NEGATIVE_SQR_LOW_POSITIVE_MIN = 5402926248376769404
}
