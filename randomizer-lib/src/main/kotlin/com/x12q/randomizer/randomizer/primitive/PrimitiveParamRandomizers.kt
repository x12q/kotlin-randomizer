package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.param.paramRandomizer
import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random
import kotlin.random.nextLong


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers
 */
fun intParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Int,
): ParameterRandomizer<Int> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers
 */
fun intParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Int,
): ParameterRandomizer<Int> {
    return paramRandomizer(
        condition = condition,
        random = { value },
    )
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers
 */
fun intParamRandomizer(
    random: (ParamInfo) -> Int,
): ParameterRandomizer<Int> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun intParamRandomizer(
    value: Int,
): ParameterRandomizer<Int> {
    return paramRandomizer { value }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers within a range
 */
fun intParamRandomizer(
    range: IntRange,
): ParameterRandomizer<Int> {
    return paramRandomizer {
        Random.nextInt(range.first, range.last)
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random integers up to a certain value
 */
fun intParamRandomizerUntil(until: Int): ParameterRandomizer<Int> {
    return paramRandomizer {
        Random.nextInt(until)
    }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random floats
 */
fun floatParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Float,
): ParameterRandomizer<Float> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun floatParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Float,
): ParameterRandomizer<Float> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random floats
 */
fun floatParamRandomizer(
    random: (ParamInfo) -> Float,
): ParameterRandomizer<Float> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun floatParamRandomizer(
    value: Float,
): ParameterRandomizer<Float> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random floats within a range
 */
fun floatParamRandomizer(
    from: Float, to: Float
): ParameterRandomizer<Float> {
    return paramRandomizer {
        Random.nextDouble(from.toDouble(), to.toDouble()).toFloat()
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random floats up to a certain value
 */
fun floatParamRandomizerUntil(until: Float): ParameterRandomizer<Float> {
    return paramRandomizer {
        Random.nextDouble(until.toDouble()).toFloat()
    }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random strings.
 */
fun stringParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> String,
): ParameterRandomizer<String> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun stringParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: String,
): ParameterRandomizer<String> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random strings.
 */
fun stringParamRandomizer(
    random: (ParamInfo) -> String,
): ParameterRandomizer<String> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun stringParamRandomizer(
    value: String,
): ParameterRandomizer<String> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random uuid strings
 */
fun uuidStringParamRandomizer(
): ParameterRandomizer<String> {
    return paramRandomizer {
        randomUUIDStr()
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles.
 */
fun doubleParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Double,
): ParameterRandomizer<Double> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun doubleParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Double,
): ParameterRandomizer<Double> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles
 */
fun doubleParamRandomizer(
    random: (ParamInfo) -> Double,
): ParameterRandomizer<Double> {
    return paramRandomizer(random)
}


/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun doubleParamRandomizer(
    value: Double,
): ParameterRandomizer<Double> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles within a range
 */
fun doubleParamRandomizer(
    from: Double, to: Double
): ParameterRandomizer<Double> {
    return paramRandomizer {
        Random.nextDouble(from, to)
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles up to a certain value
 */
fun doubleParamRandomizerUntil(until: Double): ParameterRandomizer<Double> {
    return paramRandomizer {
        Random.nextDouble(until)
    }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random bytes.
 */
fun byteParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Byte,
): ParameterRandomizer<Byte> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun byteParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Byte
): ParameterRandomizer<Byte> {
    return paramRandomizer(condition = condition, random = { value })
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random bytes.
 */
fun byteParamRandomizer(
    random: (ParamInfo) -> Byte,
): ParameterRandomizer<Byte> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun byteParamRandomizer(
    value: Byte,
): ParameterRandomizer<Byte> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random shorts
 */
fun shortParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Short,
): ParameterRandomizer<Short> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun shortParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Short,
): ParameterRandomizer<Short> {
    return paramRandomizer(condition = condition, random = { value })
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random shorts
 */
fun shortParamRandomizer(
    random: (ParamInfo) -> Short,
): ParameterRandomizer<Short> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun shortParamRandomizer(
    value: Short,
): ParameterRandomizer<Short> {
    return paramRandomizer { value }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random booleans
 */
fun booleanParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Boolean,
): ParameterRandomizer<Boolean> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun booleanParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Boolean,
): ParameterRandomizer<Boolean> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random booleans
 */
fun booleanParamRandomizer(
    random: (ParamInfo) -> Boolean,
): ParameterRandomizer<Boolean> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun booleanParamRandomizer(
    value: Boolean,
): ParameterRandomizer<Boolean> {
    return paramRandomizer { value }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random long
 */
fun longParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Long,
): ParameterRandomizer<Long> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun longParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Long,
): ParameterRandomizer<Long> {
    return paramRandomizer(condition = condition, random = { value })
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random longs
 */
fun longParamRandomizer(
    random: (ParamInfo) -> Long,
): ParameterRandomizer<Long> {
    return paramRandomizer(random)
}


/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun longParamRandomizer(
    value: Long
): ParameterRandomizer<Long> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles within a range
 */
fun longParamRandomizer(
    longRange: LongRange
): ParameterRandomizer<Long> {
    return paramRandomizer {
        Random.nextLong(longRange)
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random doubles up to a certain value
 */
fun longParamRandomizerUntil(until: Long): ParameterRandomizer<Long> {
    return paramRandomizer {
        Random.nextLong(until)
    }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random characters
 */
fun charParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Char,
): ParameterRandomizer<Char> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun charParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Char,
): ParameterRandomizer<Char> {
    return paramRandomizer(condition = condition, random = { value })
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random characters
 */
fun charParamRandomizer(
    random: (ParamInfo) -> Char,
): ParameterRandomizer<Char> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun charParamRandomizer(
    value: Char,
): ParameterRandomizer<Char> {
    return paramRandomizer { value }
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random sets
 */
fun <T> setParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Set<T>,
): ParameterRandomizer<Set<T>> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun <T> setParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Set<T>,
): ParameterRandomizer<Set<T>> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random sets
 */
fun <T> setParamRandomizer(
    random: (ParamInfo) -> Set<T>,
): ParameterRandomizer<Set<T>> {
    return paramRandomizer(random)
}


/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun <T> setParamRandomizer(
    value: Set<T>,
): ParameterRandomizer<Set<T>> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random map
 */
fun <K, V> mapParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> Map<K, V>,
): ParameterRandomizer<Map<K, V>> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun <K, V> mapParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: Map<K, V>,
): ParameterRandomizer<Map<K, V>> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random map
 */
fun <K, V> mapParamRandomizer(
    random: (ParamInfo) -> Map<K, V>,
): ParameterRandomizer<Map<K, V>> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that can produce random map
 */
fun <K, V> mapParamRandomizer(
    value: Map<K, V>,
): ParameterRandomizer<Map<K, V>> {
    return paramRandomizer { value }
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random list
 */
fun <T> listParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    random: (ParamInfo) -> List<T>,
): ParameterRandomizer<List<T>> {
    return paramRandomizer(condition, random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun <T> listParamRandomizer(
    condition: (target: ParamInfo) -> Boolean,
    value: List<T>,
): ParameterRandomizer<List<T>> {
    return paramRandomizer(condition = condition, random = { value })
}


/**
 * Convenient function to create a [ParameterRandomizer] that can produce random list
 */
fun <T> listParamRandomizer(
    random: (ParamInfo) -> List<T>,
): ParameterRandomizer<List<T>> {
    return paramRandomizer(random)
}

/**
 * Convenient function to create a [ParameterRandomizer] that always returns a fixed [value].
 */
fun <T> listParamRandomizer(
    value: List<T>,
): ParameterRandomizer<List<T>> {
    return paramRandomizer { value }
}

