package com.x12q.randomizer

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
 *     - Rule for primitive types (string, int, etc) must be very easy to set
 */
annotation class Randomizable
