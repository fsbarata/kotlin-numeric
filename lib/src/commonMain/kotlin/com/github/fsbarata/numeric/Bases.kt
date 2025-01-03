package com.github.fsbarata.numeric

interface Additive<T> {
	operator fun plus(addend: T): T

	interface Scope<T> {
		operator fun T.plus(addend: T): T = add(this, addend)
		fun add(a: T, b: T): T
	}

	companion object {
		fun <T: Additive<T>> delegateScope() = object: Scope<T> {
			override fun add(a: T, b: T): T = a + b
		}
	}
}

interface Signed<T> {
	fun signum(): Int
	operator fun unaryPlus(): T = this as T
	operator fun unaryMinus(): T

	fun abs(): T = if (signum() < 0) unaryMinus() else unaryPlus()

	fun isZero(): Boolean = signum() == 0
	fun isNotZero(): Boolean = !isZero()
	fun isPositive(): Boolean = signum() > 0
	fun isNegative(): Boolean = signum() < 0

	interface Scope<T> {
		fun signum(a: T): Int
		operator fun T.unaryPlus(): T = this
		operator fun T.unaryMinus(): T = negate(this)
		fun negate(a: T): T

		fun abs(a: T): T = if (signum(a) < 0) -a else +a

		fun isZero(a: T): Boolean = signum(a) == 0
		fun isNotZero(a: T): Boolean = !isZero(a)
		fun isPositive(a: T): Boolean = signum(a) > 0
		fun isNegative(a: T): Boolean = signum(a) < 0
	}

	companion object {
		fun <T: Signed<T>> delegateScope() = object: Scope<T> {
			override fun signum(a: T): Int = a.signum()
			override fun negate(a: T): T = a.unaryMinus()

			override fun abs(a: T): T = a.abs()

			override fun isZero(a: T): Boolean = a.isZero()
			override fun isNotZero(a: T): Boolean = a.isNotZero()
			override fun isPositive(a: T): Boolean = a.isPositive()
			override fun isNegative(a: T): Boolean = a.isNegative()
		}
	}
}

interface Subtractive<T>: Additive<T>, Signed<T> {
	operator fun minus(sub: T): T

	interface Scope<T>: Additive.Scope<T>, Signed.Scope<T> {
		operator fun T.minus(sub: T): T = subtract(this, sub)
		fun subtract(a: T, b: T): T = add(a, -b)
	}

	companion object {
		fun <T: Subtractive<T>> delegateScope(): Scope<T> =
			object: Scope<T>,
				Signed.Scope<T> by Signed.delegateScope<T>(),
				Additive.Scope<T> by Additive.delegateScope<T>() {
				override fun subtract(a: T, b: T): T = a - b
			}
	}
}

interface Multiplicative<T> {
	operator fun times(multiplier: T): T

	fun sqr(): T = times(this as T)

	interface Scope<T> {
		operator fun T.times(multiplier: T): T = multiply(this, multiplier)
		fun multiply(a: T, b: T): T
		fun sqr(a: T): T = multiply(a, a)

		fun pow(base: T, exp: Int): T {
			if (exp < 0) throw ArithmeticException("Negative exponent")
			if (exp == 0) throw ArithmeticException("Multiplicative does not define ONE")

			var result = base
			var b: T = base
			var e: Int = exp - 1

			while (e > 0) {
				// If exponent is odd, multiply result by base
				if (e and 1 == 1) result *= b

				// Square the base and divide exponent by 2
				b = sqr(b)
				e = e shr 1
			}

			return result
		}
	}

	companion object {
		fun <T: Multiplicative<T>> delegateScope(): Scope<T> =
			object: Scope<T> {
				override fun multiply(a: T, b: T): T = a * b
				override fun sqr(a: T): T = a.sqr()
			}
	}
}

interface Divisible<T> {
	operator fun div(divisor: T): T

	interface Scope<T> {
		operator fun T.div(divisor: T): T = divide(this, divisor)
		fun divide(a: T, b: T): T
	}

	companion object {
		fun <T: Divisible<T>> delegateScope(): Scope<T> =
			object: Scope<T> {
				override fun divide(a: T, b: T): T = a / b
			}
	}
}

