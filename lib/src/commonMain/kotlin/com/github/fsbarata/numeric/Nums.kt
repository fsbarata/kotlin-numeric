package com.github.fsbarata.numeric

inline fun <T: Additive<T>> Iterable<T>.cumsum(): List<T> = runningReduce { a, b -> a + b }
