package com.github.fsbarata.numeric.ratio

import com.github.fsbarata.numeric.ints.BigInt
import com.github.fsbarata.numeric.ints.Int128

operator fun Int.plus(fraction: IntFraction): IntFraction = fraction + this
operator fun Int.plus(fraction: LongFraction): LongFraction = fraction + this
operator fun Long.plus(fraction: LongFraction): LongFraction = fraction + this
operator fun Int.plus(fraction: Rational): Rational = fraction + this
operator fun Long.plus(fraction: Rational): Rational = fraction + this
operator fun BigInt.plus(fraction: Rational): Rational = fraction + this

operator fun Int.minus(fraction: IntFraction): IntFraction = IntFraction.fromInt(this) - fraction
operator fun Int.minus(fraction: LongFraction): LongFraction = LongFraction.fromInt(this) - fraction
operator fun Long.minus(fraction: LongFraction): LongFraction = LongFraction.fromLong(this) - fraction
operator fun Int.minus(fraction: Rational): Rational = Rational.fromInt(this) - fraction
operator fun Long.minus(fraction: Rational): Rational = Rational.fromLong(this) - fraction
operator fun BigInt.minus(fraction: Rational): Rational = Rational.fromBigInt(this) - fraction

operator fun Int.times(fraction: IntFraction): IntFraction = fraction * this
operator fun Int.times(fraction: LongFraction): LongFraction = fraction * this
operator fun Long.times(fraction: LongFraction): LongFraction = fraction * this
operator fun Int.times(fraction: Rational): Rational = fraction * this
operator fun Long.times(fraction: Rational): Rational = fraction * this
operator fun BigInt.times(fraction: Rational): Rational = fraction * this

operator fun Int.div(fraction: IntFraction): IntFraction = fraction.recip() * this
operator fun Int.div(fraction: LongFraction): LongFraction = fraction.recip() * this
operator fun Long.div(fraction: LongFraction): LongFraction = fraction.recip() * this
operator fun Int.div(fraction: Rational): Rational = fraction.recip() * this
operator fun Long.div(fraction: Rational): Rational = fraction.recip() * this
operator fun BigInt.div(fraction: Rational): Rational = fraction.recip() * this

infix fun Int.over(other: Int): IntFraction = IntFraction.create(this, other)
infix fun Long.over(other: Long): LongFraction = LongFraction.create(this, other)
infix fun BigInt.over(other: BigInt): Rational = Rational.create(this, other)

fun Int.percent() = IntFraction.create(this, 100)
fun Long.percent() = LongFraction.create(this, 100L)
fun Int128.percent() = Int128Fraction.create(this, Int128.fromInt(100))
fun BigInt.percent() = Rational.create(this, BigInt.fromInt(100))
fun Int.permille() = IntFraction.create(this, 1000)
fun Long.permille() = LongFraction.create(this, 1000)
fun Int128.permille() = Int128Fraction.create(this, Int128.fromInt(1000))
fun BigInt.permille() = Rational.create(this, BigInt.fromInt(1000))
fun Int.permyriad() = IntFraction.create(this, 10_000)
fun Long.permyriad() = LongFraction.create(this, 10_000L)
fun Int128.permyriad() = Int128Fraction.create(this, Int128.fromInt(10_000))
fun BigInt.permyriad() = Rational.create(this, BigInt.fromInt(10_000))
fun Int.pcm() = IntFraction.create(this, 100_000)
fun Long.pcm() = LongFraction.create(this, 100_000L)
fun Int128.pcm() = Int128Fraction.create(this, Int128.fromInt(100_000))
fun BigInt.pcm() = Rational.create(this, BigInt.fromInt(100_000))
fun Int.ppm() = IntFraction.create(this, 1000_000)
fun Long.ppm() = LongFraction.create(this, 1000_000L)
fun Int128.ppm() = Int128Fraction.create(this, Int128.fromInt(1000_000))
fun BigInt.ppm() = Rational.create(this, BigInt.fromInt(1000_000))

