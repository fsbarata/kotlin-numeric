package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.Int128
import kotlin.test.Test
import kotlin.test.assertEquals

interface BitwiseTestBase<T: Bitwise<T>> {
	val scope: Integral.Scope<T>
	val maxBytes: Int

	@Test
	fun bitwise() {
		with(scope) {
			assertEquals(ZERO, ZERO.and(ZERO))
			assertEquals(ZERO, ZERO.or(ZERO))
			assertEquals(ZERO, ZERO.xor(ZERO))
			assertEquals(fromInt(-1), ZERO.not())

			assertCommutative(ZERO, ZERO, fromInt(159), Bitwise<T>::and)
			assertCommutative(fromInt(159), ZERO, fromInt(159), Bitwise<T>::or)
			assertCommutative(fromInt(159), ZERO, fromInt(159), Bitwise<T>::xor)
			assertEquals(fromInt(-160), fromInt(159).not())

			assertEquals(fromInt(Int.MAX_VALUE), fromInt(Int.MIN_VALUE).not())
			assertEquals(fromInt(Int.MAX_VALUE), fromInt(Int.MIN_VALUE).not())
			assertEquals(fromInt(Int.MIN_VALUE), fromInt(Int.MAX_VALUE).not())

			if (maxBytes < 8) return
			assertEquals(fromLong(-2147483649), fromLong(2147483648).not())
			assertEquals(fromLong(2147483648), fromLong(-2147483649).not())
			assertEquals(fromLong(-9123488812301240), fromLong(9123488812301239).not())
			assertEquals(fromLong(9123488812301238), fromLong(-9123488812301239).not())
			assertEquals(fromLong(Long.MIN_VALUE), fromLong(Long.MAX_VALUE).not())
			assertEquals(fromLong(Long.MAX_VALUE), fromLong(Long.MIN_VALUE).not())

			if (maxBytes < 16) return
			assertCommutative(
				ZERO,
				ZERO, fromInt128(Int128(-6994836121274985131, 1807003)), Bitwise<T>::and,
			)
			assertCommutative(
				fromInt128(Int128(-6994836121274985131, 1807003)),
				ZERO, fromInt128(Int128(-6994836121274985131, 1807003)), Bitwise<T>::or,
			)
			assertCommutative(
				fromInt128(Int128(-6994836121274985131, 1807003)),
				ZERO, fromInt128(Int128(-6994836121274985131, 1807003)), Bitwise<T>::xor,
			)

			assertCommutative(
				fromLong(9088028413022485),
				fromInt128(Int128(-6994836121274985131, 1807003)), fromLong(9123488812301239), Bitwise<T>::and,
			)
			assertCommutative(
				fromInt128(Int128(-6994800660875706377, 1807003)),
				fromInt128(Int128(-6994836121274985131, 1807003)), fromLong(9123488812301239), Bitwise<T>::or,
			)
			assertCommutative(
				fromInt128(Int128(-7003888689288728862, 1807003)),
				fromInt128(Int128(-6994836121274985131, 1807003)), fromLong(9123488812301239), Bitwise<T>::xor,
			)
			assertEquals(
				fromInt128(Int128(6994836121274985130, -1807004)),
				fromInt128(Int128(-6994836121274985131, 1807003)).not(),
			)
		}
	}

	@Test
	fun bitwiseShift() {
		with(scope) {
			assertEquals(fromInt(0), fromInt(1).shr(1))
			assertEquals(fromInt(-1), fromInt(-1).shr(1))
			assertEquals(fromInt(2), fromInt(1).shl(1))
			assertEquals(fromInt(-2), fromInt(-1).shl(1))
			assertEquals(fromInt(8), fromInt(1).shl(3))
			assertEquals(fromInt(16), fromInt(2).shl(3))
			assertEquals(fromInt(2), fromInt(16).shr(3))
			assertEquals(fromInt(-2), fromInt(-16).shr(3))

			assertEquals(fromInt(Int.MIN_VALUE), -ONE.shl(31))
			assertEquals(fromInt((Int.MAX_VALUE + 1L).shr(1).toInt()), ONE.shl(30))

			assertEquals(fromInt(0), fromInt(1).shr(64))
			assertEquals(fromInt(0), fromInt(0).shr(64))
			assertEquals(fromInt(-1), fromInt(-1).shr(64))

			if (maxBytes < 8) return
			assertEquals(fromLong((Int.MAX_VALUE + 1L).shl(1)), ONE.shl(32))
			assertEquals(fromLong(512412), fromInt(512412).shr(0))
			assertEquals(fromLong(16012), fromInt(512412).shr(5))
			assertEquals(fromLong(-16013), fromInt(-512412).shr(5))
			assertEquals(fromLong(2147483648), fromInt(1).shl(31))
			assertEquals(fromLong(4294967296), fromInt(1).shl(32))
			assertEquals(fromInt(1), fromLong(4294967296).shr(32))
			assertEquals(fromInt(-1), fromLong(-4294967296).shr(32))
			assertEquals(fromLong(13510798882111488), fromInt(3).shl(52))
			assertEquals(fromLong(4503599627370496000), fromInt(1000).shl(52))
			assertEquals(fromLong(-4503599627370496000), fromInt(-1000).shl(52))
			assertEquals(fromLong(-4294967296), fromInt(-268435456).shl(4))
			assertEquals(fromLong(3787849294), fromLong(121211177434).shr(5))
			assertEquals(fromLong(3787849294), fromLong(121211177434).shl(-5))

			assertEquals(fromLong(Long.MIN_VALUE), -ONE.shl(63))
			assertEquals(fromLong(-(Long.MIN_VALUE.shr(1))), ONE.shl(62))

			if (maxBytes < 16) return
			assertEquals(fromInt128(Int128(0, 1)), ONE.shl(64))
			assertEquals(
				fromInt128(Int128.fromString("-22222222222222222222")),
				fromInt128(Int128.fromString("-11111111111111111111")).shl(1),
			)
			assertEquals(
				fromInt128(Int128.fromString("-11111111111111111111")),
				fromInt128(Int128.fromString("-22222222222222222222")).shr(1),
			)
			assertEquals(fromInt128(Int128(0, 1024)), fromInt(268435456).shl(46))
			assertEquals(fromInt128(Int128(0, 121211177434)), fromLong(121211177434).shl(64))
			assertEquals(fromInt128(Int128(0, 60605588717)), fromLong(121211177434).shl(63))
			assertEquals(fromInt128(Int128(1022302231203938304, 7)), fromLong(121211177434).shl(30))
			assertEquals(fromInt128(Int128(0, -60605588717)), fromLong(-121211177434).shl(63))
			assertEquals(fromInt128(Int128(-1022302231203938304, -8)), fromLong(-121211177434).shl(30))
			assertEquals(fromLong(121211177434), fromInt128(Int128(0, 121211177434)).shr(64))
			assertEquals(fromLong(-121211177434), fromInt128(Int128(-1022302231203938304, -8)).shr(30))
			assertEquals(
				fromInt128(Int128(3775478147785424896, 1)),
				fromInt128(Int128(7550956295570849792, 2)).shl(-1),
			)

			assertEquals(
				fromInt128(Int128(low = -3775478148512670606, high = -2)),
				fromInt128(Int128(low = 7335632962598440505, high = -1)).shl(1)
			)
			assertEquals(
				fromInt128(Int128(low = 7335632962598440505, high = -1)),
				fromInt128(Int128(low = -3775478148512670606, high = -2)).shr(1)
			)

			assertEquals(fromInt128(Int128.MIN_VALUE), -ONE.shl(127))
			assertEquals(fromInt128(Int128(0, 4611686018427387904)), ONE.shl(126))
		}
	}
}