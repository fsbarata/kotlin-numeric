package com.github.fsbarata.numeric.ints

import com.github.fsbarata.numeric.LongNumScope
import org.junit.Test
import java.math.BigInteger
import kotlin.system.measureNanoTime
import kotlin.test.Ignore

@Ignore
class BigIntPerformanceTest {
	@Suppress("NOTHING_TO_INLINE")
	private inline fun BigInteger(i: Int) = BigInteger.valueOf(i.toLong())

	@Suppress("NOTHING_TO_INLINE")
	private inline fun BigInteger(l: Long) = BigInteger.valueOf(l)

	@Test
	fun construct() {
		listOf(
			benchmark(
				"Int",
				reference = { BigInteger(it) },
				benched = { BigInt.fromInt(it) },
				target = { it.toLong() },
				times = 1000_000_000,
			),
			benchmark(
				"Long",
				reference = { BigInteger.valueOf(15125124125121233L) },
				benched = { BigInt.fromLong(15125124125121233) },
				target = {},
				times = 1000_000_000,
			),
			benchmark(
				"String positive",
				reference = { BigInteger("3") },
				benched = { BigInt.fromString("3") },
				target = { "3".toLong() },
				times = 100_000_000,
			),
			benchmark(
				"String negative",
				reference = { BigInteger("-2") },
				benched = { BigInt.fromString("-2") },
				target = { "-2".toLong() },
				times = 100_000_000,
			),
			benchmark(
				"String positive",
				reference = { BigInteger("15125124125121233") },
				benched = { BigInt.fromString("15125124125121233") },
				target = { "3".toLong() },
				times = 100_000_000,
			),
			benchmark(
				"String negative",
				reference = { BigInteger("-15125124125121233") },
				benched = { BigInt.fromString("-15125124125121233") },
				target = { "-2".toLong() },
				times = 100_000_000,
			)
		).forEach { assert(it) }
	}

	@Test
	fun plusMinus() {
		listOf(
			benchmark(
				"3 + 5",
				referenceItemFactory = { Pair(BigInteger(3), BigInteger(5)) },
				reference = { (a, b), _ -> a.add(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(3), BigInt.fromInt(5)) },
				benched = { (a, b), _ -> a + b },
				target = { 3L + 5L },
				times = 100_000_000,
			),
			benchmark(
				"3 + -5",
				referenceItemFactory = { Pair(BigInteger(3), BigInteger(-5L)) },
				reference = { (a, b), _ -> a.add(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(3), BigInt.fromLong(-5L)) },
				benched = { (a, b), _ -> a + b },
				target = { 3L + (-5L) },
				times = 100_000_000,
			),
			benchmark(
				"3 + -25",
				referenceItemFactory = { Pair(BigInteger(3), BigInteger(-25L)) },
				reference = { (a, b), _ -> a.add(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(3), BigInt.fromLong(-25L)) },
				benched = { (a, b), _ -> a + b },
				target = { 3L + (-25) },
				times = 100_000_000,
			),
			benchmark(
				"3 - 25",
				referenceItemFactory = { Pair(BigInteger(3), BigInteger(25)) },
				reference = { (a, b), _ -> a.subtract(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(3), BigInt.fromInt(25)) },
				benched = { (a, b), _ -> a - b },
				target = { 3L - 25 },
				times = 100_000_000,
			),
			benchmark(
				"315125125152121 - 223232225",
				referenceItemFactory = { Pair(BigInteger(315125125152121), BigInteger(223232225)) },
				reference = { (a, b), _ -> a.subtract(b) },
				benchedItemFactory = { Pair(BigInt.fromLong(315125125152121), BigInt.fromInt(223232225)) },
				benched = { (a, b), _ -> a - b },
				target = { 3L - 25 },
				times = 100_000_000,
			),
			benchmark(
				"55511232131237123787129132 - 55511622168923129132",
				referenceItemFactory = {
					Pair(
						BigInteger("55511232131237123787129132"),
						BigInteger("55511622168923129132")
					)
				},
				reference = { (a, b), _ -> a.add(b) },
				benchedItemFactory = {
					Pair(
						BigInt.fromString("55511232131237123787129132"),
						BigInt.fromString("55511622168923129132")
					)
				},
				benched = { (a, b), _ -> a + b },
				target = { },
				times = 100_000_000,
			),
		).forEach { assert(it) }
	}

	@Test
	fun multiply() {
		listOf(
			benchmark(
				"52 * 143",
				referenceItemFactory = { Pair(BigInteger(52), BigInteger(143)) },
				reference = { (a, b), _ -> a.multiply(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(52), BigInt.fromInt(143)) },
				benched = { (a, b), _ -> a * b },
				target = { 52L * 143 },
				times = 100_000_000,
			),
			benchmark(
				"52 * -25",
				referenceItemFactory = { Pair(BigInteger(52), BigInteger(-25)) },
				reference = { (a, b), _ -> a.multiply(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(52), BigInt.fromInt(-25)) },
				benched = { (a, b), _ -> a * b },
				target = { 52L * -25 },
				times = 100_000_000,
			),
			benchmark(
				"-52155155 * -2515552",
				referenceItemFactory = { Pair(BigInteger(-52155155), BigInteger(-2515552)) },
				reference = { (a, b), _ -> a.multiply(b) },
				benchedItemFactory = { Pair(BigInt.fromLong(-52155155), BigInt.fromLong(-2515552)) },
				benched = { (a, b), _ -> a * b },
				target = { (-52155155L) * -2515552L },
				times = 100_000_000,
			),
			benchmark(
				"55511232131237123787129132 * 55511622168923129132",
				referenceItemFactory = {
					Pair(
						BigInteger("55511232131237123787129132"),
						BigInteger("55511622168923129132")
					)
				},
				reference = { (a, b), _ -> a.multiply(b) },
				benchedItemFactory = {
					Pair(
						BigInt.fromString("55511232131237123787129132"),
						BigInt.fromString("55511622168923129132")
					)
				},
				benched = { (a, b), _ -> a * b },
				target = { },
				times = 10_000_000,
			),
		).forEach { assert(it) }
	}

	@Test
	fun divide() {
		listOf(
			benchmark(
				"52821 / 208",
				referenceItemFactory = { Pair(BigInteger(52821), BigInteger.valueOf(208)) },
				reference = { (a, b), _ -> a.divide(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(52821), BigInt.fromInt(208)) },
				benched = { (a, b), _ -> a / b },
				target = { 52821L / 208 },
				times = 5_000_000,
			),
			benchmark(
				"52 / -25",
				referenceItemFactory = { Pair(BigInteger(52), BigInteger(-25L)) },
				reference = { (a, b), _ -> a.divide(b) },
				benchedItemFactory = { Pair(BigInt.fromInt(52), BigInt.fromLong(-25L)) },
				benched = { (a, b), _ -> a / b },
				target = { 52L / -25 },
				times = 5_000_000,
			),
			benchmark(
				"-52124124124142124 / -2515552",
				referenceItemFactory = { Pair(BigInteger(-52124124124142124L), BigInteger(-2515552L)) },
				reference = { (a, b), _ -> a.divide(b) },
				benchedItemFactory = { Pair(BigInt.fromLong(-52124124124142124L), BigInt.fromLong(-2515552L)) },
				benched = { (a, b), _ -> a / b },
				target = { (-52124124124142124L) / -2515552 },
				times = 10_000_000,
			),
			benchmark(
				"55511232131237123787129132 /% 55511622168923129132",
				referenceItemFactory = {
					Pair(
						BigInteger("55511232131237123787129132"),
						BigInteger("55511622168923129132")
					)
				},
				reference = { (a, b), _ -> a.divideAndRemainder(b) },
				benchedItemFactory = {
					Pair(
						BigInt.fromString("55511232131237123787129132"),
						BigInt.fromString("55511622168923129132")
					)
				},
				benched = { (a, b), _ -> a.divRem(b) },
				target = { },
				times = 10_000_000,
			),
		).forEach { assert(it) }
	}

	@Test
	fun pow() {
		listOf(
			benchmark(
				"52 ^ 20",
				referenceItemFactory = { BigInteger(52) },
				reference = { a, _ -> a.pow(20) },
				benchedItemFactory = { BigInt.fromInt(52) },
				benched = { a, _ -> a.pow(20) },
				target = { LongNumScope.pow(52L, 20) },
				times = 10_000_000,
			),
			benchmark(
				"-52 ^ 25",
				referenceItemFactory = { BigInteger(-52) },
				reference = { a, _ -> a.pow(25) },
				benchedItemFactory = { BigInt.fromInt(-52) },
				benched = { a, _ -> a.pow(25) },
				target = { LongNumScope.pow(-52L, 25) },
				times = 10_000_000,
			),
			benchmark(
				"-52 ^ 24",
				referenceItemFactory = { BigInteger(-52) },
				reference = { a, _ -> a.pow(24) },
				benchedItemFactory = { BigInt.fromInt(-52) },
				benched = { a, _ -> a.pow(24) },
				target = { LongNumScope.pow(-52L, 24) },
				times = 10_000_000,
			),
			benchmark(
				"10 ^ 5",
				referenceItemFactory = { BigInteger.TEN },
				reference = { a, _ -> a.pow(5) },
				benchedItemFactory = { BigInt.TEN },
				benched = { a, _ -> a.pow(5) },
				target = { LongNumScope.pow(10L, 5) },
				times = 50_000_000,
			),
			benchmark(
				"10 ^ 24",
				referenceItemFactory = { BigInteger.TEN },
				reference = { a, _ -> a.pow(24) },
				benchedItemFactory = { BigInt.TEN },
				benched = { a, _ -> a.pow(24) },
				target = { LongNumScope.pow(10L, 24) },
				times = 10_000_000,
			),
			benchmark(
				"-10 ^ 24",
				referenceItemFactory = { BigInteger(-10) },
				reference = { a, _ -> a.pow(24) },
				benchedItemFactory = { BigInt.fromInt(-10) },
				benched = { a, _ -> a.pow(24) },
				target = { LongNumScope.pow(-10L, 24) },
				times = 10_000_000,
			),
			benchmark(
				"10000 ^ 12",
				referenceItemFactory = { BigInteger(10000) },
				reference = { a, _ -> a.pow(12) },
				benchedItemFactory = { BigInt.fromInt(10000) },
				benched = { a, _ -> a.pow(12) },
				target = { LongNumScope.pow(10000L, 12) },
				times = 10_000_000,
			),
			benchmark(
				"20880467999847912034355032910567 ^ 243",
				referenceItemFactory = { BigInteger("20880467999847912034355032910567") },
				reference = { a, _ -> a.pow(243) },
				benchedItemFactory = { BigInt.fromString("20880467999847912034355032910567") },
				benched = { a, _ -> a.pow(243) },
				target = { LongNumScope.pow(23, 243 * 23) },
				times = 1_000,
			),
		).forEach { assert(it) }
	}


	private inline fun benchmark(
		tag: String = "",
		reference: (count: Int) -> Unit,
		benched: (count: Int) -> Unit,
		target: (count: Int) -> Unit,
		times: Int,
	): Boolean {
		return benchmark(
			tag,
			referenceItemFactory = {},
			reference = { _, count -> reference(count) },
			benchedItemFactory = {},
			benched = { _, count -> benched(count) },
			target,
			times,
		)
	}

	private inline fun <A, B> benchmark(
		tag: String = "",
		referenceItemFactory: () -> A,
		reference: (item: A, count: Int) -> Unit,
		benchedItemFactory: () -> B,
		benched: (item: B, count: Int) -> Unit,
		target: (count: Int) -> Unit,
		times: Int,
	): Boolean {
		measureNanoTime {}
		val targetTime = measureNanoTime { repeat(times, target) }
		val referenceItem = referenceItemFactory()
		val referenceTime = measureNanoTime { repeat(times) { reference(referenceItem, it) } }
		val benchedItem = benchedItemFactory()
		val benchedTime = measureNanoTime { repeat(times) { benched(benchedItem, it) } }

		if (tag.isNotEmpty()) print("$tag -> ")
		println(
			String.format(
				"Benched: %.3fs, Reference: %.3fs, Target %.3fs",
				benchedTime * 1e-9,
				referenceTime * 1e-9,
				targetTime * 1e-9,
			)
		)
		return benchedTime < referenceTime * 1.5
	}
}