package com.github.fsbarata.numeric


object Floating {
	interface Scope<T>: Fractional.Scope<T> {
		val PI: T
		val E: T

		fun exp(a: T): T
		fun ln(a: T): T

		fun log(a: T, base: T): T

		fun sin(a: T): T
		fun cos(a: T): T
		fun tan(a: T): T

		fun asin(a: T): T
		fun acos(a: T): T
		fun atan(a: T): T

		fun sinh(a: T): T
		fun cosh(a: T): T
		fun tanh(a: T): T

		fun asinh(a: T): T
		fun acosh(a: T): T
		fun atanh(a: T): T

		fun atan2(x: T, y: T): T
	}
}
