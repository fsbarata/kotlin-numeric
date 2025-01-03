package com.github.fsbarata.numeric

actual fun scalb(a: Double, scaleFactor: Int): Double {
	return Math.scalb(a, scaleFactor)
}
