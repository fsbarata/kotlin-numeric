package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.divRem
import com.github.fsbarata.numeric.iLog2

// IntArray ops file:
// IntArrays used in these operations are one of the 3 following options:
// No elements - ZERO
// One element (signed) which behaves exactly as an Int in two-complement
// Two or more elements, Little Endian order, that carry all the bits that a word of the same size would
// The last element, when existing, is always a High, carrying the sign bit, and the remaining elements are considered unsigned
// The first element carries the increase two-complement


private val ZERO = IntArray(0)
internal fun zero() = ZERO
internal inline fun IntArray.isZero() = isEmpty()

internal const val LONG_LOW_MASK = 0xFFFFFFFFL

internal fun signumLE(array: IntArray, size: Int = array.size): Int =
	when {
		size == 0 -> 0
		array[size - 1] < 0 -> -1
		else -> 1
	}

internal fun compareLE(array1: IntArray, array2: IntArray): Int {
	return compareLE(array1, array1.size, array2, array2.size)
}

private fun compareLE(array1: IntArray, size1: Int, array2: IntArray, size2: Int): Int {
	val compareSize = size1.compareTo(size2)
	if (compareSize != 0) {
		val signum1 = signumLE(array1, size1)
		val signum2 = signumLE(array2, size2)
		val compareSign = signum1.compareTo(signum2)
		if (compareSign != 0) return compareSign
		return if (signum1 < 0) -compareSize else compareSize
	}

	if (size1 == 0) return 0
	val compareHigh = array1[size1 - 1].compareTo(array2[size1 - 1])
	if (compareHigh != 0) return compareHigh

	for (i in (size1 - 2) downTo 0) {
		val compareLong = unsignedAsLong(array1[i]).compareTo(unsignedAsLong(array2[i]))
		if (compareLong != 0) return compareLong
	}
	return 0
}

internal fun IntArray.negated(): IntArray {
	if (isZero()) return ZERO
	return withVarArray(0) { negate(this@negated) }
}

private fun VarArray.negate(readArray: IntArray, size: Int = readArray.size) {
	if (size == 0) return
	operationWithCarry(readArray, size, initialCarry = 0, op = Long::unaryMinus)
}

// Bitwise ops
internal fun IntArray.not(): IntArray {
	if (isZero()) return intArrayOf(-1)
	return IntArray(size) { this[it].inv() }.dropLeadingZeros()
}

internal fun IntArray.and(other: IntArray): IntArray {
	if (other.isZero()) return ZERO
	if (isZero()) return ZERO

	return bitwise(this, other, Int::and)
}

internal fun IntArray.or(other: IntArray): IntArray {
	if (other.isZero()) return this
	if (isZero()) return other

	return bitwise(this, other, Int::or)
}

internal fun IntArray.xor(other: IntArray): IntArray {
	if (other.isZero()) return this
	if (isZero()) return other

	return bitwise(this, other, Int::xor)
}

private fun bitwise(a: IntArray, b: IntArray, op: (Int, Int) -> Int): IntArray {
	val signWord1 = if (a.isNotEmpty() && a[a.lastIndex] < 0) -1 else 0
	val signWord2 = if (b.isNotEmpty() && b[b.lastIndex] < 0) -1 else 0
	return IntArray(maxOf(a.size, b.size)) { index ->
		op(if (index >= a.size) signWord1 else a[index], if (index >= b.size) signWord2 else b[index])
	}.dropLeadingZeros()
}

internal fun shiftLeftLE(array: IntArray, bits: Int): IntArray {
	if (bits == 0) return array
	if (array.isZero()) return ZERO
	return withVarArray(0) { shiftLeftLE(array, array.size, bits) }
}


internal fun shiftRightLE(array: IntArray, bits: Int): IntArray {
	if (bits == 0) return array
	if (array.isZero()) return ZERO
	return withVarArray(0) { shiftRightLE(array, array.size, bits) }
}

private fun VarArray.shiftLeftLE(readArray: IntArray, size: Int, bits: Int) {
	if (bits < 0) {
		shiftRightLE(readArray, size, -bits)
		return
	}
	if (bits == 0) {
		setFrom(readArray)
		return
	}
	if (size == 0) {
		setZero()
		return
	}

	val intShifts = bits.shr(5)
	val newSize = size + intShifts

	val bitsShiftLeft = bits - intShifts.shl(5)
	val bitsShiftRight = 32 - bitsShiftLeft

	if (bitsShiftLeft == 0) {
		reset(newSize)
		copyRange(readArray, intShifts, 0, size)
		fillZeros(0, intShifts)
		return
	}

	val high = readArray[size - 1]
	val newHigh = high.shr(bitsShiftRight)

	val next = high.shl(bitsShiftLeft) or (if (size > 1) readArray[size - 2].ushr(bitsShiftRight) else 0)

	val isNewHighSign = (newHigh == 0 && next >= 0) || (newHigh == -1 && next < 0)

	reset(if (isNewHighSign) newSize else newSize + 1)
	if (!isNewHighSign) this[newSize] = newHigh
	this[newSize - 1] = next
	shiftLeftRangeUnsafe(readArray, maxOf(1 + intShifts, 0), newSize - 2, intShifts, bitsShiftLeft)
	// set the new lower bits
	if (size > 1) this[intShifts] = readArray[0].shl(bitsShiftLeft)
	fillZeros(startIndex = 0, endIndex = intShifts - 1)
}

private fun VarArray.shiftRightLE(readArray: IntArray, size: Int, bits: Int) {
	if (bits <= 0) {
		shiftLeftLE(readArray, size, -bits)
		return
	}
	if (size == 0) {
		setZero()
		return
	}
	val intShifts = bits.shr(5)
	val newSize = size - intShifts - 1
	val bitsShiftRight = bits - intShifts.shl(5)
	val bitsShiftLeft = 32 - bitsShiftRight

	val high = readArray[size - 1]
	if (newSize < 0) {
		setInt(if (high < 0) -1 else 0)
		return
	}

	if (bitsShiftRight == 0) {
		setFromRange(readArray, intShifts, size)
		return
	}

	val newHigh = high.shr(bitsShiftRight)
	if (newSize == 0) {
		setInt(newHigh)
		return
	}

	val next = high.shl(bitsShiftLeft) or (if (size > 1) readArray[size - 2].ushr(bitsShiftRight) else 0)

	val isNewHighSign = (newHigh == 0 && next >= 0) || (newHigh == -1 && next < 0)
	reset(if (isNewHighSign) newSize else newSize + 1)
	shiftLeftRangeUnsafe(readArray, 0, newSize - 2, -intShifts - 1, bitsShiftLeft)
	this[newSize - 1] = next
	if (!isNewHighSign) this[newSize] = newHigh
}

private fun VarArray.shiftLeftRangeUnsafe(
	readArray: IntArray,
	fromIndex: Int,
	toIndex: Int,
	intShifts: Int,
	bits: Int,
) {
	val bitsShiftRight = 32 - bits
	val range: IntProgression = if (intShifts < 0) (fromIndex..toIndex) else (toIndex downTo fromIndex)
	for (newIndex in range) {
		val oldIndex = newIndex - intShifts
		this[newIndex] = readArray[oldIndex].shl(bits) or readArray[oldIndex - 1].ushr(bitsShiftRight)
	}
}


// Math ops
internal fun addLE(addend1: IntArray, addend2: IntArray): IntArray {
	if (addend1.isZero()) return addend2
	if (addend2.isZero()) return addend1
	val isAdding = signumLE(addend1) == signumLE(addend2)
	val resultSizeWithCarry = maxOf(addend1.size, addend2.size) + if (isAdding) 1 else 0
	return withVarArray(resultSizeWithCarry) { addLE(addend1, addend1.size, addend2, addend2.size) }
}

// safe to use with VarArray.array == addend1 or addend2
private fun VarArray.addLE(addend1: IntArray, addend1Size: Int, addend2: IntArray, addend2Size: Int) {
	operationWithCarry(addend1, addend1Size, addend2, addend2Size) { a, b -> a + b }
}

internal fun subtractLE(minuend: IntArray, sub: IntArray): IntArray {
	if (minuend.isZero()) return sub.negated()
	if (sub.isZero()) return minuend
	val isAdding = signumLE(minuend) == -signumLE(sub)
	val resultSizeWithCarry = maxOf(minuend.size, sub.size) + if (isAdding) 1 else 0
	return withVarArray(resultSizeWithCarry) { subtractLE(minuend, minuend.size, sub, sub.size) }
}

// safe to use with VarArray arrays
private fun VarArray.subtractLE(minuend: IntArray, minuendSize: Int, sub: IntArray, subSize: Int) {
	operationWithCarry(minuend, minuendSize, sub, subSize) { a, b -> a - b }
}

internal fun multiplyIntLE(multiplicand: IntArray, multiplier: Int): IntArray {
	if (multiplier == 1) return multiplicand
	if (multiplicand.isZero() || multiplier == 0) return ZERO
	if (multiplicand.size == 1) return longToIntArray(multiplicand[0].toLong() * multiplier.toLong())
	return withVarArray(multiplicand.size + 1) { multiplyIntLE(multiplicand, multiplicand.size, multiplier) }
}

private fun VarArray.multiplyIntLE(multiplicand: IntArray, multiplicandSize: Int, multiplier: Int) {
	if (multiplier == 1) {
		setFrom(multiplicand)
		return
	}
	if (multiplier == 0) {
		setZero()
		return
	}
	operationWithCarry(multiplicand, multiplicandSize) { a -> a * multiplier }
}

private inline fun VarArray.operationWithCarry(
	opSize: Int,
	initialCarry: Int = 0,
	op: (index: Int) -> Long,
) {
	reset(opSize)
	if (opSize == 0) return

	var carry = initialCarry.toLong()
	for (index in 0 until opSize - 1) {
		val opResult = op(index)
		val result = opResult + carry
		val resultLow = result.toInt()
		this[index] = resultLow
		carry = (result shr 32)
	}

	// high is signed
	val opResult = op(opSize - 1)
	val result = opResult + carry
	val resultLow = result.toInt()
	this[opSize - 1] = resultLow
	when {
		resultLow.toLong() != result -> {
			val hw = (result shr 32)
			val high = hw.toInt()
			if (high.toLong() != hw) {
				resize(opSize + 2)
				this[opSize + 1] = (hw shr 32).toInt()
			} else {
				resize(opSize + 1)
			}
			this[opSize] = high
		}

		else -> {
			dropLeadingZeros()
		}
	}
}

// Safe to use with VarArray.array == operand
private inline fun VarArray.operationWithCarry(
	operand: IntArray, operandSize: Int,
	initialCarry: Int = 0,
	op: (Long) -> Long,
) {
	return operationWithCarry(operandSize, initialCarry) { index ->
		op(wordToOp(operand.getWord(index, operandSize), index, operandSize))
	}
}

private inline fun VarArray.operationWithCarry(
	operand1: IntArray, operand1Size: Int = operand1.size,
	operand2: IntArray, operand2Size: Int = operand2.size,
	initialCarry: Int = 0,
	op: (Long, Long) -> Long,
) {
	val opSize = maxOf(operand1Size, operand2Size)
	return operationWithCarry(opSize, initialCarry) { index ->
		val word1 = operand1.getWord(index, operand1Size)
		val word2 = operand2.getWord(index, operand2Size)
		op(wordToOp(word1, index, opSize), wordToOp(word2, index, opSize))
	}
}

internal fun multiplyLE(multiplicand: IntArray, multiplier: IntArray): IntArray {
	if (multiplicand.isZero() || multiplier.isZero()) return ZERO
	if (multiplicand.size == 1) return multiplyIntLE(multiplier, multiplicand[0])
	return withVarArray(multiplicand.size + multiplier.size) {
		multiplyLE(
			multiplicand,
			multiplicand.size,
			multiplier,
			multiplier.size,
		)
	}
}

private fun VarArray.multiplyLE(
	multiplicand: IntArray,
	multiplicandSize: Int,
	multiplier: IntArray,
	multiplierSize: Int,
) {
	if (multiplier.size == 1) {
		multiplyIntLE(multiplicand, multiplicandSize, multiplier[0])
		return
	}

	val resultSize = multiplicandSize + multiplierSize
	resize(resultSize)
	var carry: Long = 0
	var j = 0
	var k = 0
	val multiplierIsNegative = multiplier.getWord(multiplierSize) < 0
	while (if (multiplierIsNegative) k < resultSize else j <= multiplierSize) {
		val product = unsignedAsLong(multiplicand[0]) * unsignedAsLong(multiplier.getWord(j)) + carry
		this[k] = product.toInt()
		carry = product ushr 32
		k++
		j++
	}

	var i = 1
	val multiplicandIsNegative = multiplicand.getWord(multiplicandSize) < 0
	while (if (multiplicandIsNegative) i < resultSize else i < multiplicandSize) {
		carry = 0
		j = 0
		k = i
		while (if (multiplicandIsNegative || multiplierIsNegative) k < resultSize else j <= multiplierSize) {
			val product = unsignedAsLong(this[k]) +
					unsignedAsLong(multiplicand.getWord(i)) * unsignedAsLong(multiplier.getWord(j)) + carry
			this[k] = product.toInt()
			carry = product ushr 32
			k++
			j++
		}
		i++
	}

	dropLeadingZeros()
}

internal fun divRemLE(dividend: IntArray, divisor: IntArray): Pair<IntArray, IntArray> {
	val divisorSign = signumLE(divisor)
	if (divisorSign == 0) throw ArithmeticException("Cannot divide by 0")
	if (divisor.size > dividend.size) return Pair(ZERO, dividend)

	val dividendSign = signumLE(dividend)
	if (dividendSign == 0) return Pair(ZERO, ZERO)

	if (dividend.size == 1) {
		val (d, r) = dividend[0].divRem(divisor[0])
		return Pair(intToIntArray(d), intToIntArray(r))
	}
	if (dividend.size == 2) {
		val dividendLong = intsToLong(dividend[0], dividend[1])
		val divisorLong = if (divisor.size == 1) divisor[0].toLong() else intsToLong(divisor[0], divisor[1])
		val (d, r) = dividendLong.divRem(divisorLong)
		return Pair(longToIntArray(d), longToIntArray(r))
	}

	val rem = VarArray(dividend.copyOf())
	if (dividendSign < 0) rem.negate()
	val shiftedDivisor = VarArray(divisor.copyOf())
	if (divisorSign < 0) shiftedDivisor.negate()

	var remShifts = 0
	val dividendTrailingBits = rem.trailingBits()
	if (dividendTrailingBits > 0) {
		val divisorTrailingBits = shiftedDivisor.trailingBits()
		if (divisorTrailingBits > 0) {
			remShifts = minOf(dividendTrailingBits, divisorTrailingBits)
			rem.shiftRightLE(remShifts)
			shiftedDivisor.shiftRightLE(remShifts)
		}
	}

	var shifts = rem.iLog2() - shiftedDivisor.iLog2()
	if (shifts < 0) return Pair(ZERO, dividend)

	val quo = VarArray(1 + (shifts + 1) / 32)
	var quoIndex = shifts / 32
	var quoBitIndex = shifts % 32

	shiftedDivisor.shiftLeftLE(shifts)
	while (shifts >= 0) {
		val comp = rem.compareTo(shiftedDivisor)
		if (comp >= 0) {
			quo[quoIndex] = quo[quoIndex] or 1.shl(quoBitIndex)
			if (comp == 0) {
				rem.setZero()
				break
			}
			rem -= shiftedDivisor
		}

		quoBitIndex--
		while (quoBitIndex < 0) {
			quoIndex--
			quoBitIndex += 32
		}
		shifts--
		if (shifts >= 0) shiftedDivisor.shiftRightLE(1)
	}

	quo.dropLeadingZeros()
	if (remShifts > 0) rem.shiftLeftLE(remShifts)
	if (divisorSign != dividendSign) quo.negate()
	if (dividendSign < 0) rem.negate()
	return Pair(quo.toArray(), rem.toArray())
}

internal fun IntArray.dropLeadingZeros(size: Int = this.size): IntArray {
	return VarArray(this, dropLeadingZerosSize(size)).toArray()
}

private fun IntArray.dropLeadingZerosSize(size: Int = this.size): Int {
	if (isZero()) return 0

	var lastIndex = size - 1
	while (lastIndex > 0) {
		val high = this[lastIndex]
		if (high != -1 && high != 0) break
		if (lastIndex == 0) return 0

		val negative = high == -1
		val nextLow = this[lastIndex - 1]
		if (!negative && nextLow < 0) break
		if (negative && nextLow >= 0) break

		lastIndex--
	}

	if (lastIndex == 0 && this[0] == 0) return 0
	return lastIndex + 1
}


internal fun IntArray.hasLeadingZeros(): Boolean {
	if (size <= 1) return false
	val high = this[lastIndex]
	return when (high) {
		0 -> this[lastIndex - 1] >= 0
		-1 -> this[lastIndex - 1] < 0
		else -> false
	}
}

internal fun iLog2(array: IntArray, lastIndex: Int = array.lastIndex): Int {
	check(lastIndex >= 0)
	if (array[lastIndex] == 0) return lastIndex * 32 - 1
	return array[lastIndex].iLog2() + lastIndex * 32
}

internal fun trailingZeroBits(array: IntArray, lastIndex: Int = array.lastIndex): Int {
	var trailingBits = 0
	var index = 0
	while (index <= lastIndex) {
		val item = array[index]
		if (item != 0) {
			trailingBits += item.countTrailingZeroBits()
			break
		} else trailingBits += 32
		index++
	}
	return trailingBits
}

internal inline fun unsignedAsLong(word: Int): Long {
	return word.toLong() and LONG_LOW_MASK
}

internal fun longToIntArray(long: Long): IntArray {
	if (long == 0L) return ZERO
	val low = long.toInt()
	return if (low.toLong() == long) intToIntArray(low)
	else intArrayOf(low, long.shr(32).toInt())
}

internal inline fun intToIntArray(int: Int): IntArray {
	return if (int == 0) ZERO else intArrayOf(int)
}

internal fun VarArray.setInt(int: Int) {
	if (int == 0) setZero()
	else {
		reset(1)
		this[0] = int
	}
}

private fun IntArray.getWord(index: Int, size: Int = this.size): Int {
	return when {
		index >= size -> if (size > 0 && this[size - 1] < 0) -1 else 0
		index >= 0 -> this[index]
		else -> throw IndexOutOfBoundsException("$index < 0")
	}
}

private fun wordToOp(word: Int, index: Int, opSize: Int): Long {
	return if (index >= opSize - 1) word.toLong() else unsignedAsLong(word)
}

internal inline fun intsToLong(low: Int, high: Int): Long {
	return high.toLong().shl(32) or unsignedAsLong(low)
}

internal class VarArray(
	private var array: IntArray,
	size: Int = array.size,
) {
	constructor(size: Int): this(
		if (size == 0) ZERO
		else IntArray(size),
		size = size,
	)

	var size: Int = size
		private set

	val lastIndex get() = size - 1

	fun toArray(): IntArray {
		return when (size) {
			array.size -> array
			0 -> ZERO
			else -> array.copyOf(size)
		}
	}

	fun getOrNull(index: Int): Int? =
		if (index < 0 || index >= size) null
		else array[index]

	operator fun get(index: Int): Int =
		getOrNull(index) ?: throw IndexOutOfBoundsException("$index not in bounds [0, $size[")

	operator fun set(index: Int, value: Int) {
		if (index >= size) throw IndexOutOfBoundsException("$index not in bounds [0, $size[")
		array[index] = value
	}

	fun dropLeadingZeros() {
		size = array.dropLeadingZerosSize(size)
	}

	fun fillZeros(startIndex: Int, endIndex: Int) {
		if (endIndex <= startIndex) return
		require(startIndex >= 0 && endIndex < array.size)
		array.fill(0, startIndex, endIndex)
	}

	fun reset(newSize: Int) {
		if (newSize > array.size) {
			array = IntArray(newSize)
		}
		size = newSize
	}

	fun resize(newSize: Int) {
		if (newSize > array.size) {
			array = array.copyOf(newSize)
		}
		size = newSize
	}

	fun setFrom(source: IntArray, size: Int = source.size) {
		this.size = size

		if (source === array) return
		else if (array.size < size)
			array = source.copyOf()
		else if (size > 0) {
			source.copyInto(array, endIndex = size)
		}
	}

	fun setFromRange(source: IntArray, startIndex: Int, endIndex: Int) {
		if (startIndex == 0) {
			setFrom(source, endIndex)
			return
		}

		this.size = endIndex - startIndex
		if (array.size < size)
			array = source.copyOfRange(startIndex, endIndex)
		else if (size > 0) {
			source.copyInto(array, startIndex = startIndex, endIndex = size)
		}
	}

	fun copyRange(source: IntArray, offset: Int, startIndex: Int, endIndex: Int) {
		if (array.size < offset + (endIndex - startIndex)) {
			array = array.copyOf(offset + (endIndex - startIndex))
		}
		source.copyInto(array, offset, startIndex, endIndex)
	}

	fun setZero() {
		size = 0
	}

	fun iLog2() = iLog2(array, lastIndex)
	fun trailingBits() = trailingZeroBits(array, lastIndex)

	fun compareTo(other: VarArray): Int = compareLE(array, size, other.array, other.size)

	fun negate() {
		negate(array, size)
	}

	fun shiftLeftLE(bits: Int) {
		shiftLeftLE(array, size, bits)
	}

	fun shiftRightLE(bits: Int) {
		shiftRightLE(array, size, bits)
	}

	operator fun plusAssign(other: IntArray) {
		addLE(array, size, other, other.size)
	}

	operator fun plusAssign(other: VarArray) {
		addLE(array, size, other.array, other.size)
	}

	operator fun minusAssign(other: VarArray) {
		subtractLE(array, size, other.array, other.size)
	}

	operator fun timesAssign(multiplier: Int) {
		multiplyIntLE(array, size, multiplier)
	}
}

internal inline fun withVarArray(capacity: Int, block: VarArray.() -> Unit): IntArray {
	return VarArray(capacity).apply(block).toArray()
}