package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegralSqrtTest {

	@Test
	fun isqrt() {
		assertEquals(0, IntNumScope.isqrt(0))
		assertEquals(2, IntNumScope.isqrt(4))
		assertEquals(5, IntNumScope.isqrt(35))
		assertEquals(6, IntNumScope.isqrt(36))
		assertEquals(6, IntNumScope.isqrt(37))
		assertEquals(1L, LongNumScope.isqrt(3L))
		assertEquals(2L, LongNumScope.isqrt(4L))
		assertEquals(1073741824L, LongNumScope.isqrt(1152921504606846976))
		assertEquals(Int128.fromLong(2L), Int128.isqrt(Int128.fromLong(4L)))
		assertEquals(Int128.fromLong(6074000999), Int128.isqrt(Int128(0, 2)))
		assertEquals(Int128.fromLong(6074000999), Int128.isqrt(Int128(581896767, 2)))
		assertEquals(Int128.fromLong(6074001000), Int128.isqrt(Int128(581896768, 2)))
		assertEquals(Int128.fromString("13043817825332782212"), Int128.isqrt(Int128.MAX_VALUE))
		assertEquals(
			BigInt.fromString("13043817825332782212"),
			BigInt.isqrt(BigInt.fromString("170141183460469231731687303715884105728"))
		)
	}
}