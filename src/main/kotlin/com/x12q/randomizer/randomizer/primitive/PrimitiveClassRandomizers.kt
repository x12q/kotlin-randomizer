package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun intRandomizer(value: Int): ClassRandomizer<Int> {
    return intRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers.
 */
fun intRandomizer(
    random: () -> Int
): ClassRandomizer<Int> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers within a range.
 */
fun intRandomizer(
    range: IntRange
): ClassRandomizer<Int> {
    return classRandomizer {
        range.random()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers up to certain value.
 */
fun intRandomizerUntil(until: Int): ClassRandomizer<Int> {
    return classRandomizer {
        Random.nextInt(until)
    }
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats.
 */
fun floatRandomizer(
    random: () -> Float
): ClassRandomizer<Float> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always return a fixed [value].
 */
fun floatRandomizer(
    value: Float
): ClassRandomizer<Float> {
    return floatRandomizer { value }
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats within a range.
 */
fun floatRandomizer(
    from: Float, to: Float
): ClassRandomizer<Float> {
    return classRandomizer {
        Random.nextDouble(from.toDouble(), to.toDouble()).toFloat()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats up to a limit.
 */
fun floatRandomizerUntil(until: Float): ClassRandomizer<Float> {
    return classRandomizer {
        Random.nextDouble(until.toDouble()).toFloat()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random strings.
 */
fun stringRandomizer(
    random: () -> String
): ClassRandomizer<String> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always return a fixed [value].
 */
fun stringRandomizer(
    value: String
): ClassRandomizer<String> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random uuid strings.
 */
fun uuidStringRandomizer(): ClassRandomizer<String> {
    return classRandomizer {
        randomUUIDStr()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles.
 */
fun doubleRandomizer(
    random: () -> Double
): ClassRandomizer<Double> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always return a fixed [value].
 */
fun doubleRandomizer(
    value: Double
): ClassRandomizer<Double> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles within a range.
 */
fun doubleRandomizer(
    from: Double, to: Double
): ClassRandomizer<Double> {
    return classRandomizer {
        Random.nextDouble(from, to)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles up to a limit.
 */
fun doubleRandomizerUntil(until: Double): ClassRandomizer<Double> {
    return classRandomizer {
        Random.nextDouble(until)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random bytes.
 */
fun byteRandomizer(
    random: () -> Byte
): ClassRandomizer<Byte> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun byteRandomizer(
    value: Byte
): ClassRandomizer<Byte> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random shorts.
 */
fun shortRandomizer(
    random: () -> Short
): ClassRandomizer<Short> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun shortRandomizer(
    value: Short
): ClassRandomizer<Short> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random booleans.
 */
fun booleanRandomizer(
    random: () -> Boolean
): ClassRandomizer<Boolean> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun booleanRandomizer(
    value: Boolean
): ClassRandomizer<Boolean> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random long.
 */
fun longRandomizer(
    random: () -> Long
): ClassRandomizer<Long> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random long.
 */
fun longRandomizer(
    value: Long
): ClassRandomizer<Long> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random longs within a range.
 */
fun longRandomizer(
    range: LongRange
): ClassRandomizer<Long> {
    return classRandomizer {
        Random.nextLong(range.first, range.last)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random longs up to certain value.
 */
fun longRandomizerUntil(until: Long): ClassRandomizer<Long> {
    return classRandomizer {
        Random.nextLong(until)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random characters.
 */
fun charRandomizer(
    random: () -> Char
): ClassRandomizer<Char> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun charRandomizer(
    value: Char
): ClassRandomizer<Char> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random list.
 */
fun <T> listRandomizer(
    random: () -> List<T>
): ClassRandomizer<List<T>> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun <T> listRandomizer(
    value: List<T>
): ClassRandomizer<List<T>> {
    return classRandomizer { value }
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce a random map.
 */
fun <K, V> mapRandomizer(
    random: () -> Map<K, V>
): ClassRandomizer<Map<K, V>> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun <K, V> mapRandomizer(
    value: Map<K, V>
): ClassRandomizer<Map<K, V>> {
    return classRandomizer { value }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random set.
 */
fun <T> setRandomizer(
    random: () -> Set<T>
): ClassRandomizer<Set<T>> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that always returns a fixed [value].
 */
fun <T> setRandomizer(
    value: Set<T>
): ClassRandomizer<Set<T>> {
    return classRandomizer { value }
}
