package com.github.fsbarata.numeric.ints

internal fun negate128(low: Long, high: Long): Int128 {
	return Int128(low = -low, high = negate128High(low, high))
}

internal fun negate128High(low: Long, high: Long): Long {
	return if (low != 0L) -(high + 1) else -high
}

internal fun addUnsignedCarry(x: Long, y: Long, r: Long = x + y): Int {
	return when {
		x < 0 -> if (y < 0 || r >= 0) 1 else 0
		y < 0 -> if (r >= 0) 1 else 0
		else -> 0
	}
}

internal fun multiplyUnsignedFull(x: Long, y: Long): Int128 {
	if (x == 0L || y == 0L) return Int128.ZERO
	// binary multiplication of Int parts treating them as unsigned
	val xHigh = x ushr 32
	val yHigh = y ushr 32
	val xLow = x and LONG_LOW_MASK
	val yLow = y and LONG_LOW_MASK
	val z0 = xLow * yLow
	var z64 = xHigh * yHigh
	var z32 = (z0 ushr 32) + xHigh * yLow
	z64 += z32 ushr 32
	z32 = (z32 and LONG_LOW_MASK) + xLow * yHigh
	z64 += z32 ushr 32
	val low = (z0 and LONG_LOW_MASK) or (z32 and LONG_LOW_MASK).shl(32)
	return Int128(low, z64)
}

internal fun sqrUnsignedFull(a: Long): Int128 {
	if (a == 0L) return Int128.ZERO
	// binary multiplication of Int parts as if they were Unsigned
	val xHigh = a ushr 32
	val xLow = a and LONG_LOW_MASK
	val z0 = xLow * xLow
	val zhl = xLow * xHigh
	var z64 = xHigh * xHigh
	var z32 = (z0 ushr 32) + zhl
	z64 += z32 ushr 32
	z32 = (z32 and LONG_LOW_MASK) + zhl
	z64 += z32 ushr 32
	val low = (z0 and LONG_LOW_MASK) or (z32 and LONG_LOW_MASK).shl(32)
	return Int128(low, z64)
}

internal inline fun Long.signWord() = if (this < 0) -1L else 0L


