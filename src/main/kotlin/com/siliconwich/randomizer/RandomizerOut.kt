package com.siliconwich.randomizer

import com.siliconwich.randomizer.config.RandomizerCollection
import com.siliconwich.randomizer.config.RandomizerConfigFactory
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Requirement:
 * - recursive function to traverse the constructor tree, and init object.
 * - for abstract parameter, add @Randomizable(ConcreteClass1::class, ConcreteClass2::class, AnotherRandomizableAbstractClass::class)
 * - For abstract class, add the same @Randomizable, the one in parameter override the one in the class.
 * - For concrete class, add @Randomizable(factoryFunction=...) or @Randomizable(randomizer = CustomRandomizer::class)
 *
 * The top level random function:
 * - should it accept some kind of master rule that override everything?
 *      - if so, what should those rule looks like?
 *          - each param rule contains:
 *              - a param name
 *              - type/ class of the param
 *              - a Parent class
 *              - a factory function
 *          - each class rule contains:
 *              - a class name
 *              - a factory function
 */

// for class + parameter
annotation class Randomizable


interface ParamRule{
    val paramName:String
    val paramType:KClass<*>
    val parentClass: KClass<*>?
    fun factoryFunction()
}

interface ClassRule{
    val clazz: KClass<*>
    fun factoryFunction()
}

object RandomizerOut {
    inline fun <reified T : Any> makeRandomInstance(
        random: Random = Random,
        config: RandomizerCollection = RandomizerConfigFactory.defaultConfig(),
    ): T {
        val producer = Randomizer(random, config)
        val q =RDClassData.from<T>()
        return producer.random(q) as T
    }
}
