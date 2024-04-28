package com.x12q.randomizer.randomizer_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.test.BeforeTest
import kotlin.test.Test

class RandomizerEndProcessorTest {

    lateinit var processor: RandomizerChecker


    @BeforeTest
    fun bt() {
        processor = RandomizerChecker()
    }

    /**
     * Test if [RandomizerChecker] can identify the correct randomizer for a concrete class target
     */
    @Test
    fun getValidClassRandomizer_concrete_target() {
        val processor = RandomizerChecker()

        val testMap = mapOf(
            M1::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = M1::class,
                    actualClass = Int::class,
                    targetClass = String::class,
                )
            ),
            M2::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = M2::class,
                    actualClass = Float::class,
                    targetClass = String::class,
                )
            ),
            M3::class to Ok(M3::class),
            M4::class to Err(InvalidClassRandomizerReason.IsAbstract(M4::class)),
        )


        for ((randomizer, expectation) in testMap) {
            processor.checkValidClassRandomizer(
                targetClass = String::class,
                randomizerClass = randomizer
            ) shouldBe expectation
        }

    }

    /**
     * Test if [RandomizerChecker] can identify randomizer that can generate instance of children classes of a parent class
     */
    @Test
    fun getValidClassRandomizer_childrenClass_of_parent_class() {

        val processor = RandomizerChecker()

        val testMap = mapOf(
            MA1::class to Ok(MA1::class),
            MA2::class to Ok(MA2::class),
            MA3::class to Ok(MA3::class),
            M1::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = M1::class,
                    actualClass = Int::class,
                    targetClass = A1::class,
                )
            ),
        )

        val parentClass = RDClassData.from<A1>()


        for ((subject, expectation) in testMap) {
            processor.checkValidClassRandomizer(
                targetClassData = parentClass,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }

    @Test
    fun getValidClassRandomizer_childrenClass_of_parent_interface() {

        val processor = RandomizerChecker()

        val testMap = mapOf(
            MA1::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = MA1::class,
                    actualClass = A1::class,
                    targetClass = IA::class,
                )
            ),
            MA2::class to Ok(MA2::class),
            MA3::class to Ok(MA3::class),
            M1::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = M1::class,
                    actualClass = Int::class,
                    targetClass = IA::class,
                )
            ),
        )

        val parentClass = RDClassData.from<IA>()


        for ((subject, expectation) in testMap) {
            processor.checkValidClassRandomizer(
                targetClassData = parentClass,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }


    /**
     * Test if [RandomizerChecker] can recognize randomizers that can generate parameter child types of a parent class.
     */
    @Test
    fun getValidParamRandomizer_child_type_of_parent_class() {

        class Target2<T>(
            val i: Number,
            val t: T,
        )

        val target = Target2::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RandomizerChecker()
        val parentClassData = RDClassData.from<Target2<Int>>()

        val testMap = mapOf(
            M1Pr::class to Ok(M1Pr::class),
            M2Pr::class to Ok(M2Pr::class),
            M3Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M3Pr::class,
                    targetParam = target,
                    parentClass = Target2::class,
                    actualClass = String::class,
                    targetClass = Number::class,
                )
            )
        )


        for ((subject, expectation) in testMap) {
            processor.checkValidParamRandomizer(
                parentClassData = parentClassData,
                targetParam = target,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }

    /**
     * Test if [RandomizerChecker] can recognize randomizers that can generate parameter child types of a parent interface.
     */
    @Test
    fun getValidParamRandomizer_child_type_of_parent_interface() {

        class Target2<T>(
            val i: IA,
            val t: T,
        )

        val target = Target2::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RandomizerChecker()
        val parentClassData = RDClassData.from<Target2<Int>>()

        val testMap = mapOf(
            MA1Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = MA1Pr::class,
                    targetParam = target,
                    parentClass = Target2::class,
                    actualClass = A1::class,
                    targetClass = IA::class,
                )
            ),
            MA2Pr::class to Ok(MA2Pr::class),
            MA3Pr::class to Ok(MA3Pr::class),
        )


        for ((subject, expectation) in testMap) {
            processor.checkValidParamRandomizer(
                parentClassData = parentClassData,
                targetParam = target,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }

    /**
     * Check if [RandomizerChecker] can generate instance of certain concrete type
     */
    @Test
    fun getValidParamRandomizer_concreteType() {

        class Target<T>(
            val i: String,
            val t: T,
        )

        // of i parameter in Target
        val targetType = Target::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RandomizerChecker()
        val parentDt = RDClassData.from<Target<Int>>()

        val testMap = mapOf(
            M32Pr::class to Ok(M32Pr::class),
            M1Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M1Pr::class,
                    targetParam = targetType,
                    parentClass = parentDt.kClass,
                    actualClass = Int::class,
                    targetClass = targetType.type.classifier as KClass<*>,
                )
            ),
            M2Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M2Pr::class,
                    targetParam = targetType,
                    parentClass = parentDt.kClass,
                    actualClass = Float::class,
                    targetClass = targetType.type.classifier as KClass<*>,
                )
            ),
            M3Pr::class to Ok(M3Pr::class),
            M4Pr::class to Err(
                InvalidParamRandomizerReason.IsAbstract(
                    randomizerClass = M4Pr::class,
                    targetParam = targetType,
                    parentClass = parentDt.kClass
                )
            )
        )


        for ((subject, expectation) in testMap) {
            processor.checkValidParamRandomizer(
                parentClassData = parentDt,
                targetParam = targetType,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }

    /**
     * Test if [RandomizerChecker] can generate instance for a generic parameter (that is specified as a concrete type)
     */
    @Test
    fun getValidParamRandomizer_genericType() {

        class Target<T>(
            val i: String,
            val t: T
        )

        // param t in Target, later is resolved to Int
        val target = Target::class.primaryConstructor!!.parameters.first { it.name == "t" }

        val processor = RandomizerChecker()
        val parentDt = RDClassData.from<Target<Int>>()

        val testMap = mapOf(
            M1Pr::class to Ok(M1Pr::class),
            M2Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M2Pr::class,
                    targetParam = target,
                    parentClass = parentDt.kClass,
                    actualClass = Float::class,
                    targetClass = Int::class,
                )
            ),
            M3Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M3Pr::class,
                    targetParam = target,
                    parentClass = parentDt.kClass,
                    actualClass = String::class,
                    targetClass = Int::class,
                )
            ),
            M4Pr::class to Err(
                InvalidParamRandomizerReason.IsAbstract(
                    randomizerClass = M4Pr::class,
                    targetParam = target,
                    parentClass = parentDt.kClass
                )
            )
        )


        for ((subject, expectation) in testMap) {
            processor.checkValidParamRandomizer(
                parentClassData = parentDt,
                targetParam = target,
                randomizerClass = subject
            ) shouldBe expectation
        }

    }

    interface IA
    open class A1
    open class A2 : IA, A1()
    open class A3 : IA, A2()

    open class BaseClassRandomizer<T> : ClassRandomizer<T> {
        override val returnedInstanceData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): T {
            TODO("Not yet implemented")
        }
    }

    class M1 : BaseClassRandomizer<Int>()
    class M2 : BaseClassRandomizer<Float>()
    class M3 : BaseClassRandomizer<String>()
    abstract class M4 : ClassRandomizer<String>
    class MA1 : BaseClassRandomizer<A1>()
    class MA2 : BaseClassRandomizer<A2>()
    class MA3 : BaseClassRandomizer<A3>()

    open class BaseParamRandomizer<T> : ParameterRandomizer<T> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(parameterClassData: RDClassData, parameter: KParameter, parentClassData: RDClassData): T {
            TODO("Not yet implemented")
        }
    }

    class M1Pr : BaseParamRandomizer<Int>()
    class M2Pr : BaseParamRandomizer<Float>()
    open class M3Pr : BaseParamRandomizer<String>()
    class M32Pr : M3Pr()
    abstract class M4Pr : BaseParamRandomizer<String>()
    class MA1Pr : BaseParamRandomizer<A1>()
    class MA2Pr : BaseParamRandomizer<A2>()
    class MA3Pr : BaseParamRandomizer<A3>()

}
