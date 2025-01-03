package com.github.fsbarata.numeric

import kotlin.test.assertEquals

fun <A, R> assertCommutative(
	expected: R,
	operand1: A,
	operand2: A,
	operation: (A, A) -> R,
) {
	assertCommutative(expected, operand1, operand2, "op", operation)
}

fun <A, R> assertCommutative(
	expected: R,
	operand1: A,
	operand2: A,
	operationSymbol: String,
	operation: (A, A) -> R,
) {
	assertEquals(expected, operation(operand1, operand2), "$operand1 $operationSymbol $operand2")
	assertEquals(expected, operation(operand2, operand1), "$operand2 $operationSymbol $operand1")
}

fun <D, R> assertDivRem(expectedDiv: D, expectedRem: R, actualDivRem: Pair<D, R>) {
	assertEquals(expectedDiv, actualDivRem.first, "Div not equal")
	assertEquals(expectedRem, actualDivRem.second, "Rem not equal")
}
