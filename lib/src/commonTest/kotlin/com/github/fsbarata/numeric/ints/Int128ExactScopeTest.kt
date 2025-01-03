package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.assertCommutative
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Int128ExactScopeTest {
	@Test
	fun addSubtract() {
		assertAddition(Int128.fromInt(1299), Int128.fromInt(-14))
		assertAddition(Int128(0, 1248), Int128(1, 5889))
		assertAddition(Int128(0, 1248), Int128(1, -5889))
		assertAddition(Int128(5, 9123881282139123891), Int128(1, 1))
		assertAddition(Int128(5, -9123881282139123891), Int128.MAX_VALUE)

		assertAdditionNull(Int128(0, 9123881282139123891), Int128(1, 9123881282139123891))

		for (high1 in longArrayOf(Long.MAX_VALUE.shr(1), Long.MAX_VALUE.shr(1) + 1, 50000, Long.MAX_VALUE)) {
			val high2 = Long.MAX_VALUE - high1
			assertAddition(Int128(0, high1), Int128(0, high2))
			assertAddition(Int128(-1, high1), Int128(0, high2))
			assertAddition(Int128(0, high1), Int128(-1, high2))

			assertAdditionNull(Int128(-1, high1), Int128(1, high2))
			assertAddition(Int128(-1, high1), Int128(1, high2 - 1))
			assertAddition(Int128(-1, high1 - 1), Int128(1, high2))

			assertAdditionNull(Int128(1, high1), Int128(-1, high2))
			assertAddition(Int128(1, high1), Int128(-1, high2 - 1))
			assertAddition(Int128(1, high1 - 1), Int128(-1, high2))

			assertAdditionNull(Int128(-1, high1), Int128(5, high2))
			assertAddition(Int128(-5, high1), Int128(2, high2))
			assertAddition(Int128(-1, high1 - 1), Int128(1, high2))
			assertAddition(Int128(-1, high1), Int128(1, high2 - 1))

			assertAdditionNull(Int128(Long.MIN_VALUE, high1), Int128(Long.MIN_VALUE, high2))
			assertAddition(Int128(Long.MIN_VALUE, high1), Int128(Long.MAX_VALUE, high2))
			assertAddition(Int128(Long.MAX_VALUE, high1), Int128(Long.MIN_VALUE, high2))

		}

		for (high1 in longArrayOf(Long.MIN_VALUE.shr(1), Long.MIN_VALUE.shr(1) - 1, -50000, Long.MIN_VALUE)) {
			val high2 = Long.MIN_VALUE - high1 - 1
			assertAddition(Int128(-1, high1), Int128(-1, high2))

			assertAdditionNull(Int128(0, high1), Int128(-1, high2))
			assertAddition(Int128(0, high1 + 1), Int128(-1, high2))
			assertAddition(Int128(0, high1), Int128(-1, high2 + 1))

			assertAdditionNull(Int128(-1, high1), Int128(0, high2))
			assertAddition(Int128(-1, high1 + 1), Int128(0, high2))
			assertAddition(Int128(-1, high1), Int128(0, high2 + 1))

			assertAdditionNull(Int128(Long.MAX_VALUE, high1), Int128(Long.MIN_VALUE, high2))
			assertAdditionNull(Int128(Long.MIN_VALUE, high1), Int128(Long.MAX_VALUE, high2))
			assertAddition(Int128(Long.MAX_VALUE, high1), Int128(-Long.MAX_VALUE, high2))
			assertAddition(Int128(-Long.MAX_VALUE, high1), Int128(Long.MAX_VALUE, high2))

			assertAdditionNull(Int128(-5, high1), Int128(2, high2))
			assertAddition(Int128(-2, high1), Int128(5, high2))
			assertAddition(Int128(-1, high1 + 1), Int128(1, high2))
			assertAddition(Int128(-1, high1), Int128(1, high2 + 1))
		}
	}

	private fun assertAddition(a: Int128, b: Int128) {
		assertEquals(a.toBigInt() + b.toBigInt(), (a + b).toBigInt())
		assertCommutative(a + b, a, b, Int128ExactScope::addOrNull)

		val minusB = Int128ExactScope.negateOrNull(b) ?: return
		assertEquals(a.toBigInt() - minusB.toBigInt(), (a - minusB).toBigInt())
		assertEquals(a - minusB, Int128ExactScope.subtractOrNull(a, minusB))
	}

	private fun assertAdditionNull(a: Int128, b: Int128) {
		assertNotEquals(a.toBigInt() + b.toBigInt(), (a + b).toBigInt())
		assertCommutative(null, a, b, Int128ExactScope::addOrNull)

		val minusB = Int128ExactScope.negateOrNull(b) ?: return
		assertNotEquals(a.toBigInt() - minusB.toBigInt(), (a - minusB).toBigInt())
		assertEquals(null, Int128ExactScope.subtractOrNull(a, minusB))
	}

	@Test
	fun multiplyOrNull() {
		assertMultiplication(Int128.fromLong(Long.MIN_VALUE), Int128.fromLong(Long.MAX_VALUE))
		assertMultiplication(Int128.fromLong(Long.MIN_VALUE), Int128.fromLong(Long.MIN_VALUE))
		assertMultiplication(Int128.fromLong(4569515072723572), Int128(4569515072723572, -2))

		assertMultiplicationNull(Int128(0, 1), Int128(0, 1))
		assertMultiplicationNull(Int128(0, -1), Int128(0, -1))
		assertMultiplicationNull(Int128(0, 1), Int128(0, -1))
		assertMultiplicationNull(Int128(0, -1), Int128(0, 1))

		assertMultiplication(Int128(0, 1), Int128(Long.MAX_VALUE, 0))        // 2^64 * (2^63 - 1)
		assertMultiplication(Int128(1, 1), Int128(Long.MAX_VALUE, 0))        // (2^64 + 1) * (2^63 - 1)
		assertMultiplicationNull(Int128(0, 1), Int128(Long.MIN_VALUE, 0))    // 2^64 * 2^63
		assertMultiplicationNull(Int128(-1, 0), Int128(-Long.MAX_VALUE, 0))  // (2^64 - 1) * (2^63 + 1)

		assertMultiplication(Int128(0, -1), Int128(-Long.MAX_VALUE, -1))     // -2^64 * -(2^63 - 1)
		assertMultiplication(Int128(-1, -2), Int128(-Long.MAX_VALUE, -1))    // -(2^64 + 1) * -(2^63 - 1)
		assertMultiplicationNull(Int128(0, -1), Int128(Long.MIN_VALUE, -1))  // -2^64 * -2^63
		assertMultiplicationNull(Int128(1, -1), Int128(Long.MAX_VALUE, -1))  // -(2^64 - 1) * -(2^63 + 1)

		assertMultiplication(Int128(0, 1), Int128(Long.MIN_VALUE, -1))       // 2^64 * -2^63
		assertMultiplicationNull(Int128(1, 1), Int128(Long.MIN_VALUE, -1))   // (2^64 + 1) * -2^63
		assertMultiplicationNull(Int128(0, 1), Int128(Long.MAX_VALUE, -1))   // 2^64 * -(2^63 + 1)

		assertMultiplication(Int128(0, -1), Int128(Long.MIN_VALUE, 0))       // -2^64 * 2^63
		assertMultiplicationNull(Int128(-1, -2), Int128(Long.MIN_VALUE, 0))  // -(2^64 + 1) * 2^63
		assertMultiplicationNull(Int128(0, -1), Int128(-Long.MAX_VALUE, 0))  // -2^64 * (2^63 + 1)

		assertMultiplication(Int128(-8446744073709551616, 0), Int128(-8446744073709551625, 0))

		assertMultiplication(Int128(-5402926248376769404, 0), Int128(-5402926248376769404, 0))
		assertMultiplication(Int128(5402926248376769404, -1), Int128(-5402926248376769404, 0))
		assertMultiplication(Int128(5402926248376769404, -1), Int128(5402926248376769404, -1))
		assertMultiplicationNull(Int128(-5402926248376769404, 0), Int128(-5402926248376769403, 0))
		assertMultiplicationNull(Int128(5402926248376769403, -1), Int128(-5402926248376769404, 0))
		assertMultiplicationNull(Int128(5402926248376769404, -1), Int128(-5402926248376769403, 0))
		assertMultiplicationNull(Int128(5402926248376769403, -1), Int128(5402926248376769404, -1))

		assertMultiplication(Int128.fromLong(45695150727235), Int128(-99999999999999, 5))

		assertMultiplication(Int128.fromLong(80000000000), Int128(8498132715301476896, 115292150))
		assertMultiplication(Int128.fromLong(-80000000000), Int128(8498132715301476896, 115292150))
		assertMultiplication(Int128.fromLong(80000000000), Int128(-8498132715301476896, -115292151))
		assertMultiplication(Int128.fromLong(-80000000000), Int128(-8498132715301476896, -115292151))

		assertMultiplicationNull(Int128.fromLong(80000000000), Int128(8498132715301476897, 115292150))
		assertMultiplicationNull(Int128.fromLong(80000000001), Int128(8498132715301476896, 115292150))
		assertMultiplicationNull(Int128.fromLong(-80000000000), Int128(8498132715301476897, 115292150))
		assertMultiplicationNull(Int128.fromLong(-80000000001), Int128(8498132715301476896, 115292150))
		assertMultiplicationNull(Int128.fromLong(80000000000), Int128(-8498132715301476897, -115292151))
		assertMultiplicationNull(Int128.fromLong(80000000001), Int128(-8498132715301476896, -115292151))
		assertMultiplicationNull(Int128.fromLong(-80000000000), Int128(-8498132715301476897, -115292151))
		assertMultiplicationNull(Int128.fromLong(-80000000001), Int128(-8498132715301476896, -115292151))
	}

	private fun assertMultiplication(a: Int128, b: Int128) {
		assertEquals(a.toBigInt() * b.toBigInt(), (a * b).toBigInt(), "$a * $b")
		assertCommutative(a * b, a, b, Int128ExactScope::multiplyOrNull)
	}

	private fun assertMultiplicationNull(a: Int128, b: Int128) {
		assertNotEquals(a.toBigInt() * b.toBigInt(), (a * b).toBigInt(), "$a * $b")
		assertCommutative(null, a, b, Int128ExactScope::multiplyOrNull)
	}

	@Test
	fun sqrOrNull() {
		assertSqr(Int128.fromLong(4569515072723572))
		assertSqr(Int128.fromLong(-5402926248376769403))
		assertSqr(Int128.fromLong(Long.MIN_VALUE))
		assertSqr(Int128.fromLong(Long.MAX_VALUE))
		assertSqr(Int128(5402926248376769404, -1))
		assertSqrNull(Int128(5402926248376769403, -1))
		assertSqr(Int128(-5402926248376769404, 0))
		assertSqrNull(Int128(-5402926248376769403, 0))
		assertSqrNull(Int128(0, 1))
		assertSqrNull(Int128(0, -1))
	}

	private fun assertSqr(a: Int128) {
		assertEquals(a.toBigInt().sqr(), (a.sqr()).toBigInt())
		assertEquals(a.sqr(), Int128ExactScope.sqrOrNull(a))
	}

	private fun assertSqrNull(a: Int128) {
		assertNotEquals(a.toBigInt().sqr(), (a.sqr()).toBigInt())
		assertEquals(null, Int128ExactScope.sqrOrNull(a))
	}
}