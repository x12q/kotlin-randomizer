package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer

/**
 * A builder that can build a list of [ClassRandomizer]
 */
class RandomizerListBuilder {

    private var lst = mutableListOf<ClassRandomizer<*>>()

    fun build(): Collection<ClassRandomizer<*>> {
        return lst.toList()
    }

    fun add(randomizer: ClassRandomizer<*>): RandomizerListBuilder {
        lst.add(randomizer)
        return this
    }

    fun <T>list(random:()->List<T>):RandomizerListBuilder{
        lst.add(listRandomizer (random))
        return this
    }

    fun <K,V> map(random:()->Map<K,V>):RandomizerListBuilder{
        lst.add(mapRandomizer(random))
        return this
    }

    fun int(random: () -> Int): RandomizerListBuilder {
        lst.add(intRandomizer(random))
        return this
    }

    fun float(random: () -> Float): RandomizerListBuilder {
        lst.add(floatRandomizer(random))
        return this
    }

    fun string(random: () -> String): RandomizerListBuilder {
        lst.add(stringRandomizer(random))
        return this
    }

    fun double(random: () -> Double): RandomizerListBuilder {
        lst.add(doubleRandomizer(random))
        return this
    }

    fun byte(random: () -> Byte): RandomizerListBuilder {
        lst.add(byteRandomizer(random))
        return this
    }

    fun short(random: () -> Short): RandomizerListBuilder {
        lst.add(shortRandomizer(random))
        return this
    }

    fun boolean(random: () -> Boolean): RandomizerListBuilder {
        lst.add(booleanRandomizer(random))
        return this
    }

    fun long(random: () -> Long): RandomizerListBuilder {
        lst.add(longRandomizer(random))
        return this
    }

    fun char(random: () -> Char): RandomizerListBuilder {
        lst.add(charRandomizer(random))
        return this
    }
}
