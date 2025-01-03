package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.BitwiseTestBase
import com.github.fsbarata.numeric.IntegralTestBase
import com.github.fsbarata.numeric.assertCommutative
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BigIntTest: IntegralTestBase<BigInt>, BitwiseTestBase<BigInt> {
	override val scope = BigInt
	override val maxBytes: Int = Int.MAX_VALUE

	@Test
	fun constructor() {
		assertTrue(BigInt(byteArrayOf()).toByteArrayBE().isEmpty())
		assertEquals(0, BigInt(byteArrayOf()).toInt())
		assertEquals(3, BigInt(byteArrayOf(3)).toByteArrayBE().singleOrNull())
		assertEquals(3, BigInt(byteArrayOf(3)).toInt())
		assertEquals(127, BigInt(byteArrayOf(0x7f)).toInt())
		assertEquals(-128, BigInt(byteArrayOf(0x80.toByte())).toInt())
		assertEquals(-501, BigInt(byteArrayOf(-0x02, 0x0b)).toInt())
		assertEqualsBytes(
			0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			actual = fromBytesBE(0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00),
		)
		assertEqualsBytes(
			0x06, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0,
			actual = fromBytesBE(0x06, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0),
		)
		assertEqualsBytes(
			0xAD, 0xCE, 0xBD, 0xC7, 0x35, 0x89, 0x96, 0x66, 0x40, 0x9D, 0xF1, 0x96, 0x2E, 0x9E, 0x51,
			actual = fromBytesBE(
				0xFF,
				0xAD,
				0xCE,
				0xBD,
				0xC7,
				0x35,
				0x89,
				0x96,
				0x66,
				0x40,
				0x9D,
				0xF1,
				0x96,
				0x2E,
				0x9E,
				0x51
			),
		)
	}

	@Test
	fun toByteArrayBE() {
		assertTrue(BigInt.ZERO.toByteArrayBE().isEmpty())
		assertEqualsBytes(
			0x03,
			actual = BigInt.fromInt(3),
		)
		assertEqualsBytes(
			0x7f,
			actual = BigInt.fromInt(127),
		)
		assertEqualsBytes(
			0x80,
			actual = BigInt.fromInt(-128),
		)
		assertEqualsBytes(
			0xFE, 0x0B,
			actual = BigInt.fromInt(-501),
		)
		assertEqualsBytes(
			0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0,
			actual = BigInt.fromLong(5084243225297961184),
		)
		assertEqualsBytes(
			0x06, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0,
			actual = fromBytesBE(0x06, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0),
		)
		assertEqualsBytes(
			0xf6, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0,
			actual = fromBytesBE(0xf6, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0),
		)
		assertEqualsBytes(
			0x80, 0x00, 0x00, 0x00,
			actual = BigInt.fromInt(Int.MIN_VALUE),
		)
	}

	@Test
	fun fromString() {
		assertEquals(0L, BigInt.fromString("0").toLongOrNull())
		assertEquals(3L, BigInt.fromString("3").toLongOrNull())
		assertEquals(315121231211312L, BigInt.fromString("315121231211312").toLongOrNull())
		assertEquals(-315121231211312L, BigInt.fromString("-315121231211312").toLongOrNull())
		assertEquals(0x468EDC46A39240E0, BigInt.fromString("5084243225297961184").toLongOrNull())

		assertEqualsBytes(
			0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			actual = BigInt.fromString("9223372036854775808"),
		)
		assertEqualsBytes(
			0x06, 0xAC, 0x46, 0x8E, 0xDC, 0x46, 0xA3, 0x92, 0x40, 0xE0,
			actual = BigInt.fromString("31512123121121212121312"),
		)
		assertEqualsBytes(
			0xAD, 0xCE, 0xBD, 0xC7, 0x35, 0x89, 0x96, 0x66, 0x40, 0x9D, 0xF1, 0x96, 0x2E, 0x9E, 0x51,
			actual = BigInt.fromString("-426767427105083491625822010125410735"),
		)
		assertEqualsBytes(
			0x00, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
			actual = BigInt.fromString("9223372036854775809"),
		)
		assertEqualsBytes(
			0xFF, 0x7F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
			actual = BigInt.fromString("-9223372036854775809"),
		)
	}

	@Test
	fun toDouble() {
		assertEquals(
			-4.2676742710508346E35,
			BigInt.fromString("-426767427105083491625822010125410735").toDouble(),
			1e-2,
		)
	}

	@Test
	fun iLog2() {
		assertEquals(0, BigInt.fromInt(1).iLog2())
		assertEquals(1, BigInt.fromInt(2).iLog2())
		assertEquals(1, BigInt.fromInt(3).iLog2())
		assertEquals(2, BigInt.fromInt(4).iLog2())
		assertEquals(3, BigInt.fromInt(15).iLog2())
		assertEquals(4, BigInt.fromInt(16).iLog2())
		assertEquals(36, BigInt.fromLong(121211177434).iLog2())
		assertEquals(61, BigInt.fromLong(4587658253900688810).iLog2())
		assertEquals(62, BigInt.fromLong(4611686018427387904).iLog2())
		assertEquals(118, BigInt.fromString("426767427105083491625822010125410735").iLog2())
	}

	@Test
	fun bitwiseOps() {
		assertEquals(
			BigInt.fromString("-3333333333333333333124198895837873333334"),
			BigInt.fromString("3333333333333333333124198895837873333333").not(),
		)
	}

	@Test
	fun shift() {
		assertEquals(
			BigInt.fromString("22222222221494976512"),
			BigInt.fromString("44444444442989953024").shl(-1),
		)
	}

	@Test
	fun times() {
		assertCommutative(
			// "-6058852324646118603455209867090951496111"
			BigInt.fromIntArrayLE(intArrayOf(-1775329711, -1674763519, 1149758123, 835943657, -18)),
			BigInt.fromLong(-192227819960702969), BigInt.fromInt128(Int128(-6362500848411590425, 1708)), BigInt::times,
		)

		assertEquals(
			BigInt.fromString("-435993943892672664200353461405376235401663658494141675420261489"),
			BigInt.fromString("20880467999847912034355032910567") * BigInt.fromString("-20880467999847912034355032910567"),
		)
	}

	@Test
	fun sqr() {
		assertEquals(
			BigInt.fromString("435993943892672664200353461405376235401663658494141675420261489"),
			BigInt.fromString("20880467999847912034355032910567").sqr(),
		)

		assertEquals(
			BigInt.fromString("959808212397219019449497179599436663180350848510929442653299157947210000"),
			// overflows
			BigInt.fromLong(-989796959493929190).sqr().sqr(),
		)
	}

	@Test
	fun divRem() {
		assertDivRem(
			BigInt.fromString("283672093600361009334"), BigInt.fromString("10567964469817889926"),
			BigInt.fromString("3151912151115122325912382176292000000000")
				.divRem(BigInt.fromString("11111111111111111111")),
		)
		assertDivRem(
			BigInt.fromString("-283672093600361009334"), BigInt.fromString("-10567964469817889926"),
			BigInt.fromString("-3151912151115122325912382176292000000000")
				.divRem(BigInt.fromString("11111111111111111111")),
		)
		assertDivRem(
			BigInt.fromString("-283672093600361009334"), BigInt.fromString("10567964469817889926"),
			BigInt.fromString("3151912151115122325912382176292000000000")
				.divRem(BigInt.fromString("-11111111111111111111")),
		)
		assertDivRem(
			BigInt.fromString("283672093600361009334"), BigInt.fromString("-10567964469817889926"),
			BigInt.fromString("-3151912151115122325912382176292000000000")
				.divRem(BigInt.fromString("-11111111111111111111")),
		)
	}

	@Test
	fun pow() {
		assertEquals(BigInt.ONE, BigInt.ZERO.pow(0))
		assertEquals(BigInt.ONE, BigInt.ONE.pow(0))
		assertEquals(BigInt.ONE, (-BigInt.ONE).pow(0))
		assertEquals(BigInt.ONE, BigInt.ONE.pow(4))
		assertEquals(BigInt.ONE, BigInt.ONE.pow(5))
		assertEquals(BigInt.ONE, (-BigInt.ONE).pow(4))
		assertEquals(-BigInt.ONE, (-BigInt.ONE).pow(5))
		assertEquals(BigInt.ONE, BigInt.TWO.pow(0))
		assertEquals(BigInt.ONE, (-BigInt.TWO).pow(0))
		assertEquals(BigInt.fromInt(32), BigInt.TWO.pow(5))
		assertEquals(BigInt.fromInt(-32), (-BigInt.TWO).pow(5))
		assertEquals(BigInt.ONE, BigInt.ONE.pow(-1))
		assertEquals(-BigInt.ONE, (-BigInt.ONE).pow(-1))
		assertEquals(BigInt.fromInt(36), BigInt.fromInt(6).pow(2))
		assertEquals(BigInt.fromInt(100), BigInt.fromInt(10).pow(2))
		assertEquals(BigInt.fromInt(10000), BigInt.TEN.pow(4))
		assertEquals(BigInt.fromLong(1000000000000000L), BigInt.fromInt(10).pow(15))
		assertEquals(BigInt.fromString("29098125988731506183153025616435306561536"), BigInt.fromInt(6).pow(52))
		assertEquals(BigInt.fromString("1237940039285380274899124224"), BigInt.TWO.pow(90))
		assertEquals(BigInt.fromString("1000000000000000000000000000"), BigInt.TEN.pow(27))
		assertEquals(BigInt.fromString("-1000000000000000000000000000"), (-BigInt.TEN).pow(27))
		assertEquals(BigInt.fromString("10000000000000000000000000000"), (-BigInt.TEN).pow(28))
		assertEquals(BigInt.fromString("1000000000000000000000000"), BigInt.fromInt(1000).pow(8))
		assertEquals(BigInt.fromString("1000000000000000000000000"), BigInt.fromInt(-1000).pow(8))
		assertEquals(BigInt.fromString("1000000000000000000000000000"), BigInt.fromInt(1000).pow(9))
		assertEquals(BigInt.fromString("-1000000000000000000000000000"), BigInt.fromInt(-1000).pow(9))
		assertEquals(
			BigInt.fromString(
				"-8835519820219702192325245841484325726188491931084619347856117865549235897220330461043197333691346739314496399632209932043068859731241131739782437594544960207282104963080546601168821240331958825542375876890452464553279748055075987200844705825539303885161010454385355350086281081367009530281531268813328865910241199322696433856241302572820103372555122578298733556125353152900788849554898890242758089969169281552572473996904190708520682532963281573879876313518193106189240856066019783379809494511666064580442216039801756517326472886553875141106961530759568821683934966675428761648388451289156694315594285272484962055018005086910260984867180167972421512614948038897866411091848233900676233107245657126312742432974184953924005830647860017900063890401660170857775511883615020250958861981462416078718046365212336416152363034872548377224170231288700877476515911887225768579956371586799151098729789067103699347253370885585547868951494383012455706005845308091423848694003334476471685342141754250708117920455435376067520650314952343199521196188400347355030073172055557246182361635192576389791041235015105391204444970859073149213517444504897575325707829892243544793793564800566656675740765300079438504465270193535307697039404386753722406271412425237005440755889939428032253207156975277728355236686549993511140923938542933245819755779426507778532708187866089157346500669431086019360882178773315296950719180053024195607296639604603778667583912474040264735411854755646732279353508234155978789507014825455514062525870526376060622905733000562285705999167775381235974747848682280505341950393561842760166316995106507614769742172193914677480337175768562545944731546713239445252487"
			),
			BigInt.fromString("-20880467999847912034355032910567").pow(53),
		)
	}

	private fun <T> assertDivRem(expectedDiv: BigInt, expectedRem: T, actualDivRem: Pair<BigInt, T>) {
		assertEquals(expectedDiv, actualDivRem.first, "Div not equal")
		assertEquals(expectedRem, actualDivRem.second, "Rem not equal")
	}

	@Test
	fun testToString() {
		assertEquals("0", BigInt.ZERO.toString())
		assertEquals("315121231211312", BigInt.fromString("315121231211312").toString(10))
		assertEquals("-315121231211312", BigInt.fromString("-315121231211312").toString(10))
		assertEquals("31519123121121212121319", BigInt.fromString("31519123121121212121319").toString(10))
		assertEquals("-31519123121121212121319", BigInt.fromString("-31519123121121212121319").toString(10))
		assertEquals(
			"426767427105083491625822010125410735",
			BigInt.fromString("426767427105083491625822010125410735").toString(10)
		)
		assertEquals(
			"55844105986793442773355413541572575232",
			BigInt.fromString("55844105986793442773355413541572575232").toString(10)
		)
		assertEquals("6aca7b3db30374e40e7", BigInt.fromString("31519123121121212121319").toString(16).lowercase())
		assertEquals("-6aca7b3db30374e40e7", BigInt.fromString("-31519123121121212121319").toString(16).lowercase())
		assertEquals(
			"-426767427105083491625822010125410735",
			BigInt.fromString("-426767427105083491625822010125410735").toString(10)
		)
		assertEquals(
			"1000000000000000000000000000",
			BigInt.fromString("1000000000000000000000000000").toString(),
		)
	}

	@Test
	fun fromIntArrayLE() {
		assertEquals(BigInt.ZERO, BigInt.fromIntArrayLE(intArrayOf()))
		assertEquals(BigInt.ZERO, BigInt.fromIntArrayLE(intArrayOf(0)))
		assertEquals(BigInt.ZERO, BigInt.fromIntArrayLE(intArrayOf(0, 0)))
		assertEquals(1, BigInt.fromIntArrayLE(intArrayOf(1)).toInt())
		assertEquals(-1, BigInt.fromIntArrayLE(intArrayOf(-1)).toInt())
		assertEquals(-1L, BigInt.fromIntArrayLE(intArrayOf(-1)).toLong())
		assertEquals(Int.MIN_VALUE.toLong(), BigInt.fromIntArrayLE(intArrayOf(Int.MIN_VALUE)).toLong())
		assertEquals(4294967295L, BigInt.fromIntArrayLE(intArrayOf(-1, 0)).toLong())
		assertEquals(4294967296L, BigInt.fromIntArrayLE(intArrayOf(0, 1)).toLong())
		assertEquals(-4294967296L, BigInt.fromIntArrayLE(intArrayOf(0, -1)).toLong())
	}

	private fun assertEqualsBytes(
		vararg expected: Int,
		actual: BigInt,
	) {
		assertEquals(expected.map { it.toByte() }, actual.toByteArrayBE().toList())
		assertEquals(expected.map { it.toByte() }.asReversed(), actual.toByteArrayLE().toList())
	}

	private fun fromBytesBE(vararg bytes: Int): BigInt {
		return BigInt(bytes.map { it.toByte() }.toByteArray())
	}
}