package com.x12q.randomizer.annotation_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
import com.x12q.randomizer.annotation_processor.param.InvalidParamRandomizerReason
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.test.BeforeTest
import kotlin.test.Test

class RdAnnotationProcessorTest : TestAnnotation() {

    lateinit var processor: RdAnnotationProcessor

    open class A1

    class A2 : A1()

    @BeforeTest
    fun bt() {
        processor = RdAnnotationProcessor()
    }

    @Test
    fun getValidClassRandomizer() {
        val processor = RdAnnotationProcessor()

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

        test {
            for ((subject, expectation) in testMap) {
                processor.getValidClassRandomizer(
                    RDClassData.from<String>(), subject
                ) shouldBe expectation
            }
        }
    }

    @Test
    fun getValidClassRandomizer_childrenClass() {

        val processor = RdAnnotationProcessor()

        val testMap = mapOf(
            MA1::class to Ok(MA1::class),
            MA2::class to Ok(MA2::class),
            M1::class to Err(
                InvalidClassRandomizerReason.UnableToGenerateTargetType(
                    rmdClass = M1::class,
                    actualClass = Int::class,
                    targetClass = A1::class,
                )
            ),
        )

        test {
            for ((subject, expectation) in testMap) {
                processor.getValidClassRandomizer(
                    targetClassData = RDClassData.from<A1>(),
                    randomizerClass = subject
                ) shouldBe expectation
            }
        }
    }


    /**
     * This test check if the processor can recognize randomizers that can generate child types of a parent type.
     */
    @Test
    fun getValidParamRandomizer_child_type() {

        class Target2<T>(
            val i: Number,
            val t: T,
        )

        val target = Target2::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RdAnnotationProcessor()
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

        test {
            for ((subject, expectation) in testMap) {
                processor.getValidParamRandomizer(
                    parentClassData = parentClassData,
                    targetKParam = target,
                    randomizerClass = subject
                ) shouldBe expectation
            }
        }
    }


    /**
     * This test verify if the processor can recognize randomizers that can generate certain concrete type
     */
    @Test
    fun getValidParamRandomizer_concreteType() {

        class Target<T>(
            val i: String,
            val t: T,
        )

        val iParamType = Target::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RdAnnotationProcessor()
        val parentDt = RDClassData.from<Target<Int>>()

        val testMap = mapOf(
            M32Pr::class to Ok(M32Pr::class),
            M1Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M1Pr::class,
                    targetParam = iParamType,
                    parentClass = parentDt.kClass,
                    actualClass = Int::class,
                    targetClass = iParamType.type.classifier as KClass<*>,
                )
            ),
            M2Pr::class to Err(
                InvalidParamRandomizerReason.UnableToGenerateTarget(
                    randomizerClass = M2Pr::class,
                    targetParam = iParamType,
                    parentClass = parentDt.kClass,
                    actualClass = Float::class,
                    targetClass = iParamType.type.classifier as KClass<*>,
                )
            ),
            M3Pr::class to Ok(M3Pr::class),
            M4Pr::class to Err(
                InvalidParamRandomizerReason.IsAbstract(
                    randomizerClass = M4Pr::class,
                    targetParam = iParamType,
                    parentClass = parentDt.kClass
                )
            )
        )

        test {
            for ((subject, expectation) in testMap) {
                processor.getValidParamRandomizer(
                    parentClassData = parentDt,
                    targetKParam = iParamType,
                    randomizerClass = subject
                ) shouldBe expectation
            }
        }
    }

    /**
     * Test if the annotation processor can recognize the correct randomizer for generic type
     */
    @Test
    fun getValidParamRandomizer_genericType() {

        class Target<T>(
            val i: String,
            val t: T
        )

        val target = Target::class.primaryConstructor!!.parameters.first { it.name == "t" }

        val processor = RdAnnotationProcessor()
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

        test {
            for ((subject, expectation) in testMap) {
                processor.getValidParamRandomizer(
                    parentClassData = parentDt,
                    targetKParam = target,
                    randomizerClass = subject
                ) shouldBe expectation
            }
        }
    }


    class M1Pr : ParameterRandomizer<Int> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun randomRs(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Result<Int, ErrorReport> {
            TODO("Not yet implemented")
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Int? {
            TODO("Not yet implemented")
        }
    }

    class M2Pr : ParameterRandomizer<Float> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun randomRs(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Result<Float, ErrorReport> {
            TODO("Not yet implemented")
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Float? {
            TODO("Not yet implemented")
        }

    }

    open class M3Pr : ParameterRandomizer<String> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun randomRs(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Result<String, ErrorReport> {
            TODO("Not yet implemented")
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): String? {
            TODO("Not yet implemented")
        }


    }

    class M32Pr : M3Pr()

    abstract class M4Pr : ParameterRandomizer<String>


    class M1 : ClassRandomizer<Int> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): Int {
            TODO("Not yet implemented")
        }

    }

    class M2 : ClassRandomizer<Float> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): Float {
            TODO("Not yet implemented")
        }

    }

    class M3 : ClassRandomizer<String> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): String {
            TODO("Not yet implemented")
        }

    }

    abstract class M4 : ClassRandomizer<String>

    class MA1 : ClassRandomizer<A1> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): A2 {
            TODO("Not yet implemented")
        }

    }

    class MA2 : ClassRandomizer<A2> {
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): A2 {
            TODO("Not yet implemented")
        }

    }


}
