package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import kotlin.test.Test
import kotlin.test.assertEquals


interface NumTestBase<T: Num<T>>: NumScopeTestBase<T> {
	override val scope: Num.Scope<T>

	@Test
	fun toDouble32() {
		with(scope) {
			assertEquals(1.0, ONE.toDouble(), 1e-2)
			assertEquals(0.0, ZERO.toDouble(), 1e-2)
			assertEquals(-1.0, (-ONE).toDouble(), 1e-2)
			assertEquals(158.0, fromInt(158).toDouble(), 1e-8)
			assertEquals(-5000000.0, fromInt(-5000000).toDouble(), 1e-2)
			assertEquals(5000000.0, fromInt(5000000).toDouble(), 1e-2)
			assertEquals(2147483647.0, fromInt(Int.MAX_VALUE).toDouble(), 1e-8)
			assertEquals(-2147483648.0, fromInt(Int.MIN_VALUE).toDouble(), 1e-4)
		}
	}

	@Test
	fun unaryMinus32() {
		with(scope) {
			assertEquals(ZERO, -ZERO)
			assertEquals(fromInt(-1), -ONE)
			assertEquals(fromInt(-3), -fromInt(3))
			assertEquals(fromInt(3), -fromInt(-3))
			assertEquals(fromInt(15881), -fromInt(-15881))
			assertEquals(fromInt(-Int.MAX_VALUE), (-fromInt(Int.MAX_VALUE)))
		}
	}

	@Test
	fun plus32() {
		with(scope) {
			assertCommutative(ZERO, ZERO, ZERO, Num<T>::plus)
			assertCommutative(ONE, ZERO, ONE, Num<T>::plus)
			assertCommutative(fromInt(14), ZERO, fromInt(14), Num<T>::plus)
			assertCommutative(fromInt(-14), ZERO, fromInt(-14), Num<T>::plus)

			assertCommutative(fromInt(2), ONE, ONE, Num<T>::plus)
			assertPlusInt(ZERO, ONE, -1)

			assertPlusInt(fromInt(1313), fromInt(1299), 14)
			assertPlusInt(fromInt(-1285), fromInt(-1299), 14)
			assertPlusInt(fromInt(1285), fromInt(1299), -14)
			assertPlusInt(fromInt(-1313), fromInt(-1299), -14)
		}
	}

	private fun assertPlusInt(expected: T, a: T, b: Int) {
		assertEquals(expected, a + b)
		assertCommutative(expected, a, scope.fromInt(b), Num<T>::plus)
	}

	@Test
	fun minus32() {
		with(scope) {
			assertEquals(ZERO, ZERO - ZERO)
			assertEquals(ZERO, ONE - ONE)

			assertEquals(fromInt(-14), ZERO - fromInt(14))
			assertEquals(fromInt(14), fromInt(14) - ZERO)
			assertEquals(fromInt(-14), fromInt(-14) - ZERO)
			assertEquals(fromInt(14), ZERO - fromInt(-14))

			assertEquals(fromInt(1285), fromInt(1299) - fromInt(14))
			assertEquals(fromInt(-1313), fromInt(-1299) - fromInt(14))
			assertEquals(fromInt(1313), fromInt(1299) - fromInt(-14))
			assertEquals(fromInt(-1285), fromInt(-1299) - fromInt(-14))
		}
	}

	@Test
	fun times32() {
		with(scope) {
			assertCommutative(ZERO, ZERO, ZERO, Num<T>::times)
			assertEquals(ZERO, ZERO * 0)

			assertTimesInt(ZERO, fromLong(-4587658253900688810), 0)
			assertEquals(ZERO, ZERO * -50)
			assertCommutative(ZERO, fromLong(4587658253900688810), ZERO, Num<T>::times)

			assertTimesInt(fromInt(18186), fromInt(1299), 14)
			assertTimesInt(fromInt(-18186), fromInt(-1299), 14)
			assertTimesInt(fromInt(-18186), fromInt(1299), -14)
			assertTimesInt(fromInt(18186), fromInt(-1299), -14)
		}
	}

	private fun assertTimesInt(expected: T, a: T, b: Int) {
		assertEquals(expected, a * b)
		assertCommutative(expected, a, scope.fromInt(b), Num<T>::times)
	}

	@Test
	fun sqr32() {
		with(scope) {
			assertEquals(ZERO, ZERO.sqr())
			assertEquals(ONE, ONE.sqr())
			assertEquals(ONE, (-ONE).sqr())
			assertEquals(fromInt(4), fromInt(2).sqr())
			assertEquals(fromInt(4), fromInt(-2).sqr())
			assertEquals(fromInt(16), fromInt(4).sqr())
			assertEquals(fromInt(16), fromInt(-4).sqr())
			assertEquals(fromInt(36), fromInt(6).sqr())
			assertEquals(fromInt(36), fromInt(-6).sqr())
			assertEquals(fromInt(100), fromInt(10).sqr())
			assertEquals(fromInt(10000), fromInt(100).sqr())
		}
	}
}

interface NumScopeTestBase<T> {
	val scope: Num.Scope<T>

	@Test
	fun zeroOne() {
		assertEquals(scope.ZERO, scope.fromInt(0))
		assertEquals(scope.ONE, scope.fromInt(1))
		assertEquals(scope.ZERO, scope.fromLong(0L))
		assertEquals(scope.ONE, scope.fromLong(1L))
		assertEquals(scope.ZERO, scope.fromLongOrNull(0L))
		assertEquals(scope.ONE, scope.fromLongOrNull(1L))
		assertEquals(scope.ZERO, scope.fromInt128(Int128.ZERO))
		assertEquals(scope.ONE, scope.fromInt128(Int128.ONE))
		assertEquals(scope.ZERO, scope.fromInt128OrNull(Int128.ZERO))
		assertEquals(scope.ONE, scope.fromInt128OrNull(Int128.ONE))
		assertEquals(scope.ZERO, scope.fromBigInt(BigInt.ZERO))
		assertEquals(scope.ONE, scope.fromBigInt(BigInt.ONE))
		assertEquals(scope.ZERO, scope.fromBigIntOrNull(BigInt.ZERO))
		assertEquals(scope.ONE, scope.fromBigIntOrNull(BigInt.ONE))
	}
}
