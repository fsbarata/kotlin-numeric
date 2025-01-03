package com.github.fsbarata.numeric.ints

import com.github.fsbarata.io.Serializable
import com.github.fsbarata.numeric.*
import com.github.fsbarata.numeric.ratio.Rational
import kotlin.math.absoluteValue

actual class BigInt: Integral<BigInt>, Bitwise<BigInt>, Serializable {
	private val intArray: IntArray

	private constructor(intArray: IntArray) {
		require(!intArray.hasLeadingZeros()) { "Integer cannot have leading zeros ${intArray.toList()}" }
		this.intArray = intArray
	}

	actual constructor(bytesBE: ByteArray, offset: Int, length: Int) {
		if (length == 0) {
			intArray = IntArray(0)
			return
		}

		val lastByte = bytesBE[offset]
		val signByte = if (lastByte < 0) 0xFF else 0x00
		val intCount = (length + 3).ushr(2)
		val startIndex = offset + length - intCount.shl(2)
		val intArray = IntArray(intCount) { intIndex ->
			val startByteIndex = (intCount - 1 - intIndex).shl(2) + startIndex
			(0 until Int.SIZE_BYTES).fold(0) { acc, intByteIndex ->
				val byteIndex = startByteIndex + 3 - intByteIndex
				val shiftBits = intByteIndex.shl(3)
				val byte =
					if (byteIndex < 0) signByte
					else bytesBE[byteIndex].toInt() and 0xFF
				acc.or(byte.shl(shiftBits))
			}
		}

		this.intArray = intArray.dropLeadingZeros()
	}

	actual fun toByteArrayBE(): ByteArray {
		if (intArray.isZero()) return ByteArray(0)

		val high = intArray[intArray.lastIndex]
		val highByteCount = when (high) {
			0, -1, 1 -> 1
			Int.MIN_VALUE -> 4
			else -> 1 + (high.absoluteValue - 1).iLog2().shr(3)
		}

		val bytesSize = intArray.lastIndex.shl(2) + highByteCount
		val bytes = ByteArray(bytesSize) { indexBE ->
			val indexLE = bytesSize - indexBE - 1
			val intArrayIndex = indexLE.ushr(2)
			val shiftBytes = indexLE - intArrayIndex.shl(2)
			val shiftBits = shiftBytes.shl(3)
			(intArray[intArrayIndex].shr(shiftBits)).toByte()
		}

		return bytes
	}

	fun toByteArrayLE(): ByteArray {
		val high = intArray[intArray.lastIndex]
		val highByteCount = when (high) {
			0, -1 -> 1
			Int.MIN_VALUE -> 4
			else -> 1 + (high.absoluteValue - 1).iLog2().shr(3)
		}

		val bytesSize = intArray.lastIndex.shl(2) + highByteCount
		return ByteArray(bytesSize) { indexLE ->
			val intArrayIndex = indexLE.ushr(2)
			val shiftBytes = indexLE - intArrayIndex.shl(2)
			val shiftBits = shiftBytes.shl(3)
			(intArray[intArrayIndex].shr(shiftBits)).toByte()
		}
	}

	fun toIntArrayLE(): IntArray = intArray.copyOf()

	actual override fun not(): BigInt = BigInt(intArray.not())
	actual override infix fun and(other: BigInt): BigInt = BigInt(intArray.and(other.intArray))
	actual override infix fun or(other: BigInt): BigInt = BigInt(intArray.or(other.intArray))
	actual override infix fun xor(other: BigInt): BigInt = BigInt(intArray.xor(other.intArray))
	actual override fun shl(bitCount: Int): BigInt = BigInt(shiftLeftLE(intArray, bitCount))
	actual override fun shr(bitCount: Int): BigInt = BigInt(shiftRightLE(intArray, bitCount))

	fun iLog2(): Int = iLog2(intArray)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is BigInt) return false
		return compareTo(other) == 0
	}

	override fun hashCode(): Int {
		return intArray.reduceOrNull(Int::xor)?.hashCode() ?: 0
	}

	override fun toString(): String {
		return toString(10)
	}

	actual fun toString(radix: Int): String {
		require(radix in 1..36)
		if (intArray.isEmpty()) return "0"

		val (digits, pow) = maxDigitsPowFor(radix)
		var d = this@BigInt
		return buildString {
			while (true) {
				val dr = d.divRem(pow)
				d = dr.first
				if (d.isZero()) {
					insert(0, dr.second.toString(radix))
					break
				}
				val r = dr.second.absoluteValue
				insert(0, r.toString(radix))
				repeat(digits - 1 - if (r > 0) r.iLog10() else 0) { insert(0, '0') }
			}
		}
	}

	override fun isZero() = intArray.isZero()

	actual override fun signum(): Int = signumLE(intArray)
	actual override fun unaryMinus(): BigInt = BigInt(intArray.negated())

	actual override fun compareTo(other: BigInt): Int {
		return compareLE(intArray, other.intArray)
	}

	actual override fun toInt(): Int {
		return intArray.firstOrNull() ?: 0
	}

	actual override fun toIntOrNull(): Int? {
		return if (intArray.size > 1) null else toInt()
	}

	actual override fun toLong(): Long {
		return when (intArray.size) {
			0 -> 0L
			1 -> intArray[0].toLong()
			else -> intsToLong(low = intArray[0], high = intArray[1])
		}
	}

	actual override fun toLongOrNull(): Long? {
		return if (intArray.size > 2) null else toLong()
	}

	actual override fun toInt128(): Int128 {
		return when (intArray.size) {
			0, 1, 2 -> Int128.fromLong(toLong())
			3 -> Int128(low = toLong(), high = intArray[2].toLong())
			else -> Int128(low = toLong(), high = intsToLong(intArray[3], intArray[4]))
		}
	}

	actual override fun toInt128OrNull(): Int128? {
		return if (intArray.size <= 4) toInt128() else null
	}

	actual override fun toBigInt(): BigInt = this

	actual override fun plus(addend: BigInt): BigInt {
		if (isZero()) return addend
		if (addend.isZero()) return this
		return BigInt(addLE(intArray, addend.intArray))
	}

	actual override fun minus(sub: BigInt): BigInt {
		if (isZero()) return sub.unaryMinus()
		if (sub.isZero()) return this
		return BigInt(subtractLE(intArray, sub.intArray))
	}

	actual override fun times(multiplier: BigInt): BigInt {
		if (isZero() || multiplier.isZero()) return ZERO
		return BigInt(multiplyLE(intArray, multiplier.intArray))
	}

	actual override fun divRem(divisor: BigInt): Pair<BigInt, BigInt> {
		val dr = divRemLE(intArray, divisor.intArray)
		return Pair(BigInt(dr.first), BigInt(dr.second))
	}

	actual override fun div(divisor: BigInt): BigInt {
		return divRem(divisor).first
	}

	actual override fun rem(divisor: BigInt): BigInt {
		return divRem(divisor).second
	}

	actual override fun toRational(): Rational {
		return Rational.fromBigInt(this)
	}

	actual override fun toDouble(): Double {
		if (intArray.size <= 2) return toLong().toDouble()
		if (intArray.size > 32) throw ArithmeticException("Double overflow")
		return scalb(intArray[intArray.lastIndex].toDouble(), intArray.lastIndex * 32) +
				scalb(unsignedAsLong(intArray[intArray.lastIndex - 1]).toDouble(), (intArray.lastIndex - 1) * 32) +
				scalb(unsignedAsLong(intArray[intArray.lastIndex - 2]).toDouble(), (intArray.lastIndex - 2) * 32)
	}

	actual fun toDoubleOrNull(): Double? = if (iLog2() >= 53) null else toDouble()

	actual override operator fun plus(addend: Int): BigInt {
		if (addend == 0) return this
		return BigInt(addLE(intArray, intToIntArray(addend)))
	}

	actual operator fun plus(addend: Long): BigInt {
		if (addend == 0L) return this
		return BigInt(addLE(intArray, longToIntArray(addend)))
	}

	actual override operator fun minus(sub: Int): BigInt = plus(-sub)

	actual operator fun minus(sub: Long): BigInt = plus(-sub)

	actual override operator fun times(multiplier: Int): BigInt {
		if (isZero() || multiplier == 0) return ZERO
		return BigInt(multiplyIntLE(intArray, multiplier))
	}

	actual operator fun times(multiplier: Long): BigInt {
		if (isZero() || multiplier == 0L) return ZERO
		return BigInt(multiplyLE(intArray, longToIntArray(multiplier)))
	}

	actual override operator fun div(divisor: Int): BigInt = divRem(divisor).first
	actual override operator fun rem(divisor: Int): Int = divRem(divisor).second

	actual override fun divRem(divisor: Int): Pair<BigInt, Int> {
		val (result, rem) = divRemLE(intArray, intArrayOf(divisor))
		if (rem.size > 1) throw OverflowError("Remainder ${rem.joinToString()} converting to Int")
		val remInt = rem.firstOrNull() ?: 0
		return Pair(BigInt(result), remInt)
	}


	actual operator fun div(divisor: Long): BigInt = divRem(divisor).first
	actual operator fun rem(divisor: Long): Long = divRem(divisor).second


	actual fun divRem(divisor: Long): Pair<BigInt, Long> {
		val (result, rem) = divRemLE(intArray, longToIntArray(divisor))
		val remLong = when (rem.size) {
			0 -> 0L
			1 -> rem[0].toLong()
			2 -> intsToLong(rem[0], rem[1])
			else -> throw OverflowError("Remainder ${rem.joinToString()} converting to Long")
		}
		return Pair(BigInt(result), remLong)
	}

	actual fun pow(exponent: Int): BigInt {
		return when {
			exponent == 0 -> ONE
			exponent < 0 -> when (toIntOrNull()) {
				1 -> ONE
				-1 -> if (exponent.isOdd()) this else ONE
				else -> throw ArithmeticException("Negative exponent")
			}

			else -> when (val int = toIntOrNull()) {
				0, 1 -> this
				-1 -> if (exponent.isOdd()) this else ONE
				10 -> BigInt(tenPower(exponent))
				-10 -> BigInt(tenPower(exponent, exponent.isOdd()))
				2 -> BigInt(shiftLeftLE(intArrayOf(1), exponent))
				-2 -> BigInt(shiftLeftLE(intArrayOf(if (exponent.isOdd()) -1 else 1), exponent))
				else -> {
					if (int != null) {
						val index = TEN_POWERS_INT.indexOf(int)
						if (index != -1) return BigInt(tenPower(exponent * index))
						val indexNegated = TEN_POWERS_INT.indexOf(-int)
						if (indexNegated != -1) return BigInt(tenPower(exponent * indexNegated, exponent.isOdd()))
					}
					BigInt.pow(this, exponent)
				}
			}
		}
	}

	private fun Int.isOdd() = and(1) != 0


	actual companion object:
		Integral.Scope<BigInt>, ExactIntegralScope<BigInt>,
		Integral.OpScope<BigInt> by Integral.delegateOpScope() {
		actual override val ZERO: BigInt = BigInt(zero())
		actual override val ONE: BigInt = fromInt(1)
		actual val TWO: BigInt = fromInt(2)
		actual val TEN: BigInt = fromInt(10)

		actual override fun compare(a: BigInt, b: BigInt): Int = a.compareTo(b)

		actual fun fromString(string: String): BigInt = fromString(string, 10)
		actual fun fromString(string: String, radix: Int): BigInt {
			return parseOrNull(string, radix)
				?: throw IllegalArgumentException("Cannot parse as BigInt (radix $radix): $string")
		}

		actual fun parseOrNull(string: String): BigInt? = parseOrNull(string, 10)
		actual fun parseOrNull(string: String, radix: Int): BigInt? {
			if (string.isEmpty()) return null
			val (digits, pow) = maxDigitsPowFor(radix)
			val digitWords = 1 + (string.length / digits)

			val firstWordEndIndex = if (digitWords > 2) string.length - (digitWords - 2) * digits else string.length
			val firstWord = string.substring(0, firstWordEndIndex).toLongOrNull(radix) ?: return null
			if (digitWords <= 2) return fromLong(firstWord)

			val range = firstWordEndIndex until string.length step digits
			val strings = range.map { string.substring(it, (it + digits).coerceAtMost(string.length)) }
			if (strings.isEmpty()) return fromLong(firstWord)

			val isNegative = firstWord < 0
			val r = withVarArray(strings.size + 2) {
				setFrom(longToIntArray(firstWord))
				strings.forEach { s ->
					val b = s.toIntOrNull(radix) ?: return null
					if (b < 0) return null
					this *= pow
					this += intArrayOf(if (isNegative) -b else b)
				}
			}

			return BigInt(r)
		}

		actual override fun toInt(a: BigInt): Int = a.toInt()
		actual override fun toIntOrNull(a: BigInt): Int? = a.toIntOrNull()
		actual override fun toLong(a: BigInt): Long = a.toLong()
		actual override fun toLongOrNull(a: BigInt): Long? = a.toLongOrNull()
		actual override fun toInt128(a: BigInt): Int128 = a.toInt128()
		actual override fun toInt128OrNull(a: BigInt): Int128? = a.toInt128OrNull()
		actual override fun toBigInt(a: BigInt): BigInt = a

		actual override fun fromInt(int: Int): BigInt = BigInt(intToIntArray(int))
		actual override fun fromLong(long: Long): BigInt = BigInt(longToIntArray(long))
		actual override fun fromLongOrNull(long: Long): BigInt? = fromLong(long)
		actual override fun fromInt128(int128: Int128): BigInt = int128.toBigInt()
		actual override fun fromInt128OrNull(int128: Int128): BigInt? = int128.toBigInt()
		actual override fun fromBigInt(bigInt: BigInt): BigInt = bigInt
		actual override fun fromBigIntOrNull(bigInt: BigInt): BigInt? = bigInt

		actual override fun toDouble(a: BigInt): Double = a.toDouble()

		actual override fun divRem(a: BigInt, b: BigInt): Pair<BigInt, BigInt> = a.divRem(b)
		actual override fun mod(a: BigInt, b: BigInt): BigInt = a % b

		actual fun fromIntArrayLE(array: IntArray): BigInt = BigInt(array.dropLeadingZeros())

		private fun maxDigitsPowFor(radix: Int): Pair<Int, Int> {
			val digits = when (radix) {
				2 -> return Pair(30, 1073741824)
				3 -> 15
				4 -> return Pair(15, 1073741824)
				in 5..7 -> 10
				8 -> return Pair(10, 1073741824)
				9 -> 9
				10 -> return Pair(9, 1_000_000_000)
				in 11..15 -> 7
				16 -> return Pair(7, 268435456)
				in 17..32 -> 6
				else -> 5
			}
			return Pair(digits, IntNumScope.pow(radix, digits))
		}

		private fun tenPower(exponent: Int, negative: Boolean = false): IntArray {
			return if (exponent < TEN_POWERS_INT.lastIndex)
				intArrayOf(if (negative) -TEN_POWERS_INT[exponent] else TEN_POWERS_INT[exponent])
			else withVarArray(2) {
				setInt(TEN_POWERS_INT[TEN_POWERS_INT.lastIndex])
				var n = exponent - TEN_POWERS_INT.lastIndex
				while (n > 0) {
					val pow10Int = TEN_POWERS_INT[minOf(n, TEN_POWERS_INT.lastIndex)]
					this *= pow10Int
					n -= TEN_POWERS_INT.lastIndex
				}
				if (negative) negate()
			}
		}

		actual override fun addOrNull(a: BigInt, b: BigInt): BigInt? = add(a, b)
		actual override fun subtractOrNull(a: BigInt, b: BigInt): BigInt? = subtract(a, b)
		actual override fun multiplyOrNull(a: BigInt, b: BigInt): BigInt? = multiply(a, b)
		actual override fun negateOrNull(a: BigInt): BigInt? = negate(a)
	}
}