package com.x12q.randomizer.util

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.Randomizer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
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

    fun getInnerTypes(kParam: KParameter):List<KTypeParameter>?{
        val classifier = kParam.type.classifier
        val innerTypes = when(classifier){
            is KClass<*> -> classifier.typeParameters
            is KTypeParameter -> listOf(classifier)
            else -> null
        }
        return innerTypes
    }
    /**
     * Get type parameter supplied by enclosing class within a KParameter
     */
    fun getSuppliedTypes(param:KParameter): List<KTypeParameter> {
        return param.type.arguments.mapNotNull { it.type?.classifier as? KTypeParameter }
    }

    /**
     * Extract a mapping from inner generic type -> supplied type.
     */
    fun getTypeMap(kParam:KParameter): Map<KTypeParameter, KTypeParameter>? {
        val innerTypes = getInnerTypes(kParam)
        if (innerTypes != null) {
            val suppliedType = getSuppliedTypes(kParam)
            return innerTypes.zip(suppliedType).toMap()
        } else {
            return null
        }
    }
}
