package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.BitwiseTestBase
import com.github.fsbarata.numeric.IntegralTestBase
import com.github.fsbarata.numeric.assertDivRem
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Int128Test: IntegralTestBase<Int128>, BitwiseTestBase<Int128> {
	override val scope = Int128
	override val maxBytes: Int = Int128.SIZE_BYTES

	@Test
	fun toByteArray() {
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES),
			Int128.ZERO.toByteArrayBE(),
		)
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES),
			Int128.ZERO.toByteArrayLE(),
		)
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES).apply { this[Int128.SIZE_BYTES - 1] = 1 },
			Int128.ONE.toByteArrayBE(),
		)
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES).apply { this[0] = 1 },
			Int128.ONE.toByteArrayLE(),
		)
		assertContentEquals(
			byteArrayOf(
				0x7f, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, // high
				-0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, // low
			),
			Int128.MAX_VALUE.toByteArrayBE(),
		)
		assertContentEquals(
			byteArrayOf(
				-0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, // low
				-0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, 0x7f, // high
			),
			Int128.MAX_VALUE.toByteArrayLE(),
		)
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES).apply { this[0] = -0x80 },
			Int128.MIN_VALUE.toByteArrayBE(),
		)
		assertContentEquals(
			ByteArray(Int128.SIZE_BYTES).apply { this[Int128.SIZE_BYTES - 1] = -0x80 },
			Int128.MIN_VALUE.toByteArrayLE(),
		)
	}

	@Test
	fun fromInt() {
		assertEquals(0, Int128.fromInt(0).toIntOrNull())
		assertEquals(3, Int128.fromInt(3).toIntOrNull())
		assertEquals(-501, Int128.fromInt(-501).toIntOrNull())
	}

	@Test
	fun fromLong() {
		assertEquals(0, Int128.fromLong(0L).toIntOrNull())
		assertEquals(-1, Int128.fromLong(-1L).toIntOrNull())
		assertEquals(3, Int128.fromLong(3L).toIntOrNull())
		assertEquals(-501, Int128.fromLong(-501L).toIntOrNull())
		assertEquals(null, Int128.fromLong(1L.shl(50)).toIntOrNull())
		assertEquals(1L.shl(50), Int128.fromLong(1L.shl(50)).toLongOrNull())
		assertEquals(
			Int.MAX_VALUE.toLong() + Int.MAX_VALUE / 2,
			Int128.fromLong(Int.MAX_VALUE.toLong() + Int.MAX_VALUE / 2).toLongOrNull()
		)
		assertEquals(
			Int.MIN_VALUE.toLong() + Int.MIN_VALUE / 2,
			Int128.fromLong(Int.MIN_VALUE.toLong() + Int.MIN_VALUE / 2).toLongOrNull()
		)
		assertEquals(-151212113421881, Int128.fromLong(-151212113421881L).toLongOrNull())
		assertEquals(0x468EDC46A39240E0, Int128.fromLong(5084243225297961184).toLongOrNull())
	}

	@Test
	fun fromBigInt() {
		assertEquals(Int128.ZERO, Int128.fromBigInt(BigInt.ZERO))
		assertEquals(Int128.ONE, Int128.fromBigInt(BigInt.ONE))
		assertEquals(Int128.fromInt(15812), Int128.fromBigInt(BigInt.fromInt(15812)))
		assertEquals(Int128.fromLong(-12399199915812), Int128.fromBigInt(BigInt.fromLong(-12399199915812)))
		assertEquals(
			Int128.fromString("-9999912399199915812"),
			Int128.fromBigInt(BigInt.fromString("-9999912399199915812"))
		)
		assertEquals(
			Int128(1938544, 0),
			// overflows
			Int128.fromBigInt(BigInt.fromString("340282366920938463463374607431770150000"))
		)
	}

	@Test
	fun compare() {
		assertEquals(0, Int128.ZERO.compareTo(Int128.ZERO))
		assertEquals(0, Int128.ONE.compareTo(Int128.ONE))
		assertEquals(0, Int128(12309, 1288).compareTo(Int128(12309, 1288)))
		assertEquals(0, Int128(-12309, 1288).compareTo(Int128(-12309, 1288)))
		assertEquals(0, Int128(-12309, -1288).compareTo(Int128(-12309, -1288)))
		assertEquals(1, Int128(-12309, -1).compareTo(Int128(-12309, -1288)))
		assertEquals(-1, Int128(-12309, -1).compareTo(Int128(-12309, 0)))
		assertEquals(-1, Int128(-12309, -1).compareTo(Int128(12309, 0)))
		assertEquals(1, Int128(-12309, 0).compareTo(Int128(12309, 0)))
		assertEquals(-1, Int128(12309, 0).compareTo(Int128(-12309, 0)))
		assertEquals(1, Int128(Long.MIN_VALUE, 0).compareTo(Int128(Long.MAX_VALUE, -1)))
		assertEquals(1, Int128(-1, 0).compareTo(Int128(Long.MIN_VALUE, 0)))
		assertEquals(-1, Int128(0, -1).compareTo(Int128(-5, -1)))
		assertEquals(-1, Int128(0, -5).compareTo(Int128(-5, -5)))
		assertEquals(-1, Int128(0, 5).compareTo(Int128(-5, 5)))
	}

	@Test
	fun fromString() {
		assertEquals(0L, Int128.fromString("0").toLongOrNull())
		assertEquals(3L, Int128.fromString("3").toLongOrNull())
		assertEquals(315121231211312L, Int128.fromString("315121231211312").toLongOrNull())
		assertEquals(-315121231211312L, Int128.fromString("-315121231211312").toLongOrNull())
		assertEquals(0x468EDC46A39240E0, Int128.fromString("5084243225297961184").toLongOrNull())

		assertEquals(
			Int128(7766279631452241919, 5),
			Int128.fromString("99999999999999999999"),
		)

		assertEquals(
			Int128(-6930898827444486144, 54210108),
			Int128.fromString("1000000000000000000000000000"),
		)
		assertEquals(
			Int128(Long.MIN_VALUE, 0L),
			actual = Int128.fromString("9223372036854775808"),
		)
		assertEquals(
			Int128(5084243225297961184L, 1708L),
			actual = Int128.fromString("31512123121121212121312"),
		)
		assertEquals(
			Int128(7368062651310448209, -23135108580669034L),
			Int128.fromString("-426767427105083491625822010125410735"),
		)
	}

	@Test
	fun iLog2() {
		assertEquals(0, Int128.fromInt(1).iLog2())
		assertEquals(1, Int128.fromInt(2).iLog2())
		assertEquals(1, Int128.fromInt(3).iLog2())
		assertEquals(2, Int128.fromInt(4).iLog2())
		assertEquals(3, Int128.fromInt(15).iLog2())
		assertEquals(4, Int128.fromInt(16).iLog2())
		assertEquals(36, Int128.fromLong(121211177434).iLog2())
		assertEquals(61, Int128.fromLong(4587658253900688810).iLog2())
		assertEquals(62, Int128.fromLong(4611686018427387904).iLog2())
		assertEquals(118, Int128.fromString("426767427105083491625822010125410735").iLog2())
		assertEquals(126, Int128.MAX_VALUE.iLog2())
	}

	@Test
	fun plusLong() {
		assertEquals(-135425168673, (Int128.fromLong(-121211177434) + (-14213991239)).toLong())
		assertEquals(-1421391239, (Int128.fromLong(-121211177434) + (119789786195)).toLong())
		assertEquals(1421391239, (Int128.fromLong(121211177434) + (-119789786195)).toLong())
		assertEquals(Int128.fromLong(-135425168673), Int128.fromLong(-121211177434) + (-14213991239))

		assertEquals(
			Int128.fromLong(-1044846154747028498),
			Int128.fromLong(3458753472623467502) + (-4503599627370496000),
		)
		assertEquals(
			Int128.fromLong(1206953658938219502),
			Int128.fromLong(3458753472623467502) + (-2251799813685248000),
		)
	}

	@Test
	fun sqr() {
		assertEquals(
			Int128(-6650129732152502000, -6592871373048748842),
			// overflows
			Int128.fromLong(-989796959493929190).sqr().sqr(),
		)
	}

	@Test
	fun pow() {
		assertEquals(Int128.ONE, Int128.pow(Int128.ZERO, 0))
		assertEquals(Int128.ONE, Int128.pow(Int128.ONE, 0))
		assertEquals(Int128.ONE, Int128.pow((-Int128.ONE), 0))
		assertEquals(Int128.ONE, Int128.pow(Int128.ONE, 4))
		assertEquals(Int128.ONE, Int128.pow(Int128.ONE, 5))
		assertEquals(Int128.ONE, Int128.pow((-Int128.ONE), 4))
		assertEquals(-Int128.ONE, Int128.pow((-Int128.ONE), 5))
		assertEquals(Int128.ONE, Int128.pow(Int128.TWO, 0))
		assertEquals(Int128.ONE, Int128.pow((-Int128.TWO), 0))
		assertEquals(Int128.fromInt(32), Int128.pow(Int128.TWO, 5))
		assertEquals(Int128.fromInt(-32), Int128.pow((-Int128.TWO), 5))
		assertEquals(Int128.ONE, Int128.pow(Int128.ONE, -1))
		assertEquals(-Int128.ONE, Int128.pow((-Int128.ONE), -1))
		assertEquals(Int128.fromInt(36), Int128.pow(Int128.fromInt(6), 2))
		assertEquals(Int128.fromInt(100), Int128.pow(Int128.fromInt(10), 2))
		assertEquals(Int128.fromInt(10000), Int128.pow(Int128.TEN, 4))
		assertEquals(Int128.fromLong(1000000000000000L), Int128.pow(Int128.fromInt(10), 15))
		assertEquals(Int128(4793518853382471680, 7302835975055466601), Int128.pow(Int128.fromInt(6), 49))
		assertEquals(Int128(0, 1.shl(26)), Int128.pow(Int128.TWO, 90))
		assertEquals(Int128.fromString("1000000000000000000000000000"), Int128.pow(Int128.TEN, 27))
		assertEquals(Int128.fromString("-1000000000000000000000000000"), Int128.pow((-Int128.TEN), 27))
		assertEquals(Int128.fromString("10000000000000000000000000000"), Int128.pow((-Int128.TEN), 28))
		assertEquals(Int128.fromString("1000000000000000000000000"), Int128.pow(Int128.fromInt(1000), 8))
		assertEquals(Int128.fromString("1000000000000000000000000"), Int128.pow(Int128.fromInt(-1000), 8))
		assertEquals(Int128.fromString("1000000000000000000000000000"), Int128.pow(Int128.fromInt(1000), 9))
		assertEquals(Int128.fromString("-1000000000000000000000000000"), Int128.pow(Int128.fromInt(-1000), 9))
	}

	@Test
	fun testToString() {
		assertEquals("0", Int128.ZERO.toString())
		assertEquals("315121231211312", Int128.fromString("315121231211312").toString(10))
		assertEquals("-315121231211312", Int128.fromString("-315121231211312").toString(10))
		assertEquals("31519123121121212121319", Int128.fromString("31519123121121212121319").toString(10))
		assertEquals("-31519123121121212121319", Int128.fromString("-31519123121121212121319").toString(10))
		assertEquals(
			"426767427105083491625822010125410735",
			Int128.fromString("426767427105083491625822010125410735").toString(10)
		)
		assertEquals(
			"55844105986793442773355413541572575232",
			Int128.fromString("55844105986793442773355413541572575232").toString(10)
		)
		assertEquals("6aca7b3db30374e40e7", Int128.fromString("31519123121121212121319").toString(16).lowercase())
		assertEquals("-6aca7b3db30374e40e7", Int128.fromString("-31519123121121212121319").toString(16).lowercase())
		assertEquals(
			"-426767427105083491625822010125410735",
			Int128.fromString("-426767427105083491625822010125410735").toString(10)
		)
		assertEquals(
			"1000000000000000000000000000",
			Int128.fromString("1000000000000000000000000000").toString(),
		)
	}

	@Test
	fun toBigInt() {
		assertEquals(BigInt.ZERO, Int128.ZERO.toBigInt())
		assertEquals(BigInt.fromString("315121231211312"), Int128.fromString("315121231211312").toBigInt())
		assertEquals(BigInt.fromString("-315121231211312"), Int128.fromString("-315121231211312").toBigInt())
		assertEquals(
			BigInt.fromString("31519123121121212121319"),
			Int128.fromString("31519123121121212121319").toBigInt()
		)
		assertEquals(
			BigInt.fromString("-31519123121121212121319"),
			Int128.fromString("-31519123121121212121319").toBigInt()
		)
		assertEquals(
			BigInt.fromString("426767427105083491625822010125410735"),
			Int128.fromString("426767427105083491625822010125410735").toBigInt()
		)
		assertEquals(
			BigInt.fromString("55844105986793442773355413541572575232"),
			Int128.fromString("55844105986793442773355413541572575232").toBigInt()
		)
		assertEquals(
			BigInt.fromString("6aca7b3db30374e40e7", 16),
			Int128.fromString("6aca7b3db30374e40e7", 16).toBigInt()
		)
		assertEquals(
			BigInt.fromString("-6aca7b3db30374e40e7", 16),
			Int128.fromString("-6aca7b3db30374e40e7", 16).toBigInt()
		)
		assertEquals(
			BigInt.fromString("-426767427105083491625822010125410735"),
			Int128.fromString("-426767427105083491625822010125410735").toBigInt(),
		)
		assertEquals(
			BigInt.fromString("1000000000000000000000000000"),
			Int128.fromString("1000000000000000000000000000").toBigInt(),
		)
	}

	@Test
	fun divRem() {
		assertDivRem(
			Int128(0, 1), 0L,
			Int128.MIN_VALUE.divRem(Long.MIN_VALUE),
		)
	}
}