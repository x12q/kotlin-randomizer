package com.x12q.randomizer.util

import com.x12q.randomizer.RDClassData
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


    /**
     * Construct an index-type map for a particular [constructorParam], using type data from [enclosureRDClassData].
     * The index of the output map is: the index of generic type appear in [constructorParam]
     * Example:
     * For parameter of type Q<T1,T2,T3>, and given that T1 -> String, T3->Double, T3 -> another generic from enclosure
     */
    fun makeTypeMap(
        constructorParam: KParameter,
        enclosureRDClassData: RDClassData,
        parentTypeMap:Map<Int, RDClassData> = emptyMap(),
    ): Map<Int, RDClassData> {
        /**
         * This gives the entire type structure of the param
         * Eg: Q1<Q2<Int>>
         */
        val ktype = constructorParam.type

        /**
         * Perform lookup on [enclosureRDClassData] to know which concrete types are passed to this [constructorParam] in place of its generic type, at which index
         */
        val typeMapFromEnclosure: Map<Int, RDClassData> = ktype.arguments.withIndex().mapNotNull { (index, arg) ->
            // only consider type parameter, ignore the rest
            val argKtypeParam = arg.type?.classifier as? KTypeParameter
            val concreteType = argKtypeParam?.let { enclosureRDClassData.getDataFor(it) }
            val pair = concreteType?.let {
                index to it
            }
            pair
        }.toMap()
        return typeMapFromEnclosure
    }

}
