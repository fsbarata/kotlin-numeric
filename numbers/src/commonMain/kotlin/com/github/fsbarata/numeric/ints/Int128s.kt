package com.github.fsbarata.numeric.ints

// Output in [-1..0]
fun addHigh(x: Long, y: Long, r: Long = x + y): Long {
	return when {
		x < 0 -> y.signWord() or r.signWord()
		y < 0 -> r.signWord()
		else -> 0
	}
}

fun addFull(x: Long, y: Long): Int128 {
	if (x == 0L) return y.toInt128()
	if (y == 0L) return x.toInt128()
	val sumLow = x + y
	val sumHigh = addHigh(x, y, sumLow)
	return Int128(low = sumLow, high = sumHigh)
}

fun multiplyFull(x: Long, y: Long): Int128 {
	if (x == 0L || y == 0L) return Int128.ZERO
	val xHigh = x shr 32
	val yHigh = y shr 32
	val xLow = x and LONG_LOW_MASK
	val yLow = y and LONG_LOW_MASK
	val z0 = xLow * yLow
	val carry0 = z0 ushr 32
	var z64 = xHigh * yHigh
	var z32 = xHigh * yLow + carry0
	if (x < 0 || y < 0) {
		z64 += z32 shr 32
		z32 = (z32 and LONG_LOW_MASK) + xLow * yHigh
		z64 += z32 shr 32
	} else {
		z32 += xLow * yHigh
		z64 += z32 ushr 32
	}
	val low = (z0 and LONG_LOW_MASK) or (z32 and LONG_LOW_MASK).shl(32)
	return Int128(low, z64)
}

fun sqrFull(a: Long): Int128 {
	if (a == 0L) return Int128.ZERO
	val xHigh = a shr 32
	val xLow = a and LONG_LOW_MASK
	val z0 = xLow * xLow
	val carry0 = z0 ushr 32
	var z64 = xHigh * xHigh
	val xl = xHigh * xLow
	var z32 = xl + carry0
	if (a < 0) {
		z64 += z32 shr 32
		z32 = (z32 and LONG_LOW_MASK) + xl
		z64 += z32 shr 32
	} else {
		z32 += xl
		z64 += z32 ushr 32
	}
	val low = (z0 and LONG_LOW_MASK) or (z32 and LONG_LOW_MASK).shl(32)
	return Int128(low, z64)
}

fun Int.toInt128() = Int128.fromInt(this)
fun Long.toInt128() = Int128.fromLong(this)