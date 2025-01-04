package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Rational


interface Integral<T>: Num<T>, Divisible<T> {
	fun toInt(): Int
	fun toIntOrNull(): Int?
	fun toLong(): Long
	fun toLongOrNull(): Long?
	fun toInt128(): Int128
	fun toInt128OrNull(): Int128?
	fun toBigInt(): BigInt

	override operator fun div(divisor: T): T
	operator fun rem(divisor: T): T
	fun divRem(divisor: T): Pair<T, T> = Pair(div(divisor), rem(divisor))

	operator fun div(divisor: Int): T
	operator fun rem(divisor: Int): Int
	fun divRem(divisor: Int): Pair<T, Int> = Pair(div(divisor), rem(divisor))

	interface Scope<T>: Num.Scope<T>, OpScope<T> {
		operator fun T.div(divisor: Int): T = divide(this, divisor)
		fun divide(a: T, b: Int): T = divide(a, fromInt(b))

		fun divRem(a: T, b: Int): Pair<T, Int> {
			val (d, r) = divRem(a, fromInt(b))
			return Pair(
				d,
				toIntOrNull(r) ?: throw IllegalStateException("Remainder of division $a / $b must be smaller than $b"),
			)
		}

		operator fun T.rem(divisor: Int): Int = mod(this, divisor)
		fun mod(a: T, b: Int): Int =
			toIntOrNull(mod(a, fromInt(b)))
				?: throw IllegalStateException("Remainder of division $a / $b must be smaller than $b")
	}

	interface OpScope<T>: Num.OpScope<T>, Divisible.Scope<T> {
		fun divRem(a: T, b: T): Pair<T, T> = Pair(a / b, a % b)

		operator fun T.rem(divisor: T): T = mod(this, divisor)
		fun mod(a: T, b: T): T

		fun toInt(a: T): Int
		fun toIntOrNull(a: T): Int?
		fun toLong(a: T): Long
		fun toLongOrNull(a: T): Long?
		fun toInt128(a: T): Int128
		fun toInt128OrNull(a: T): Int128?
		fun toBigInt(a: T): BigInt

		override fun toRational(a: T): Rational = Rational.fromBigInt(toBigInt(a))
	}

	companion object {
		fun <T: Integral<T>> delegateOpScope(): OpScope<T> =
			object: OpScope<T>, Num.OpScope<T> by Num.delegateOpScope(),
				Divisible.Scope<T> by Divisible.delegateScope() {
				override fun divRem(a: T, b: T): Pair<T, T> = a.divRem(b)
				override fun mod(a: T, b: T): T = a % b

				override fun toInt(a: T): Int = a.toInt()
				override fun toIntOrNull(a: T): Int? = a.toIntOrNull()
				override fun toLong(a: T): Long = a.toLong()
				override fun toLongOrNull(a: T): Long? = a.toLongOrNull()
				override fun toInt128(a: T): Int128 = a.toInt128()
				override fun toInt128OrNull(a: T): Int128? = a.toInt128OrNull()
				override fun toBigInt(a: T): BigInt = a.toBigInt()

				override fun toRational(a: T): Rational = a.toRational()
			}
	}
}

interface ExactIntegralScope<T>: ExactNumScope<T>, Integral.Scope<T>
