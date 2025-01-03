package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ints.toInt128
import kotlin.test.Test
import kotlin.test.assertEquals

interface IntegralTestBase<T: Integral<T>>: NumTestBase<T> {
	override val scope: Integral.Scope<T>
	val maxBytes: Int

	@Test
	fun fromIntToInt() {
		with(scope) {
			assertEquals(0, fromInt(0).toInt())
			assertEquals(0, fromInt(0).toIntOrNull())
			assertEquals(3, fromInt(3).toInt())
			assertEquals(3, fromInt(3).toIntOrNull())
			assertEquals(-501, fromInt(-501).toInt())
			assertEquals(-501, fromInt(-501).toIntOrNull())
			assertEquals(-Int.MAX_VALUE, fromInt(-Int.MAX_VALUE).toInt())
			assertEquals(Int.MAX_VALUE, fromInt(Int.MAX_VALUE).toInt())
			assertEquals(Int.MIN_VALUE, fromInt(Int.MIN_VALUE).toInt())
		}
	}

	@Test
	fun toDouble64() {
		with(scope) {
			if (maxBytes < 8) return
			assertEquals(2147483648.0, fromLong(Int.MAX_VALUE.toLong() + 1).toDouble(), 1e-4)
			assertEquals(-2147483649.0, fromLong(Int.MIN_VALUE.toLong() - 1).toDouble(), 1e-4)
			assertEquals(-9.223372036854776E18, fromLong(-9223372036854775806).toDouble(), 1e-2)
			assertEquals(9.223372036854776E18, fromLong(9223372036854775806).toDouble(), 1e-2)
			assertEquals(9.223372036854776E18, fromLong(Long.MAX_VALUE).toDouble(), 1e-2)
			assertEquals(-9.223372036854776E18, fromLong(Long.MIN_VALUE).toDouble(), 1e-2)
		}
	}

	@Test
	fun toDouble128() {
		with(scope) {
			if (maxBytes < 16) return
			assertEquals(9.223372036854776E18, fromInt128(Long.MAX_VALUE.toInt128() + 1).toDouble(), 1e-2)
			assertEquals(-9.223372036854776E18, fromInt128(Long.MIN_VALUE.toInt128() - 1).toDouble(), 1e-2)

			assertEquals(-1.8446744073709552E19, fromInt128(Int128(0, -1)).toDouble(), 1e-2)
			assertEquals(1.8446744073709552E19, fromInt128(Int128(0, 1)).toDouble(), 1e-2)
			assertEquals(-1.8446744073709552E19, fromInt128(Int128(1, -1)).toDouble(), 1e-2)
			assertEquals(1.8446744073709552E19, fromInt128(Int128(1, 1)).toDouble(), 1e-2)
			assertEquals(1.8446744073709552E19, fromInt128(Int128(-1, 0)).toDouble(), 1e-2)

			assertEquals(1.8446744073704552E19, Int128(-5000000, 0).toDouble(), 1e-2)
			assertEquals(-1.8446744073704552E19, Int128(5000000, -1).toDouble(), 1e-2)

			assertEquals(3.6893488147419103E19, fromInt128(Int128(-1, 1)).toDouble(), 1e-2)
			assertEquals(-3.6893488147419103E19, fromInt128(Int128(0, -2)).toDouble(), 1e-2)
			assertEquals(3.6893488147419103E19, fromInt128(Int128(0, 2)).toDouble(), 1e-2)

			assertEquals(3.68934881474141E19, fromInt128(Int128(-5000000, 1)).toDouble(), 1e-2)
			assertEquals(-1.8446744073714553E19, fromInt128(Int128(-5000000, -2)).toDouble(), 1e-2)
			assertEquals(3.68934881474241E19, fromInt128(Int128(5000000, 2)).toDouble(), 1e-2)

			assertEquals(9.223372036854776E21, fromInt128(Int128(0, 500)).toDouble(), 1e-2)
			assertEquals(-9.223372036854776E21, fromInt128(Int128(0, -500)).toDouble(), 1e-2)

			assertEquals(2.3042012489911338E24, fromInt128(Int128(-1248, 124910)).toDouble(), 1e-2)

			assertEquals(
				-4.2676742710508346E35,
				fromInt128(Int128(7368062651311269678, -23135108580669034)).toDouble(),
				1e-2,
			)

			assertEquals(-1.7014118346046923E38, fromInt128(Int128.MIN_VALUE).toDouble(), 1e-2)
			assertEquals(1.7014118346046923E38, fromInt128(Int128.MAX_VALUE).toDouble(), 1e-2)
		}
	}

	@Test
	fun unaryMinus64() {
		with(scope) {
			if (maxBytes < 8) return
			assertEquals(fromLong(-(Int.MIN_VALUE.toLong())), (-fromInt(Int.MIN_VALUE)))
			assertEquals(-(Int.MIN_VALUE.toLong()), (-fromInt(Int.MIN_VALUE)).toLongOrNull())
			assertEquals(
				-Int.MAX_VALUE.toLong() - Int.MAX_VALUE / 2,
				(-fromLong(Int.MAX_VALUE.toLong() + Int.MAX_VALUE / 2)).toLongOrNull()
			)

			assertEquals(
				Int.MAX_VALUE.toLong() + Int.MAX_VALUE / 2,
				(-fromLong(-Int.MAX_VALUE.toLong() - Int.MAX_VALUE / 2)).toLongOrNull()
			)
			assertEquals(
				-(Int.MIN_VALUE.toLong()) - Int.MIN_VALUE / 2,
				(-fromLong(Int.MIN_VALUE.toLong() + Int.MIN_VALUE / 2)).toLongOrNull()
			)

			assertEquals(fromLong(-151212113421881L), -fromLong(151212113421881))
			assertEquals(fromLong(151212113421881L), -fromLong(-151212113421881))

			assertEquals(fromLong(-4503599627370496000), -fromLong(4503599627370496000))
			assertEquals(fromLong(4503599627370496000), -fromLong(-4503599627370496000))
		}
	}

	@Test
	fun unaryMinus128() {
		with(scope) {
			if (maxBytes < 16) return
			assertEquals(fromLong(Long.MIN_VALUE), -fromInt128(Long.MAX_VALUE.toInt128() + 1))
			assertEquals(fromInt128(Long.MAX_VALUE.toInt128() + 1), -fromLong(Long.MIN_VALUE))
			assertEquals(fromInt128(Long.MIN_VALUE.toInt128() - 1), -fromInt128(Long.MAX_VALUE.toInt128() + 2))
			assertEquals(fromInt128(Long.MAX_VALUE.toInt128() + 2), -fromInt128(Long.MIN_VALUE.toInt128() - 1))
			assertEquals(fromInt128(Long.MAX_VALUE.toInt128() + 2), -fromInt128(Long.MIN_VALUE.toInt128() - 1))
			assertEquals(fromInt128(Int128(-4658231916516108151, 0)), -fromInt128(Int128(4658231916516108151, -1)))
			assertEquals(fromInt128(Int128(4658231916516108151, -1)), -fromInt128(Int128(-4658231916516108151, 0)))

			assertEquals(fromInt128(Int128(-5392811766072338400, 0)), -fromInt128(Int128(5392811766072338400, -1)))
			assertEquals(fromInt128(Int128(1, -1)), -fromInt128(Int128(-1, 0)))
			assertEquals(fromInt128(Int128(-1, 0)), -fromInt128(Int128(1, -1)))
			assertEquals(fromInt128(Int128(0, -1)), -fromInt128(Int128(0, 1)))
			assertEquals(fromInt128(Int128(0, 1)), -fromInt128(Int128(0, -1)))
			assertEquals(fromInt128(Int128(-1, -2)), -fromInt128(Int128(1, 1)))
			assertEquals(fromInt128(Int128(-1, -2)), -fromInt128(Int128(1, 1)))

			assertEquals(fromInt128(Int128(Long.MIN_VALUE, -63)), -fromInt128(Int128(Long.MIN_VALUE, 62)))
			assertEquals(fromInt128(Int128(1, Long.MIN_VALUE)), -fromInt128(Int128.MAX_VALUE))
		}
	}

	@Test
	fun plus64() {
		if (maxBytes < 8) return
		with(scope) {

			assertCommutative(
				fromLong(-135425168673),
				fromLong(-121211177434), fromLong(-14213991239), Num<T>::plus,
			)
			assertCommutative(
				fromLong(-1421391239),
				fromLong(-121211177434), fromLong(119789786195), Num<T>::plus,
			)
			assertCommutative(
				fromLong(1421391239),
				fromLong(121211177434), fromLong(-119789786195), Num<T>::plus,
			)
			assertCommutative(
				fromLong(-135425168673),
				fromLong(-121211177434), fromLong(-14213991239), Num<T>::plus,
			)

			assertCommutative(
				fromLong(-1044846154747028498),
				fromLong(3458753472623467502), fromLong(-4503599627370496000), Num<T>::plus,
			)
			assertCommutative(
				fromLong(1206953658938219502),
				fromLong(3458753472623467502), fromLong(-2251799813685248000), Num<T>::plus,
			)

			assertCommutative(
				fromLong(4613195649392065845),
				fromLong(-4587658253900688810), fromLong(9200853903292754655), Num<T>::plus,
			)
			assertCommutative(
				fromLong(-4613195649392065845),
				fromLong(4587658253900688810), fromLong(-9200853903292754655), Num<T>::plus,
			)
		}
	}

	@Test
	fun plus128() {
		if (maxBytes < 16) return
		with(scope) {
			assertEquals(fromInt128(Int128(Long.MIN_VALUE, 0)), fromLong(Long.MAX_VALUE) + 1)

			assertCommutative(
				fromInt128(Int128(4658231916516108151, -1)),
				fromLong(-4587658253900688810), fromLong(-9200853903292754655), Num<T>::plus,
			)
			assertCommutative(
				fromInt128(Int128(-4658231916516108151, 0)),
				fromLong(4587658253900688810), fromLong(9200853903292754655), Num<T>::plus,
			)

			assertCommutative(
				fromInt128(Int128(72252365533359636, -8251)),
				fromInt128(Int128(1124121238124, -123)), fromInt128(Int128(72251241412121512, -8128)),
				Num<T>::plus,
			)

			assertCommutative(
				fromInt128(Int128(1253275, 8005)),
				fromInt128(Int128(1238124, -123)), fromInt128(Int128(15151, 8128)), Num<T>::plus,
			)
			assertCommutative(
				fromInt128(Int128(-5520981506884589120, -123)),
				fromInt128(Int128(4412881283412481248, -123)), fromLong(8512881283412481248), Num<T>::plus,
			)
			assertCommutative(
				fromInt128(Int128(-4100000000000000000, -124)),
				fromInt128(Int128(4412881283412481248, -123)), fromLong(-8512881283412481248), Num<T>::plus,
			)
			assertCommutative(
				fromInt128(Int128(-4100000000000000000, -123)),
				fromInt128(Int128(4412881283412481248, -123)), fromInt128(Int128(-8512881283412481248, 0)),
				Num<T>::plus,
			)
			assertCommutative(
				fromInt128(Int128(72252365533359636, -8251)),
				fromInt128(Int128(1124121238124, -123)), fromInt128(Int128(72251241412121512, -8128)),
				Num<T>::plus,
			)

			assertCommutative(
				fromLong(2500583218253545703),
				fromInt128(Int128(-6938961600715013913, 0)), fromInt128(Int128(-9007199254740992000, -1)),
				Num<T>::plus,
			)
		}
	}

	@Test
	fun minus64() {
		if (maxBytes < 8) return
		with(scope) {
			assertEquals(106997186195, (fromLong(121211177434) - fromLong(14213991239)).toLong())
			assertEquals(-106997186195, (fromLong(-121211177434) - fromLong(-14213991239)).toLong())
			assertEquals(1421391239, (fromLong(121211177434) - fromLong(119789786195)).toLong())
			assertEquals(-241000963629, (fromLong(-121211177434) - fromLong(119789786195)).toLong())
			assertEquals(1421391239, (fromLong(121211177434) - fromLong(119789786195)).toLong())
			assertEquals(241000963629, (fromLong(121211177434) - fromLong(-119789786195)).toLong())

			assertEquals(
				fromLong(4613195649392065845),
				fromLong(-4587658253900688810) - fromLong(-9200853903292754655),
			)
			assertEquals(
				fromLong(-4613195649392065845),
				fromLong(4587658253900688810) - fromLong(9200853903292754655),
			)
		}
	}

	@Test
	fun compare64() {
		if (maxBytes < 8) return
		with(scope) {
			assertEquals(-1, fromLong(Long.MIN_VALUE).compareTo(fromLong(-1)))
			assertEquals(1, fromLong(-1).compareTo(fromLong(Long.MIN_VALUE)))

			assertEquals(-1, fromLong(Long.MIN_VALUE).compareTo(fromLong(Long.MAX_VALUE)))
			assertEquals(1, fromLong(Long.MAX_VALUE).compareTo(fromLong(Long.MIN_VALUE)))
			assertEquals(1, fromLong(-Long.MAX_VALUE).compareTo(fromLong(Long.MIN_VALUE)))
			assertEquals(-1, fromLong(Long.MIN_VALUE).compareTo(fromLong(-Long.MAX_VALUE)))

			assertEquals(1, (fromLong(121211177434).compareTo(fromLong(14213991239))))
			assertEquals(-1, fromLong(-121211177434).compareTo(fromLong(-14213991239)))
			assertEquals(1, (fromLong(121211177434).compareTo(fromLong(119789786195))))
			assertEquals(-1, (fromLong(-121211177434).compareTo(fromLong(119789786195))))

			assertEquals(1, fromLong(-4587658253900688810).compareTo(fromLong(-9200853903292754655)))
			assertEquals(1, fromLong(4587658253900688810).compareTo(fromLong(-9200853903292754655)))
			assertEquals(-1, fromLong(4587658253900688810).compareTo(fromLong(9200853903292754655)))
			assertEquals(-1, fromLong(-4587658253900688810).compareTo(fromLong(9200853903292754655)))
		}
	}

	@Test
	fun minus128() {
		if (maxBytes < 16) return
		with(scope) {
			assertEquals(fromInt128(Int128(Long.MAX_VALUE, -1)), fromLong(Long.MIN_VALUE) - 1)

			assertEquals(
				fromLong(-6074270472259878681),
				fromInt128(Int128(8049017959173996775, 9)) - fromInt128(Int128(-4323455642275676160, 9)),
			)

			assertEquals(
				fromInt128(Int128(4658231916516108151, -1)),
				fromLong(-4587658253900688810) - fromLong(9200853903292754655),
			)
			assertEquals(
				fromInt128(Int128(-4658231916516108151, 0)),
				fromLong(4587658253900688810) - fromLong(-9200853903292754655),
			)

			assertEquals(
				fromInt128(Int128(2500583218253545703, 0)),
				fromInt128(Int128(-6938961600715013913, 0)) - fromInt128(Int128(9007199254740992000, 0)),
			)
			assertEquals(
				fromInt128(Int128(2860871188443185383, 21)),
				fromInt128(Int128(-6362500848411590425, 83)) - fromInt128(Int128(Long.MIN_VALUE, 62)),
			)
		}
	}

	@Test
	fun compare128() {
		if (maxBytes < 16) return
		with(scope) {
			assertEquals(
				-1,
				fromInt128(Int128(8049017959173996775, 9)).compareTo(fromInt128(Int128(-4323455642275676160, 9))),
			)

			assertEquals(
				1,
				fromInt128(Int128(-6938961600715013913, 0)).compareTo(fromInt128(Int128(9007199254740992000, 0))),
			)
			assertEquals(
				1,
				fromInt128(Int128(-6362500848411590425, 83)).compareTo(fromInt128(Int128(Long.MIN_VALUE, 62))),
			)
		}
	}

	@Test
	fun times64() {
		if (maxBytes < 8) return
		with(scope) {
			assertEquals(fromLong(192227819960702969), fromLong(149129418123121) * 1289)
			assertEquals(fromLong(-192227819960702969), fromLong(-149129418123121) * 1289)
			assertEquals(fromLong(-192227819960702969), fromLong(149129418123121) * -1289)
			assertEquals(fromLong(192227819960702969), fromLong(-149129418123121) * -1289)

			assertCommutative(ZERO, fromLong(-4587658253900688810), ZERO, Num<T>::times)
			assertCommutative(ZERO, fromLong(4587658253900688810), ZERO, Num<T>::times)
			assertCommutative(ZERO, ZERO, fromLong(-4587658253900688810), Num<T>::times)
			assertCommutative(ZERO, ZERO, fromLong(4587658253900688810), Num<T>::times)

			assertCommutative(
				fromLong(192227819960702969),
				fromLong(149129418123121), fromInt(1289), Num<T>::times,
			)
			assertCommutative(
				fromLong(-192227819960702969),
				fromLong(-149129418123121), fromInt(1289), Num<T>::times,
			)
			assertCommutative(
				fromLong(-192227819960702969),
				fromLong(149129418123121), fromInt(-1289), Num<T>::times,
			)
			assertCommutative(
				fromLong(192227819960702969),
				fromLong(-149129418123121), fromInt(-1289), Num<T>::times,
			)
		}
	}

	@Test
	fun timesInt128() {
		if (maxBytes < 16) return
		with(scope) {
			assertEquals(
				fromInt128(Int128(-5030641133549005828, -130070)),
				fromLong(-192227819960702969) * 12481828,
			)

			assertCommutative(
				fromInt128(Int128(2356463595119318997, 10)),
				fromLong(43506537013), fromLong(4294157089), Num<T>::times,
			)
			assertCommutative(
				fromInt128(Int128(-2356463595119318997, -11)),
				fromLong(-43506537013), fromLong(4294157089), Num<T>::times,
			)
			assertCommutative(
				fromInt128(Int128(-2356463595119318997, -11)),
				fromLong(43506537013), fromLong(-4294157089), Num<T>::times,
			)
			assertCommutative(
				fromInt128(Int128(2356463595119318997, 10)),
				fromLong(-43506537013), fromLong(-4294157089), Num<T>::times,
			)

			assertEquals(
				fromInt128(Int128(-5030641133549005828, -130070)),
				fromLong(-192227819960702969) * 12481828
			)
			assertCommutative(
				fromInt128(Int128(-5030641133549005828, -130070)),
				fromLong(-192227819960702969), fromInt(12481828), Num<T>::times,
			)
			assertCommutative(
				fromInt128(Int128(7368062651310448209, -23135108580669034)),
				fromLong(-192227819960702969), fromLong(2220112714134339815), Num<T>::times,
			)

			assertCommutative(
				fromInt128(Int128(8449642029512639632, -9137898213008747)),
				fromLong(4569515072723572), fromInt128(Int128(4569515072723572, -2)), Num<T>::times,
			)

			assertCommutative(
				fromInt128(Int128(4793518853382471680, -7302806073299443607)),
				fromInt128(Int128(16926659444736, 0)), fromInt128(Int128(-2153214848064815104, -431439)),
				Num<T>::times
			)
			assertCommutative(
				fromInt128(Int128(4793518853382471680, 7302835975055466601)),
				fromInt128(Int128(16926659444736, 0)), fromInt128(Int128(-2153214848064815104, 431439)), Num<T>::times,
			)
		}
	}

	@Test
	fun sqr128() {
		with(scope) {
			assertEquals(
				fromInt128(Int128(2666859315176369828, 53109536138667442)),
				fromLong(-989796959493929190).sqr(),
			)
			assertEquals(fromInt128(Int128(0, 1)), fromInt(2).sqr().sqr().sqr().sqr().sqr().sqr())
			assertEquals(
				fromInt128(Int128(8449642029512639632, 1131932438397)),
				fromLong(4569515072723572).sqr(),
			)
		}
	}


	@Test
	fun divRem32() {
		with(scope) {
			assertDivRem(ZERO, 0, ZERO.divRem(3))
			assertDivRem(ZERO, ZERO, ZERO.divRem(fromInt(3)))
			assertDivRem(ZERO, 1, ONE.divRem(10))
			assertDivRem(fromInt(51), 32, fromInt(5132).divRem(100))
			assertDivRem(fromInt(-51), -32, fromInt(-5132).divRem(100))
			assertDivRem(fromInt(51), fromInt(32), fromInt(5132).divRem(fromInt(100)))
			assertDivRem(fromInt(-51), fromInt(-32), fromInt(-5132).divRem(fromInt(100)))
			assertDivRem(fromInt(-805303), -798, fromInt(-805303798).divRem(1000))
		}
	}

	@Test
	fun divRem64() {
		if (maxBytes < 8) return
		with(scope) {
			assertDivRem(ZERO, ZERO, ZERO.divRem(fromLong(4587658253)))
			assertDivRem(ZERO, ZERO, ZERO.divRem(fromLong(-4587658253)))
			assertDivRem(ZERO, fromInt(5132), fromInt(5132).divRem(fromLong(4587658253)))
			assertDivRem(ZERO, fromInt(5132), fromInt(5132).divRem(fromLong(-4587658253)))

			assertDivRem(fromLong(3458753472623467), 502, fromLong(3458753472623467502).divRem(1000))
			assertDivRem(fromLong(-3458753472623467), -502, fromLong(-3458753472623467502).divRem(1000))

			assertDivRem(
				fromLong(3458753472623467), 502,
				fromLong(3458753472623467502).divRem(1000),
			)
			assertDivRem(
				fromLong(-3458753472623467), -502,
				fromLong(-3458753472623467502).divRem(1000),
			)
			assertDivRem(
				fromLong(20720750008), -17708,
				fromLong(-52124124124142124).divRem(-2515552),
			)
			assertDivRem(
				fromLong(3458753472623467), fromInt(502),
				fromLong(3458753472623467502).divRem(fromInt(1000)),
			)
			assertDivRem(
				fromLong(-3458753472623467), fromInt(-502),
				fromLong(-3458753472623467502).divRem(fromInt(1000)),
			)
			assertDivRem(
				fromLong(20720750008), fromInt(-17708),
				fromLong(-52124124124142124).divRem(fromInt(-2515552)),
			)
		}
	}

	@Test
	fun divRem128() {
		with(scope) {
			assertDivRem(
				fromLong(977840021608426723), 9,
				fromInt128(Int128(-8668343857625284377, 0)).divRem(10),
			)
			assertDivRem(
				fromInt128(Int128(-5374365026297891111, 1)), 319,
				fromInt128(Int128(-6362500848411590425, 1708)).divRem(1000),
			)
			assertDivRem(
				fromInt128(Int128(5374365026297891111, -2)), -319,
				fromInt128(Int128(6362500848411590425, -1709)).divRem(1000),
			)
			assertDivRem(
				fromInt128(Int128.fromString("31519123121121212121")), 319,
				fromInt128(Int128.fromString("31519123121121212121319")).divRem(1000),
			)
			assertDivRem(
				fromInt128(Int128.fromString("-31519123121121212121")), -319,
				fromInt128(Int128(6362500848411590425, -1709)).divRem(1000),
			)
			assertDivRem(
				fromLong(3151912312112121212), 1319,
				fromInt128(Int128.fromString("31519123121121212121319")).divRem(10000),
			)
			assertDivRem(
				fromLong(-3151912312112121212), -1319,
				fromInt128(Int128.fromString("-31519123121121212121319")).divRem(10000),
			)
			assertDivRem(
				fromLong(1000_000_000_000_000_000L), 0,
				fromInt128(Int128.fromString("1000000000000000000000000000")).divRem(1000_000_000)
			)

			assertDivRem(
				fromLong(2836720936003610093), fromLong(3883457422475956677),
				fromInt128(Int128(7085921464013761024, 1708655000861237586))
					.divRem(fromInt128(Int128(-7335632962598440505, 0))),
			)
			assertDivRem(
				fromLong(-2836720936003610093), fromLong(-3883457422475956677),
				fromInt128(
					Int128(
						-7085921464013761024,
						-1708655000861237587
					)
				).divRem(fromInt128(Int128(-7335632962598440505, 0))),
			)
			assertDivRem(
				fromLong(9), fromInt128(Int128(-4025189647097350087, 170865500086127074)),
				fromInt128(Int128(4407871307897844281, 1708655000861275352))
					.divRem(fromInt128(Int128(7085921464013761024, 170865500086127586))),
			)
			assertDivRem(
				fromInt128(Int128(1792840124972618132, 11544966222035389)), fromInt(-112),
				fromInt128(Int128(-7085921464013761024, -1708655000861237587)).divRem(fromInt(-148)),
			)
			assertDivRem(
				fromInt128(Int128(-8475531060893577770, 62320081330099836)), fromInt(-72),
				fromInt128(Int128.MIN_VALUE).divRem(fromInt(-148)),
			)
			assertDivRem(
				fromInt128(Int128(8475531060893577770, -62320081330099837)), fromInt(-72),
				fromInt128(Int128.MIN_VALUE).divRem(fromInt(148)),
			)
			assertDivRem(
				fromInt128(Int128(8475531060893577770, -62320081330099837)), fromInt(71),
				fromInt128(Int128.MAX_VALUE).divRem(fromInt(-148)),
			)
			assertDivRem(
				fromInt128(Int128(-8475531060893577770, 62320081330099836)), fromInt(71),
				fromInt128(Int128.MAX_VALUE).divRem(fromInt(148)),
			)
			assertDivRem(
				fromInt128(Int128(-8475531060893577770, 62320081330099836)), -72,
				fromInt128(Int128.MIN_VALUE).divRem(-148),
			)
			assertDivRem(
				fromInt128(Int128(8475531060893577770, -62320081330099837)), -72,
				fromInt128(Int128.MIN_VALUE).divRem(148),
			)
			assertDivRem(
				fromInt128(Int128(8475531060893577770, -62320081330099837)), 71,
				fromInt128(Int128.MAX_VALUE).divRem(-148),
			)
			assertDivRem(
				fromInt128(Int128(-8475531060893577770, 62320081330099836)), 71,
				fromInt128(Int128.MAX_VALUE).divRem(148),
			)
			assertDivRem(
				fromInt(-1), fromInt(-1),
				fromInt128(Int128.MIN_VALUE).divRem(fromInt128(Int128.MAX_VALUE)),
			)
			assertDivRem(
				ZERO, fromInt128(Int128.MAX_VALUE),
				fromInt128(Int128.MAX_VALUE).divRem(fromInt128(Int128.MIN_VALUE)),
			)
			assertDivRem(
				fromInt(2), ZERO,
				fromInt128(Int128.MIN_VALUE).divRem(fromInt128(Int128.MIN_VALUE.shr(1))),
			)
			assertDivRem(
				fromInt(-2), ZERO,
				fromInt128(Int128.MIN_VALUE).divRem(fromInt128(Int128.MIN_VALUE.ushr(1))),
			)
			assertDivRem(
				fromInt128(Int128(2, 1)), fromInt(1),
				fromInt128(Int128.MAX_VALUE).divRem(fromLong(Long.MAX_VALUE)),
			)
			assertDivRem(
				fromInt128(Int128(1, -1)), fromLong(Long.MAX_VALUE),
				fromInt128(Int128.MAX_VALUE).divRem(fromLong(Long.MIN_VALUE)),
			)
			assertDivRem(
				fromInt128(Int128(-2, -2)), fromInt(-2),
				fromInt128(Int128.MIN_VALUE).divRem(fromLong(Long.MAX_VALUE)),
			)
			assertDivRem(
				fromInt128(Int128(0, 1)), ZERO,
				fromInt128(Int128.MIN_VALUE).divRem(fromLong(Long.MIN_VALUE)),
			)
		}
	}
}
