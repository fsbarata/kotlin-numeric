package com.github.fsbarata.numeric.ints

fun Int.toBigInt() = BigInt.fromInt(this)
fun Long.toBigInt() = BigInt.fromLong(this)

operator fun Int.plus(addend: BigInt) = addend + this
operator fun Long.plus(addend: BigInt) = addend + this
operator fun Int.minus(sub: BigInt) = -sub + this
operator fun Long.minus(sub: BigInt) = -sub + this
operator fun Int.times(multiplier: BigInt) = multiplier * this
operator fun Long.times(multiplier: BigInt) = multiplier * this
operator fun Int.div(divisor: BigInt): Int {
	return (this / (divisor.toIntOrNull() ?: return 0))
}

operator fun Long.div(divisor: BigInt): Long {
	return (this / (divisor.toLongOrNull() ?: return 0L))
}
