[![](https://jitpack.io/v/fsbarata/kotlin-numeric.svg)](https://jitpack.io/#fsbarata/kotlin-numeric)

## Kotlin numeric

Implementation of numeric library for kotlin multiplatform.
*Includes a Int128 and BigInt.*

### Examples
Divide Longs with ceil rounding:<br>

    LongNumScope.divide(3L, 5L, RoundingType.CEIL)
    Int128.divide(Int128.fromInt(912), Int128.fromInt(2), RoundingType.HALF_UP)

Fractions:<br>

    (35L over 12L) * 5 // LongFraction
    Rational.create(BigInt.fromString("199999"), BigInt.fromString("129111")) // BigInt Fraction

Generic numeric:<br>

    fun <A> Fractional.Scope<A>.varianceOrNull(iterable: Iterable<A>): A? {
	    val average = averageOrNull(iterable) ?: return null
    	return averageOfOrNull(iterable) { sqr(it - average) }
    }

    fun <A> Fractional.Scope<A>.roundToResolution(a: A, resolution: A, roundingType: RoundingType): A {
	    val quo = toRational(a) / toRational(resolution)
	    val sizes = quo.round(roundingType)
	    return resolution * fromBigInt(sizes)
    }



### Interfaces

- Additive, Subtractive, Multiplicative, Divisivible
- Num (integers and floating point decimals/binaries)
- Integral (integers) and Fractionals (floating points and fractions)
- Exact scopes that have a nullable add/subtract/multiply so there is no risk of overflowing/underflowing.

### Classes

- Num Scopes for primitive types
- Int128 - Signed integer with 16 bytes (128 bits)
- BigInt - Arbitrary length signed integer (as BigInteger in Java)
- Fractions (Int, Long, Int128 and BigInt) - represeting a ratio between 2 integer numbers

### Functions

- Averages
- Integer division with rounding

