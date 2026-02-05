package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ratio.LongFraction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AverageTest {
	@Test
	fun averageOrNull() {
		assertTrue(DoubleNumScope.averageOrNull(emptyList()) == null)
		assertEquals(
			LongFraction.create(3, 10),
			LongFraction.averageOrNull(
				listOf(
					LongFraction.create(1, 2),
					LongFraction.create(-1, 10),
					LongFraction.create(1, 5),
					LongFraction.create(1, 10),
					LongFraction.create(4, 5)
				)
			)
		)
	}

	@Test
	fun averageBy() {
		assertTrue(LongFraction.averageOfOrNull(emptyList(), LongFraction::fromInt) == null)
		assertEquals(
			0.6,
			DoubleNumScope.averageOfOrNull(listOf(5, -1, 2, 1, 8)) { it / 5.0 },
		)
	}
}