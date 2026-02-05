package com.github.fsbarata.numeric

import java.math.RoundingMode

fun RoundingType?.toMode() = when (this) {
	null -> RoundingMode.UNNECESSARY
	RoundingType.HALF_UP -> RoundingMode.HALF_UP
	RoundingType.FLOOR -> RoundingMode.FLOOR
	RoundingType.CEIL -> RoundingMode.CEILING
	RoundingType.AWAY_FROM_ZERO -> RoundingMode.UP
	RoundingType.TRUNCATE -> RoundingMode.DOWN
}

fun RoundingMode.toType(): RoundingType? = when (this) {
	RoundingMode.UNNECESSARY -> null
	RoundingMode.UP -> RoundingType.AWAY_FROM_ZERO
	RoundingMode.DOWN -> RoundingType.TRUNCATE
	RoundingMode.CEILING -> RoundingType.CEIL
	RoundingMode.FLOOR -> RoundingType.FLOOR
	RoundingMode.HALF_UP -> RoundingType.HALF_UP
	RoundingMode.HALF_DOWN -> null
	RoundingMode.HALF_EVEN -> null
}
