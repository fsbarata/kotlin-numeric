package com.github.fsbarata.numeric

interface Bitwise<T> {
	fun not(): T

	infix fun and(other: T): T
	infix fun or(other: T): T
	infix fun xor(other: T): T

	infix fun shl(bitCount: Int): T
	infix fun shr(bitCount: Int): T

	fun highestSetBitIndex(): Int  // equivalent to iLog2 when number is positive, otherwise returns size in bits
	fun lowestSetBitIndex(): Int   // equivalent to (trailingZeros - 1)

	interface Scope<T> {
		fun not(t: T): T

		infix fun T.and(other: T): T
		infix fun T.or(other: T): T
		infix fun T.xor(other: T): T

		infix fun T.shl(bitCount: Int): T
		infix fun T.shr(bitCount: Int): T

		fun highestSetBitIndex(t: T): Int
		fun lowestSetBitIndex(t: T): Int
	}

	companion object {
		@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
		fun <T: Bitwise<T>> delegateScope() = object: Scope<T> {
			override fun not(t: T): T = t.not()

			override infix fun T.and(other: T): T = and(other)
			override infix fun T.or(other: T): T = or(other)
			override infix fun T.xor(other: T): T = xor(other)

			override infix fun T.shl(bitCount: Int): T = shl(bitCount)
			override infix fun T.shr(bitCount: Int): T = shr(bitCount)

			override fun highestSetBitIndex(t: T): Int = t.highestSetBitIndex()
			override fun lowestSetBitIndex(t: T): Int = t.lowestSetBitIndex()
		}
	}
}
