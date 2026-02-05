package com.github.fsbarata.numeric

import kotlin.test.Test
import kotlin.test.assertEquals

class DivideTest {
	fun <T> Integral.Scope<T>.assertDivideBound(dividend: T, divisor: T, result: (RoundingType) -> T) {
		RoundingType.entries.forEach { type ->
			val expected = result(type)
			assertEquals(expected, divide(dividend, divisor, type), "$dividend/$divisor $type")
		}
	}
	fun <T> Integral.Scope<T>.assertDivide(dividend: T, divisor: T, result: (RoundingType) -> T) {
		RoundingType.entries.forEach { type ->
			val expected = result(type)
			assertEquals(expected, divide(dividend, divisor, type), "$dividend/$divisor $type")
			assertEquals(expected, divide(-dividend, -divisor, type), "${-dividend}/${-divisor} $type")
		}
	}

	@Test
	fun divide() {
		with(IntNumScope) {
			assertDivide(5, 2) { type ->
				when (type) {
					RoundingType.TRUNCATE -> 2
					RoundingType.AWAY_FROM_ZERO -> 3
					RoundingType.HALF_UP -> 3
					RoundingType.FLOOR -> 2
					RoundingType.CEIL -> 3
				}
			}

			assertDivide(6, 2) { 3 }

			assertDivide(7, 3) { type ->
				when (type) {
					RoundingType.TRUNCATE -> 2
					RoundingType.AWAY_FROM_ZERO -> 3
					RoundingType.HALF_UP -> 2
					RoundingType.FLOOR -> 2
					RoundingType.CEIL -> 3
				}
			}

			assertDivide(-5, 2) { type ->
				when (type) {
					RoundingType.TRUNCATE -> -2
					RoundingType.AWAY_FROM_ZERO -> -3
					RoundingType.HALF_UP -> -3
					RoundingType.FLOOR -> -3
					RoundingType.CEIL -> -2
				}
			}

			assertDivide(7, -3) { type ->
				when (type) {
					RoundingType.TRUNCATE -> -2
					RoundingType.AWAY_FROM_ZERO -> -3
					RoundingType.HALF_UP -> -2
					RoundingType.FLOOR -> -3
					RoundingType.CEIL -> -2
				}
			}

			assertDivide(1375, -8) { type ->
				when (type) {
					RoundingType.TRUNCATE -> -171
					RoundingType.AWAY_FROM_ZERO -> -172
					RoundingType.HALF_UP -> -172
					RoundingType.FLOOR -> -172
					RoundingType.CEIL -> -171
				}
			}
		}
	}

	@Test
	fun longDivide() {
		with(LongExactNumScope) {
			assertDivide(5, 10) { type ->
				when (type) {
					RoundingType.TRUNCATE -> 0
					RoundingType.AWAY_FROM_ZERO -> 1
					RoundingType.HALF_UP -> 1
					RoundingType.FLOOR -> 0
					RoundingType.CEIL -> 1
				}
			}
			assertDivide(1375L, -8L) { type ->
				when (type) {
					RoundingType.TRUNCATE -> -171
					RoundingType.AWAY_FROM_ZERO -> -172
					RoundingType.HALF_UP -> -172
					RoundingType.FLOOR -> -172
					RoundingType.CEIL -> -171
				}
			}
			assertDivideBound(Long.MIN_VALUE, 10) { type ->
				when (type) {
					RoundingType.TRUNCATE -> -922337203685477580L
					RoundingType.AWAY_FROM_ZERO -> -922337203685477581L
					RoundingType.HALF_UP -> -922337203685477581L
					RoundingType.FLOOR -> -922337203685477581L
					RoundingType.CEIL -> -922337203685477580L
				}
			}
		}
	}
}