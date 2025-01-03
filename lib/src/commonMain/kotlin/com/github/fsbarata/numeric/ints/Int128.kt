package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ratio.Rational
import com.github.fsbarata.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Int128(val low: Long, val high: Long): Integral<Int128>, Bitwise<Int128>, Serializable {
	override fun toString(): String = toString(10)

	fun toString(radix: Int): String {
		return toLongOrNull()?.toString(radix) ?: toString128(radix)
	}

	private fun toString128(radix: Int): String {
		var d = this
		return buildString {
			var r = 0
			while (d.signum() != 0) {
				val dr = d.divRem(radix)
				d = dr.first
				r = dr.second
				insert(0, r.absoluteValue.digitToChar(radix))
			}
			if (r < 0) insert(0, '-')
		}
	}

	override fun signum(): Int = when {
		high < 0 -> -1
		high > 0 -> 1
		low == 0L -> 0
		else -> 1
	}

	override fun isZero() = low == 0L && high == 0L

	override fun unaryMinus(): Int128 = negate128(low, high)

	override fun compareTo(other: Int128): Int {
		val compareHigh = high.compareTo(other.high)
		return when {
			compareHigh != 0 -> compareHigh
			low < 0 && other.low >= 0 -> 1
			other.low < 0 && low >= 0 -> -1
			else -> low.compareTo(other.low)
		}
	}


	override operator fun plus(addend: Int): Int128 = plus(addend.toLong())
	operator fun plus(addend: Long): Int128 {
		return addFull(addend, toLongOrNull() ?: return plus(addend, addend.signWord()))
	}

	override operator fun plus(addend: Int128): Int128 = plus(addend.low, addend.high)

	private fun plus(addendLow: Long, addendHigh: Long): Int128 {
		if (addendHigh == 0L && addendLow == 0L) return this
		val sumLow = low + addendLow
		val sumHigh = high + addendHigh + addUnsignedCarry(low, addendLow, sumLow)
		return Int128(low = sumLow, high = sumHigh)
	}

	override operator fun minus(sub: Int): Int128 = plus(-sub)
	operator fun minus(sub: Long): Int128 = plus(-sub)
	override operator fun minus(sub: Int128): Int128 {
		return when (sub) {
			this -> ZERO
			MIN_VALUE -> -(sub.minus(this))
			else -> plus(-sub.low, negate128High(sub.low, sub.high))
		}
	}

	override operator fun times(multiplier: Int): Int128 {
		val long = toLongOrNull() ?: return times(fromInt(multiplier))
		val int = long.toInt()
		return if (int.toLong() == long) fromLong(long * multiplier) else multiplyFull(long, multiplier.toLong())
	}

	operator fun times(multiplier: Long): Int128 {
		return multiplyFull(multiplier, toLongOrNull() ?: return times(multiplier, multiplier.signWord()))
	}

	override operator fun times(multiplier: Int128): Int128 = times(multiplier.low, multiplier.high)

	private fun times(multiplierLow: Long, multiplierHigh: Long): Int128 {
		val r = multiplyUnsignedFull(low, multiplierLow)
		val z64 = high * multiplierLow + low * multiplierHigh
		return Int128(r.low, r.high + z64)
	}

	override fun sqr(): Int128 {
		val r = sqrUnsignedFull(low)
		val h = this.low * high
		return Int128(r.low, r.high + h + h)
	}

	fun iLog2(): Int {
		return when (high) {
			0L -> if (low < 0) 63 else low.iLog2()
			else -> high.iLog2() + 64
		}
	}

	fun iLog2abs(): Int {
		return when (high) {
			0L -> if (low < 0) 63 else low.iLog2()
			-1L -> when {
				low < 0 -> low.iLog2abs()
				low == 0L -> 64
				else -> 63
			}

			Long.MIN_VALUE -> if (low == Long.MIN_VALUE) 127 else 126
			else -> (if (high < 0) negate128High(low, high) else high).iLog2() + 64
		}
	}

	fun iLog10(): Int {
		if (!isPositive()) throw ArithmeticException("Cannot compute log10 of $this")
		return toLongOrNull()?.iLog10() ?: TEN_POWERS.indexOfLast { this >= it }
	}

	override fun divRem(divisor: Int128): Pair<Int128, Int128> {
		if (divisor.isZero()) throw ArithmeticException("Divide by 0")
		if (isZero()) return Pair(ZERO, ZERO)

		val thisLong = toLongOrNull()
		if (thisLong != null) {
			// dividend is Long
			val divisorLong = divisor.toLongOrNull() ?: return Pair(ZERO, this)
			val r = thisLong.divRem(divisorLong)
			return Pair(fromLong(r.first), fromLong(r.second))
		}

		if (this == divisor) return Pair(ONE, ZERO)
		if (divisor == MIN_VALUE) return Pair(ZERO, this)
		if (this == MIN_VALUE) {
			// special min value case
			val (q, r) = (this + divisor.abs()).performDivision(divisor)
			return Pair(q - divisor.signum(), r)
		}
		return performDivision(divisor)
	}

	private fun performDivision(divisor: Int128): Pair<Int128, Int128> {
		val dividendSign = high < 0
		val divisorSign = divisor.high < 0
		var rem: Int128 = if (dividendSign) -this else this
		var shiftedDivisor = if (divisorSign) -divisor else divisor

		var shifts = rem.iLog2() - shiftedDivisor.iLog2()
		if (shifts < 0) return Pair(ZERO, this)

		shiftedDivisor = shiftedDivisor.shl(shifts)

		var quoHigh = 0L
		var quoLow = 0L
		while (shifts >= 0) {
			val comp = rem - shiftedDivisor
			if (comp.high >= 0) {
				if (shifts >= 64) quoHigh = quoHigh or 1L.shl(shifts - 64)
				else quoLow = quoLow or 1L.shl(shifts)
				rem = comp
				if (rem.isZero()) break
			}

			shiftedDivisor = shiftedDivisor.performShiftRight(1)
			shifts--
		}

		val quo = if (dividendSign != divisorSign) negate128(quoLow, quoHigh) else Int128(quoLow, quoHigh)

		if (dividendSign) rem = -rem
		return Pair(quo, rem)
	}

	override operator fun div(divisor: Int128): Int128 = divRem(divisor).first
	override operator fun rem(divisor: Int128): Int128 = divRem(divisor).second

	override fun divRem(divisor: Int): Pair<Int128, Int> {
		val (q, rem) = divRem(divisor.toLong())
		return Pair(q, rem.toIntOrNull() ?: throw OverflowError("Remainder $rem converting to Int"))
	}

	override operator fun div(divisor: Int): Int128 = divRem(divisor).first
	override operator fun rem(divisor: Int): Int = divRem(divisor).second

	fun divRem(divisor: Long): Pair<Int128, Long> {
		if (divisor == 0L) throw ArithmeticException("Divide by 0")
		if (this == MIN_VALUE) {
			// special min value case
			if (divisor == Long.MIN_VALUE) return Pair(Int128(0, 1), 0L)
			val (q, rem) = (plus(divisor.absoluteValue, 0L)).divRem(divisor)
			return Pair(q - divisor.sign, rem)
		}
		val long = toLongOrNull()
		if (long != null) {
			val (q, rem) = long.divRem(divisor)
			return Pair(fromLong(q), rem)
		}
		val (q, rem) = performDivision(fromLong(divisor))
		return Pair(q, rem.toLongOrNull() ?: throw OverflowError("Remainder $rem converting to Long"))
	}

	operator fun div(divisor: Long): Int128 = divRem(divisor).first
	operator fun rem(divisor: Long): Long = divRem(divisor).second

	override fun toInt(): Int = low.toInt()
	override fun toIntOrNull(): Int? = toLongOrNull()?.toIntOrNull()

	override fun toLong(): Long = low
	override fun toLongOrNull(): Long? = when {
		high == 0L && low >= 0 -> low
		high == -1L && low < 0 -> low
		else -> null
	}

	override fun toInt128(): Int128 = this
	override fun toInt128OrNull(): Int128 = this

	override fun toBigInt(): BigInt = BigInt(toByteArrayBE())

	override fun toDouble(): Double {
		return toLongOrNull()?.toDouble()
			?: (scalb(high.toDouble(), 64) +
					(if (low < 0) scalb(1.0, 64) else 0.0) +
					low.toDouble())
	}

	fun toDoubleOrNull(): Double? = if (iLog2() < 53) toDouble() else null

	override fun toRational(): Rational = Rational.fromBigInt(toBigInt())

	override fun not() = Int128(low.inv(), high.inv())
	override infix fun or(other: Int128) = Int128(low or other.low, high or other.high)
	override infix fun and(other: Int128) = Int128(low and other.low, high and other.high)
	override infix fun xor(other: Int128) = Int128(low xor other.low, high xor other.high)

	override fun shl(bitCount: Int): Int128 {
		if (bitCount == 0 || this == ZERO) return this
		if (bitCount < 0) return shr(-bitCount)
		if (bitCount >= 128) return ZERO
		if (bitCount >= 64) return Int128(0L, low.shl(bitCount - 64))
		val l = low.shl(bitCount)
		val u = low.ushr(64 - bitCount)
		val h = high.shl(bitCount) or u
		return Int128(l, h)
	}

	override fun shr(bitCount: Int): Int128 {
		if (bitCount == 0 || this == ZERO) return this
		if (bitCount < 0) return shl(-bitCount)
		if (bitCount >= 128) return ZERO
		if (bitCount >= 64) return Int128(high.shr(bitCount - 64), high.signWord())
		return performShiftRight(bitCount)
	}

	private fun performShiftRight(bitCount: Int): Int128 {
		val u = high.shl(64 - bitCount)
		val l = low.ushr(bitCount) or u
		val h = high.shr(bitCount)
		return Int128(l, h)
	}

	fun ushr(bitCount: Int): Int128 {
		if (bitCount == 0) return this
		if (bitCount < 0) return shl(-bitCount)
		if (bitCount >= 128) return ZERO
		if (bitCount >= 64) return Int128(high.ushr(bitCount - 64), 0L)
		val u = high.shl(64 - bitCount)
		val l = low.ushr(bitCount) or u
		val h = high.ushr(bitCount)
		return Int128(l, h)
	}

	fun toIntArrayLE() = intArrayOf(low.toInt(), low.shr(32).toInt(), high.toInt(), high.shr(32).toInt())

	fun toByteArrayLE() = byteArrayOf(
		low.toByte(),
		low.shr(8).toByte(),
		low.shr(16).toByte(),
		low.shr(24).toByte(),
		low.shr(32).toByte(),
		low.shr(40).toByte(),
		low.shr(48).toByte(),
		low.shr(56).toByte(),
		high.toByte(),
		high.shr(8).toByte(),
		high.shr(16).toByte(),
		high.shr(24).toByte(),
		high.shr(32).toByte(),
		high.shr(40).toByte(),
		high.shr(48).toByte(),
		high.shr(56).toByte(),
	)

	fun toByteArrayBE() = byteArrayOf(
		high.shr(56).toByte(),
		high.shr(48).toByte(),
		high.shr(40).toByte(),
		high.shr(32).toByte(),
		high.shr(24).toByte(),
		high.shr(16).toByte(),
		high.shr(8).toByte(),
		high.toByte(),
		low.shr(56).toByte(),
		low.shr(48).toByte(),
		low.shr(40).toByte(),
		low.shr(32).toByte(),
		low.shr(24).toByte(),
		low.shr(16).toByte(),
		low.shr(8).toByte(),
		low.toByte(),
	)

	companion object: Integral.Scope<Int128>,
		Integral.OpScope<Int128> by Integral.delegateOpScope() {
		override val ZERO = Int128(0, 0)
		override val ONE = Int128(1, 0)
		val TWO = Int128(2, 0)
		val TEN = Int128(10, 0)
		val MIN_VALUE = Int128(0, Long.MIN_VALUE) // -170141183460469231731687303715884105728 = -2^127
		val MAX_VALUE = Int128(-1, Long.MAX_VALUE) // 170141183460469231731687303715884105727 = 2^127-1

		val SIZE_BYTES = 16
		val SIZE_BITS = 128

		fun fromByteArrayLE(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size): Int128 {
			if (length == offset) return ZERO
			val endIndex = length.coerceAtMost(SIZE_BYTES)
			val highByte = byteArray[endIndex - 1]
			val sign = highByte < 0
			val low =
				byteArray.asSequence().drop(offset).take(Long.SIZE_BYTES)
					.fold(if (sign) -1L else 0L) { acc, byte -> acc.shl(8) or (byte.toLong() and 0xFFL) }
			val high =
				byteArray.asSequence().drop(offset + Long.SIZE_BYTES).take(Long.SIZE_BYTES)
					.fold(if (sign) -1L else 0L) { acc, byte -> acc.shl(8) or (byte.toLong() and 0xFFL) }
			return Int128(low, high)
		}

		fun fromByteArrayBE(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size): Int128 {
			if (length == offset) return ZERO
			val startIndex = (length - SIZE_BYTES).coerceAtLeast(offset)
			val highByte = byteArray[startIndex]
			val sign = highByte < 0
			val highSize = (length - offset - Long.SIZE_BYTES).coerceIn(0, Long.SIZE_BYTES)
			val high =
				byteArray.asSequence().drop(startIndex).take(highSize)
					.fold(if (sign) -1L else 0L) { acc, byte -> acc.shl(8) or (byte.toLong() and 0xFFL) }
			val low =
				byteArray.asSequence().drop(startIndex + highSize).take(Long.SIZE_BYTES)
					.fold(if (sign) -1L else 0L) { acc, byte -> acc.shl(8) or (byte.toLong() and 0xFFL) }
			return Int128(low, high)
		}

		fun fromIntArrayLE(intArray: IntArray): Int128 {
			if (intArray.isEmpty()) return ZERO
			val low = (intArray[0].toLong() and LONG_LOW_MASK) or
					(intArray.getOrNull(1) ?: return fromInt(intArray[0])).toLong().shl(32)
			return Int128(
				low, when {
					intArray.size >= 4 -> (intArray[2].toLong() and LONG_LOW_MASK) or intArray[3].toLong().shl(32)
					intArray.size == 3 -> intArray[2].toLong()
					else -> low.signWord()
				}
			)
		}

		override fun fromLong(long: Long): Int128 = Int128(low = long, high = long.signWord())
		override fun fromLongOrNull(long: Long): Int128 = fromLong(long)

		override fun fromInt128(int128: Int128): Int128 = int128
		override fun fromInt128OrNull(int128: Int128): Int128 = int128

		override fun fromBigInt(bigInt: BigInt): Int128 = fromByteArrayBE(bigInt.toByteArrayBE())
		override fun fromBigIntOrNull(bigInt: BigInt): Int128? {
			val bytes = bigInt.toByteArrayBE()
			return if (bytes.size <= SIZE_BYTES) fromByteArrayBE(bytes) else null
		}

		fun fromString(string: String, radix: Int = 10): Int128 {
			return parseOrNull(string, radix)
				?: throw IllegalArgumentException("Cannot parse as Int128 (radix $radix): $string")
		}

		fun parseOrNull(string: String, radix: Int = 10): Int128? {
			require(radix in 2..36)
			if (string.isEmpty()) return null

			val negative = string[0] == '-'
			val offset = if (negative) 1 else 0
			val r = when (radix) {
				10 -> parse10(string, offset)
				2 -> parseBinary(string, 1, offset)
				4 -> parseBinary(string, 2, offset)
				8 -> parseBinary(string, 3, offset)
				16 -> parseBinary(string, 4, offset)
				32 -> parseBinary(string, 5, offset)
				else -> parseAbsGeneric(string, radix, offset)
			} ?: return null
			return if (negative) -r else r
		}

		private fun parseBinary(string: String, scale: Int, offset: Int): Int128? {
			val radix = 1.shl(scale)
			var r = ZERO
			for (i in offset..<string.length) {
				r = r.shl(scale) + (string[i].digitToIntOrNull(radix) ?: return null)
			}
			return r
		}

		private fun parse10(string: String, offset: Int): Int128? {
			val most =
				if (string.length < 37 + offset) 0L
				else string.substring(offset, string.length - 36).toLongOrNull() ?: return null
			val mid =
				if (string.length < 19 + offset) 0L
				else string.substring(maxOf(offset, string.length - 36), string.length - 18).toLongOrNull()
					?: return null
			val least =
				string.substring(maxOf(offset, string.length - 18), string.length).toLongOrNull()
					?: return null
			val a = Int128ExactScope.multiplyOrNull(TEN_POWERS[36], most) ?: return null
			val b = multiplyFull(TEN_POWERS_LONG[18], mid)
			return Int128ExactScope.addOrNull(a, b + least)
		}

		private fun parseAbsGeneric(string: String, radix: Int, offset: Int): Int128? {
			var r = ZERO
			for (i in offset..<string.length) {
				r = (r * radix) + (string[i].digitToIntOrNull(radix) ?: return null)
			}
			return r
		}

		override fun pow(base: Int128, exp: Int): Int128 {
			val long = base.toLongOrNull()
			return when (long) {
				1L -> ONE
				-1L -> if (exp.and(1) == 0) ONE else base
				10L -> tenPower(exp)
				-10L -> if (exp.and(1) == 0) tenPower(exp) else -tenPower(exp)
				else -> super<Integral.Scope>.pow(base, exp)
			}
		}

		fun tenPower(exponent: Int): Int128 {
			if (exponent < 0) throw ArithmeticException("Negative exponent")
			if (exponent <= MAX_POW10) return TEN_POWERS[exponent]
			var r = TEN_POWERS[MAX_POW10]
			var n = exponent - MAX_POW10
			while (n > 0) {
				r *= TEN_POWERS[minOf(n, MAX_POW10)]
				n -= MAX_POW10
			}
			return r
		}

		const val MAX_POW10 = 38
		private val TEN_POWERS: Array<Int128> =
			Array(MAX_POW10 + 1) { index ->
				when (index) {
					in 1..18 -> fromLong(TEN_POWERS_LONG[index])
					in 19..36 -> multiplyFull(TEN_POWERS_LONG[18], TEN_POWERS_LONG[index - 18])
					37 -> Int128(68739955140067328, 542101086242752217)
					38 -> Int128(687399551400673280, 5421010862427522170)
					else -> ONE
				}
			}

		override fun sqr(a: Int128): Int128 = a.sqr()
	}
}
