package com.x12q.randomizer.util

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.Randomizer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object ReflectionUtils {

    /**
     * Create a [Randomizer] from its [KClass]
     */
    private fun createRandomizer(clazz: KClass<out Randomizer<*>>): Randomizer<*> {
        val obj = clazz.objectInstance
        if (obj != null) {
            return obj
        } else if (clazz.isAbstract) {
            throw IllegalArgumentException("${clazz.qualifiedName} is abstract, therefore can't create an instance of it")
        } else {
            return clazz.createInstance()
        }
    }

    /**
     * Create a [ClassRandomizer] from its [KClass]
     */
    fun createClassRandomizer(clazz: KClass<out ClassRandomizer<*>>): ClassRandomizer<*> {
        return createRandomizer(clazz) as ClassRandomizer<*>
    }

    /**
     * Create a [ParameterRandomizer] from its [KClass]
     */
    fun createParamRandomizer(clazz: KClass<out ParameterRandomizer<*>>): ParameterRandomizer<*> {
        return createRandomizer(clazz) as ParameterRandomizer<*>
    }
}
