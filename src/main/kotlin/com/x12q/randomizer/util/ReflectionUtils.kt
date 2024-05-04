package com.x12q.randomizer.util

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.Randomizer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVariance
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

object ReflectionUtils {
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

    fun createClassRandomizer(clazz: KClass<out ClassRandomizer<*>>): ClassRandomizer<*> {
        return createRandomizer(clazz) as ClassRandomizer<*>
    }

    fun createParamRandomizer(clazz: KClass<out ParameterRandomizer<*>>): ParameterRandomizer<*> {
        return createRandomizer(clazz) as ParameterRandomizer<*>
    }
}
