package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.NumTestBase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LongFractionTest: NumTestBase<LongFraction> {
	override val scope = LongFraction

	@Test
	fun plus() {
		assertEquals(3L over 5L, (1L over 5L) + (2L over 5L))
		assertEquals(67L over 483L, (1L over 23L) + (2L over 21L))
		assertEquals(1L over 5L, (1L over 15L) + (2L over 15L))
		assertEquals(1L over 6L, (1L over 15L) + (1L over 10L))
		assertEquals(7L over 6L, (1L over 6L) + 1)
		assertEquals(5L over 6L, (-1L over 6L) + 1)
	}

	@Test
	fun minus() {
		assertEquals(-1L over 5L, (1L over 5L) - (2L over 5L))
		assertEquals(-25L over 483L, (1L over 23L) - (2L over 21L))
		assertEquals(1L over 15L, (2L over 15L) - (1L over 15L))
		assertEquals(1L over 3L, (4L over 10L) - (1L over 15L))
		assertEquals(-5L over 6L, (1L over 6L) - 1)
	}

	@Test
	fun compare() {
		assertTrue((1L over 5L) < (2L over 5L))
		assertTrue((3L over 5L) > (1L over 2L))
		assertTrue((3L over 5L) < (3L over 4L))
		assertTrue((3L over 5L) > (3L over 6L))

		assertTrue((2L over 5L) > (-3L over 4L))
		assertTrue((-2L over 5L) < (1L over 4L))
		assertTrue((-3L over 5L) < (-1L over 2L))
		assertTrue((-2L over 5L) > (-3L over 4L))

		assertTrue((1L over 0L) > (3L over 5L))
		assertTrue((1L over 0L) > (-3L over 5L))
		assertTrue((1L over 0L) <= (3L over 0L))
		assertTrue((1L over 0L) > (-3L over 0L))

		assertTrue((-1L over 0L) < (-3L over 5L))
		assertTrue((-1L over 0L) < (3L over 5L))
		assertTrue((-3L over 0L) <= (-1L over 0L))
		assertTrue((-1L over 0L) < (3L over 0L))

		assertTrue((1L over 0L) > (-1L over 0L))

		assertTrue((0L over 1L) > (-1L over 0L))
		assertTrue((0L over 1L) > (-1L over 3L))
		assertTrue((0L over 1L) < (1L over 3L))
		assertTrue((0L over 1L) < (1L over 0L))


		assertEquals(0, (1L over 5L).compareTo(1L over 5L))
		assertEquals(0, (1L over 5L).compareTo(2L over 10L))
		assertEquals(0, (1L over -3L).compareTo(-3L over 9L))
		assertEquals(0, (0L over 1L).compareTo(0L over 2L))
		assertEquals(0, (0L over -1L).compareTo(0L over 2L))
		assertEquals(0, (0L over 1L).compareTo(0L over -2L))
		assertEquals(0, (0L over -1L).compareTo(0L over -2L))
		assertEquals(0, (0L over 0L).compareTo(0L over 0L))
	}

	@Test
	fun times() {
		assertEquals(2L over 25L, (1L over 5L) * (2L over 5L))
		assertEquals(-2L over 161L, (3L over 23L) * (-2L over 21L))
		assertEquals(-13L over 225L, (-13L over 15L) * (1L over 15L))
		assertEquals(1L over 10L, (4L over 10L) * (1L over 4L))
	}

	@Test
	fun div() {
		assertEquals(3L over 2L, (3L over 5L) / (2L over 5L))
		assertEquals(-63L over 46L, (-3L over 23L) / (2L over 21L))
		assertEquals(-13L over 36L, (13L over 15L) / (-12L over 5L))
		assertEquals(8L over 5L, (4L over 10L) / (1L over 4L))
	}

	@Test
	fun recip() {
		assertEquals(3L over 2L, (2L over 3L).recip())
		assertEquals(-3L over 2L, (-2L over 3L).recip())
		assertEquals(-3L, (-2L over 3L).recip().numerator)
	}

	@Test
	fun components() {
		assertEquals(3L, (3L over 2L).numerator)
		assertEquals(2L, (3L over 2L).denominator)
		assertEquals(63L, (63L over 42L).numerator)
		assertEquals(42L, (63L over 42L).denominator)
	}

	@Test
	fun reduced() {
		assertEquals(3L over 2L, (63L over 42L).reduced())
		assertEquals(2L, (2L over 3L).reduced().numerator)
		assertEquals(3L, (2L over 3L).reduced().denominator)
		assertEquals(3L, (63L over 42L).reduced().numerator)
		assertEquals(2L, (63L over 42L).reduced().denominator)
		assertEquals(1L, (21L over 42L).reduced().numerator)
		assertEquals(2L, (21L over 42L).reduced().denominator)
	}

	@Test
	fun equals() {
		assertEqualsHashCode(3L over 2L, 3L over 2L)
		assertEqualsHashCode(1L over 3L, 1L over 3L)
		assertEqualsHashCode(1L over 3L, 3L over 9L)
		assertEqualsHashCode(-1L over 3L, 3L over -9L)
		assertEqualsHashCode(1L over 3L, -3L over -9L)
		assertEqualsHashCode(0L over 1L, 0L over -1L)
		assertEqualsHashCode(LongFraction.ZERO, 0L over -1L)
	}

	@Test
	fun parse() {
		assertEquals(1L over 3L, LongFraction.fromString("1/3"))
		assertEquals(1L over 3L, LongFraction.parseOrNull("1/3"))
		assertEquals(-1L over 3L, LongFraction.fromString("-1/3"))
		assertEquals(-1L over 3L, LongFraction.parseOrNull("-1/3"))
		assertEquals(-13L over 5L, LongFraction.fromString("13/-5"))
		assertEquals(-13L over 5L, LongFraction.parseOrNull("13/-5"))
		assertEquals(1L over 0L, LongFraction.fromString("1/0"))
		assertEquals(1L over 0L, LongFraction.parseOrNull("1/0"))
		assertEquals(-1L over 0L, LongFraction.fromString("-1/0"))
		assertEquals(-1L over 0L, LongFraction.parseOrNull("-1/0"))
		assertEquals(0L over 0L, LongFraction.fromString("0/0"))
		assertEquals(0L over 0L, LongFraction.parseOrNull("0/0"))
	}

	@Test
	fun parseOrNullInvalid() {
		assertNull(LongFraction.parseOrNull("a/3"))
		assertNull(LongFraction.parseOrNull("-1/a"))
		assertNull(LongFraction.parseOrNull("a11/-5"))
		assertNull(LongFraction.parseOrNull("1"))
		assertNull(LongFraction.parseOrNull("0"))
		assertNull(LongFraction.parseOrNull("NaN"))
	}

	private fun assertEqualsHashCode(expect: LongFraction, actual: LongFraction) {
		assertEquals(expect, actual)
		assertEquals(expect.hashCode(), actual.hashCode())
	}
}
