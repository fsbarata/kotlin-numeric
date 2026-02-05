package com.github.fsbarata.numeric

import com.github.fsbarata.numeric.ratio.IntFraction
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {
	@Test
	fun intFraction() {
		val original = IntFraction.create(5, 8)
		val bytes = ByteArrayOutputStream().apply {
			ObjectOutputStream(this).use {
				it.writeObject(original)
			}
		}.toByteArray()

		val clone = ByteArrayInputStream(bytes).use { ObjectInputStream(it).use { it.readObject() } } as IntFraction
		assertEquals(original, clone)
		assertEquals(0.625, clone.toDouble(), 1e-5)
		assertEquals(original, clone.reduced())
	}
}