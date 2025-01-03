package com.github.fsbarata.numeric

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsKtTest {
	@Test
	fun testGreatestCommonDenominator() {
		assertEquals(15L, LongNumScope.greatestCommonDenominator(15L, 30L))
		assertEquals(15L, LongNumScope.greatestCommonDenominator(30L, 15L))
		assertEquals(4L, LongNumScope.greatestCommonDenominator(124812L, 100L))
		assertEquals(50L, LongNumScope.greatestCommonDenominator(350L, 100L))
		assertEquals(5L, LongNumScope.greatestCommonDenominator(25L, 10L))
		assertEquals(5L, LongNumScope.greatestCommonDenominator(10L, 25L))
		assertEquals(1L, LongNumScope.greatestCommonDenominator(12129, 2893))
		assertEquals(5L, LongNumScope.greatestCommonDenominator(-10L, 25L))
		assertEquals(5L, LongNumScope.greatestCommonDenominator(10L, -25L))
	}

	@Test
	fun testFrexp() {
		assertEquals(Pair(0L, 0), frexp(0.0))

		assertFrexp(-1.0)

		assertFrexp(10.25)
		assertEquals(Pair(5770237022568448L, -49), frexp(10.25))
		assertFrexp(-10.25)
		assertEquals(Pair(-5770237022568448L, -49), frexp(-10.25))
		assertFrexp(-1e20)
		assertEquals(Pair(-6103515625000000, 14), frexp(-1e20))
		assertFrexp(5.234182392674965E25)
		assertEquals(Pair(5747109617455696, 31), frexp(1.2341823926749643E25))
	}

	private fun assertFrexp(value: Double) {
		val (mantissa, exponent) = frexp(value)
		val r = if (exponent >= 0) mantissa * DoubleNumScope.pow(2.0, exponent)
		else mantissa / DoubleNumScope.pow(2.0, -exponent)
		assertEquals(r, value)
	}


	@Test
	fun iLog2() {
		assertEquals(0, 1.iLog2())
		assertEquals(1, 2.iLog2())
		assertEquals(1, 3.iLog2())
		assertEquals(2, 4.iLog2())
		assertEquals(3, 8.iLog2())
		assertEquals(2, 7.iLog2())
		assertEquals(30, Int.MAX_VALUE.iLog2())
		assertEquals(31, (1L + Int.MAX_VALUE).iLog2())
		assertEquals(61, 4611686018427387903.iLog2())
		assertEquals(62, 4611686018427387904.iLog2())
		assertEquals(62, Long.MAX_VALUE.iLog2())
	}

	@Test
	fun iLog2abs() {
		assertEquals(0, 1.iLog2abs())
		assertEquals(0, (-1).iLog2abs())
		assertEquals(1, 2.iLog2abs())
		assertEquals(1, (-2).iLog2abs())
		assertEquals(1, 3.iLog2abs())
		assertEquals(1, (-3).iLog2abs())
		assertEquals(2, 4.iLog2abs())
		assertEquals(2, (-4).iLog2abs())
		assertEquals(30, Int.MAX_VALUE.iLog2abs())
		assertEquals(31, (1L + Int.MAX_VALUE).iLog2abs())
		assertEquals(31, Int.MIN_VALUE.iLog2abs())
		assertEquals(61, 4611686018427387903.iLog2abs())
		assertEquals(61, (-4611686018427387903).iLog2abs())
		assertEquals(62, 4611686018427387904.iLog2abs())
		assertEquals(62, (-4611686018427387904).iLog2abs())
		assertEquals(62, Long.MAX_VALUE.iLog2abs())
		assertEquals(63, Long.MIN_VALUE.iLog2abs())
	}
}