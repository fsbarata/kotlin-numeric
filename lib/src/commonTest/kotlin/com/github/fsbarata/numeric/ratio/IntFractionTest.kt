package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.NumTestBase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IntFractionTest: NumTestBase<IntFraction> {
	override val scope = IntFraction

	@Test
	fun plus() {
		assertEquals(3 over 5, (1 over 5) + (2 over 5))
		assertEquals(67 over 483, (1 over 23) + (2 over 21))
		assertEquals(1 over 5, (1 over 15) + (2 over 15))
		assertEquals(1 over 6, (1 over 15) + (1 over 10))
		assertEquals(7 over 6, (1 over 6) + 1)
		assertEquals(5 over 6, (-1 over 6) + 1)
		assertEquals(4049 over 3920, (50000 over 49000) + (1000 over 80000))
		assertEquals(184353643 over 15222, (14_327_313 over 1183) + (1093 over 16_637_646))
		assertEquals(23043 over 10_000, (39_233 over 50_000) + (151_964 over 100_000))
	}

	@Test
	fun minus() {
		assertEquals(-1 over 5, (1 over 5) - (2 over 5))
		assertEquals(-25 over 483, (1 over 23) - (2 over 21))
		assertEquals(1 over 15, (2 over 15) - (1 over 15))
		assertEquals(1 over 3, (4 over 10) - (1 over 15))
		assertEquals(-5 over 6, (1 over 6) - 1)
	}

	@Test
	fun compare() {
		assertTrue((1 over 5) < (2 over 5))
		assertTrue((3 over 5) > (1 over 2))
		assertTrue((3 over 5) < (3 over 4))
		assertTrue((3 over 5) > (3 over 6))

		assertTrue((2 over 5) > (-3 over 4))
		assertTrue((-2 over 5) < (1 over 4))
		assertTrue((-3 over 5) < (-1 over 2))
		assertTrue((-2 over 5) > (-3 over 4))

		assertTrue((1 over 0) > (3 over 5))
		assertTrue((1 over 0) > (-3 over 5))
		assertTrue((1 over 0) <= (3 over 0))
		assertTrue((1 over 0) > (-3 over 0))

		assertTrue((-1 over 0) < (-3 over 5))
		assertTrue((-1 over 0) < (3 over 5))
		assertTrue((-3 over 0) <= (-1 over 0))
		assertTrue((-1 over 0) < (3 over 0))

		assertTrue((1 over 0) > (-1 over 0))

		assertTrue((0 over 1) > (-1 over 0))
		assertTrue((0 over 1) > (-1 over 3))
		assertTrue((0 over 1) < (1 over 3))
		assertTrue((0 over 1) < (1 over 0))


		assertEquals(0, (1 over 5).compareTo(1 over 5))
		assertEquals(0, (1 over 5).compareTo(2 over 10))
		assertEquals(0, (1 over -3).compareTo(-3 over 9))
		assertEquals(0, (0 over 1).compareTo(0 over 2))
		assertEquals(0, (0 over -1).compareTo(0 over 2))
		assertEquals(0, (0 over 1).compareTo(0 over -2))
		assertEquals(0, (0 over -1).compareTo(0 over -2))
		assertEquals(0, (0 over 0).compareTo(0 over 0))
	}

	@Test
	fun times() {
		assertEquals(2 over 25, (1 over 5) * (2 over 5))
		assertEquals(-2 over 161, (3 over 23) * (-2 over 21))
		assertEquals(-13 over 225, (-13 over 15) * (1 over 15))
		assertEquals(1 over 10, (4 over 10) * (1 over 4))
		assertEquals(5 over 392, (50000 over 49000) * (1000 over 80000))
		assertEquals(12111 over 15222, (14_327_313 over 1183) * (1093 over 16_637_646))
		assertEquals(184353642 over 15, (16_637_646 over 1183) * (14_327_313 over 16395))
	}

	@Test
	fun div() {
		assertEquals(3 over 2, (3 over 5) / (2 over 5))
		assertEquals(-63 over 46, (-3 over 23) / (2 over 21))
		assertEquals(-13 over 36, (13 over 15) / (-12 over 5))
		assertEquals(8 over 5, (4 over 10) / (1 over 4))
	}

	@Test
	fun recip() {
		assertEquals(3 over 2, (2 over 3).recip())
		assertEquals(-3 over 2, (-2 over 3).recip())
		assertEquals(-3, (-2 over 3).recip().numerator)
	}

	@Test
	fun components() {
		assertEquals(3, (3 over 2).numerator)
		assertEquals(2, (3 over 2).denominator)
		assertEquals(63, (63 over 42).numerator)
		assertEquals(42, (63 over 42).denominator)
	}

	@Test
	fun reduced() {
		assertEquals(3 over 2, (63 over 42).reduced())
		assertEquals(2, (2 over 3).reduced().numerator)
		assertEquals(3, (2 over 3).reduced().denominator)
		assertEquals(3, (63 over 42).reduced().numerator)
		assertEquals(2, (63 over 42).reduced().denominator)
		assertEquals(1, (21 over 42).reduced().numerator)
		assertEquals(2, (21 over 42).reduced().denominator)
	}

	@Test
	fun approxNumerator() {
		assertEquals(150, (3 over 2).approxNumerator(denominator = 100))
		assertEquals(422, (511230 over 121113).approxNumerator(denominator = 100))
		assertEquals(12663, (511230 over 121113).approxNumerator(denominator = 3000))
	}

	@Test
	fun equals() {
		assertEqualsHashCode(3 over 2, 3 over 2)
		assertEqualsHashCode(1 over 3, 1 over 3)
		assertEqualsHashCode(1 over 3, 3 over 9)
		assertEqualsHashCode(-1 over 3, 3 over -9)
		assertEqualsHashCode(1 over 3, -3 over -9)
		assertEqualsHashCode(0 over 1, 0 over -1)
		assertEqualsHashCode(IntFraction.ZERO, 0 over -1)
	}

	@Test
	fun parse() {
		assertEquals(1 over 3, IntFraction.fromString("1/3"))
		assertEquals(1 over 3, IntFraction.parseOrNull("1/3"))
		assertEquals(-1 over 3, IntFraction.fromString("-1/3"))
		assertEquals(-1 over 3, IntFraction.parseOrNull("-1/3"))
		assertEquals(-13 over 5, IntFraction.fromString("13/-5"))
		assertEquals(-13 over 5, IntFraction.parseOrNull("13/-5"))
		assertEquals(1 over 0, IntFraction.fromString("1/0"))
		assertEquals(1 over 0, IntFraction.parseOrNull("1/0"))
		assertEquals(-1 over 0, IntFraction.fromString("-1/0"))
		assertEquals(-1 over 0, IntFraction.parseOrNull("-1/0"))
		assertEquals(0 over 0, IntFraction.fromString("0/0"))
		assertEquals(0 over 0, IntFraction.parseOrNull("0/0"))
	}

	@Test
	fun parseOrNullInvalid() {
		assertNull(IntFraction.parseOrNull("a/3"))
		assertNull(IntFraction.parseOrNull("-1/a"))
		assertNull(IntFraction.parseOrNull("a11/-5"))
		assertNull(IntFraction.parseOrNull("1"))
		assertNull(IntFraction.parseOrNull("0"))
		assertNull(IntFraction.parseOrNull("NaN"))
	}

	private fun assertEqualsHashCode(expect: IntFraction, actual: IntFraction) {
		assertEquals(expect, actual)
		assertEquals(expect.hashCode(), actual.hashCode())
	}
}
