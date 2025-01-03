package com.github.fsbarata.numeric

actual fun scalb(a: Double, scaleFactor: Int): Double {
	return platform.posix.ldexp(a, scaleFactor)
}
