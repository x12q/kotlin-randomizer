package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import java.util.UUID
import kotlin.random.Random


/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers
 */
fun intRandomizer(
    random: () -> Int
): ClassRandomizer<Int> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers within a range
 */
fun intRandomizer(
    range:IntRange
): ClassRandomizer<Int> {
    return intRandomizer {
        Random.nextInt(range.first,range.last)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers up to certain value
 */
fun intRandomizer(until:Int): ClassRandomizer<Int> {
    return intRandomizer {
        Random.nextInt(until)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random integers
 */
fun intRandomizer(): ClassRandomizer<Int> {
    return intRandomizer {
        Random.nextInt()
    }
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats
 */
fun floatRandomizer(
    random: () -> Float
): ClassRandomizer<Float> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats within a range
 */
fun floatRandomizer(
    from:Float, to:Float
): ClassRandomizer<Float> {
    return floatRandomizer {
        Random.nextDouble(from.toDouble(),to.toDouble()).toFloat()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats up to a limit
 */
fun floatRandomizer(until:Float): ClassRandomizer<Float> {
    return floatRandomizer {
        Random.nextDouble(until.toDouble()).toFloat()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random floats
 */
fun floatRandomizer(): ClassRandomizer<Float> {
    return floatRandomizer {
        Random.nextFloat()
    }
}


/**
 * Convenient function to create a [ClassRandomizer] that can produce random strings
 */
fun stringRandomizer(
    random: () -> String
): ClassRandomizer<String> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random uuid strings
 */
fun uuidStringRandomizer(): ClassRandomizer<String> {
    return classRandomizer{
        UUID.randomUUID().toString()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles
 */
fun doubleRandomizer(
    random: () -> Double
): ClassRandomizer<Double> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles within a range
 */
fun doubleRandomizer(
    from:Double, to:Double
): ClassRandomizer<Double> {
    return doubleRandomizer {
        Random.nextDouble(from,to)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random doubles up to a limit
 */
fun doubleRandomizer(until:Double): ClassRandomizer<Double> {
    return doubleRandomizer {
        Random.nextDouble(until)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce doubles floats
 */
fun doubleRandomizer(): ClassRandomizer<Double> {
    return doubleRandomizer {
        Random.nextDouble()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random bytes
 */
fun byteRandomizer(
    random: () -> Byte
): ClassRandomizer<Byte> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random shorts
 */
fun shortRandomizer(
    random: () -> Short
): ClassRandomizer<Short> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random booleans
 */
fun booleanRandomizer(
    random: () -> Boolean
): ClassRandomizer<Boolean> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random booleans
 */
fun booleanRandomizer(): ClassRandomizer<Boolean> {
    return classRandomizer{
        Random.nextBoolean()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random long
 */
fun longRandomizer(
    random: () -> Long
): ClassRandomizer<Long> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random longs within a range
 */
fun longRandomizer(
    range:LongRange
): ClassRandomizer<Long> {
    return longRandomizer {
        Random.nextLong(range.first,range.last)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random longs up to certain value
 */
fun longRandomizer(until:Long): ClassRandomizer<Long> {
    return longRandomizer {
        Random.nextLong(until)
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random longs
 */
fun longRandomizer(): ClassRandomizer<Long> {
    return longRandomizer {
        Random.nextLong()
    }
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce random characters
 */
fun charRandomizer(
    random: () -> Char
): ClassRandomizer<Char> {
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random list
 */
fun <T> listRandomizer(
    random:()->List<T>
):ClassRandomizer<List<T>>{
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random map
 */
fun <K,V> mapRandomizer(
    random: ()->Map<K,V>
):ClassRandomizer<Map<K,V>>{
    return classRandomizer(random)
}

/**
 * Convenient function to create a [ClassRandomizer] that can produce a random set
 */
fun <T> setRandomizer(
    random: ()->Set<T>
):ClassRandomizer<Set<T>>{
    return classRandomizer(random)
}

