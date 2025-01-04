package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ints.Int128
import com.github.fsbarata.numeric.ratio.Ratio
import com.github.fsbarata.numeric.ratio.Rational

interface Fractional<T>: Num<T>, Divisible<T> {
	override operator fun div(divisor: T): T
	fun recip(): T

	operator fun div(divisor: Int): T

	interface Scope<T>: Num.Scope<T>, OpScope<T> {
		override fun divideInt(a: T, b: Int) = divide(a, fromInt(b))

		fun fromIntRatio(ratio: Ratio<Int>): T =
			divide(fromInt(ratio.numerator), fromInt(ratio.denominator))

		fun fromLongRatio(ratio: Ratio<Long>): T =
			divide(fromLong(ratio.numerator), fromLong(ratio.denominator))

		fun fromInt128Ratio(ratio: Ratio<Int128>): T =
			divide(fromInt128(ratio.numerator), fromInt128(ratio.denominator))

		fun fromRational(rational: Rational): T =
			divide(fromBigInt(rational.numerator), fromBigInt(rational.denominator))

		override fun pow(base: T, exp: Int): T {
			return when {
				exp < 0 -> recip(super<Num.Scope>.pow(base, -exp))
				else -> super<Num.Scope>.pow(base, exp)
			}
		}
	}

	interface OpScope<T>: Num.OpScope<T>, Divisible.Scope<T> {
		operator fun T.div(divisor: Int): T = divideInt(this, divisor)
		fun divideInt(a: T, b: Int): T

		override fun divide(a: T, b: T): T = multiply(a, recip(b))
		fun recip(a: T): T
	}

	companion object {
		fun <T: Fractional<T>> delegateOpScope(): OpScope<T> =
			object: OpScope<T>, Num.OpScope<T> by Num.delegateOpScope(), Divisible.Scope<T> {
				override fun divideInt(a: T, b: Int) = a / b
				override fun divide(a: T, b: T) = a / b
				override fun recip(a: T): T = a.recip()
			}
	}
}
