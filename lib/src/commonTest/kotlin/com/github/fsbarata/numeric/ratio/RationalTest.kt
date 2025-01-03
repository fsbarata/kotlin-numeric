package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.NumTestBase
import com.github.fsbarata.numeric.ints.BigInt
import kotlin.test.Test
import kotlin.test.assertEquals

class RationalTest: NumTestBase<Rational> {
	override val scope = Rational

	@Test
	fun plus() {
		assertEqualsHashCode(3 overRational 5, (1 overRational 5) + (2 overRational 5))
		assertEqualsHashCode(67 overRational 483, (1 overRational 23) + (2 overRational 21))
		assertEqualsHashCode(1 overRational 5, (1 overRational 15) + (2 overRational 15))
		assertEqualsHashCode(1 overRational 6, (1 overRational 15) + (1 overRational 10))
		assertEqualsHashCode(7 overRational 6, (1 overRational 6) + 1)
		assertEqualsHashCode(5 overRational 6, (-1 overRational 6) + 1)
	}

	@Test
	fun minus() {
		assertEqualsHashCode(-1 overRational 5, (1 overRational 5) - (2 overRational 5))
		assertEqualsHashCode(-25 overRational 483, (1 overRational 23) - (2 overRational 21))
		assertEqualsHashCode(1 overRational 15, (2 overRational 15) - (1 overRational 15))
		assertEqualsHashCode(1 overRational 3, (4 overRational 10) - (1 overRational 15))
		assertEqualsHashCode(-5 overRational 6, (1 overRational 6) - 1)
	}

	@Test
	fun times() {
		assertEqualsHashCode(2 overRational 25, (1 overRational 5) * (2 overRational 5))
		assertEqualsHashCode(-2 overRational 161, (3 overRational 23) * (-2 overRational 21))
		assertEqualsHashCode(-13 overRational 225, (-13 overRational 15) * (1 overRational 15))
		assertEqualsHashCode(1 overRational 10, (4 overRational 10) * (1 overRational 4))
	}

	@Test
	fun div() {
		assertEqualsHashCode(3 overRational 2, (3 overRational 5) / (2 overRational 5))
		assertEqualsHashCode(-63 overRational 46, (-3 overRational 23) / (2 overRational 21))
		assertEqualsHashCode(-13 overRational 36, (13 overRational 15) / (-12 overRational 5))
		assertEqualsHashCode(8 overRational 5, (4 overRational 10) / (1 overRational 4))
	}

	@Test
	fun components() {
		assertEquals(BigInt.fromInt(3), (3 overRational 2).numerator)
		assertEquals(BigInt.fromInt(2), (3 overRational 2).denominator)
		assertEquals(BigInt.fromInt(63), (63 overRational 42).numerator)
		assertEquals(BigInt.fromInt(42), (63 overRational 42).denominator)
	}

	@Test
	fun reduced() {
		assertEqualsHashCode(3 overRational 2, (63 overRational 42).reduced())
		assertEquals(BigInt.fromInt(2), (2 overRational 3).reduced().numerator)
		assertEquals(BigInt.fromInt(3), (2 overRational 3).reduced().denominator)
		assertEquals(BigInt.fromInt(3), (63 overRational 42).reduced().numerator)
		assertEquals(BigInt.fromInt(2), (63 overRational 42).reduced().denominator)
		assertEquals(BigInt.fromInt(1), (21 overRational 42).reduced().numerator)
		assertEquals(BigInt.fromInt(2), (21 overRational 42).reduced().denominator)
	}

	@Test
	fun equals() {
		assertEquals(3 overRational 2, 3 overRational 2)
		assertEquals(1 overRational 3, 1 overRational 3)
		assertEquals(1 overRational 3, 3 overRational 9)
		assertEquals(-1 overRational 3, 3 overRational -9)
		assertEquals(1 overRational 3, -3 overRational -9)
	}

	@Test
	fun hashcode() {
		assertEquals((3 overRational 2).hashCode(), (3 overRational 2).hashCode())
		assertEquals((1 overRational 3).hashCode(), (1 overRational 3).hashCode())
		assertEquals((1 overRational 3).hashCode(), (3 overRational 9).hashCode())
		assertEquals((-1 overRational 3).hashCode(), (3 overRational -9).hashCode())
		assertEquals((1 overRational 3).hashCode(), (-3 overRational -9).hashCode())
	}

	private infix fun Int.overRational(denominator: Int) = Rational.create(this, denominator)

	private fun assertEqualsHashCode(expect: Rational, actual: Rational) {
		assertEquals(expect, actual)
		assertEquals(expect.hashCode(), actual.hashCode())
	}
}
