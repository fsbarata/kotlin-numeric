package com.github.fsbarata.numeric

interface Bitwise<T> {
	fun not(): T
	infix fun and(other: T): T
	infix fun or(other: T): T
	infix fun xor(other: T): T
	infix fun shr(bitCount: Int): T
	infix fun shl(bitCount: Int): T
}
