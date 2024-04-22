package com.x12q.randomizer.annotation_processor

import com.github.michaelbull.result.Result
import com.x12q.randomizer.annotation_processor.clazz.InvalidClassRandomizerReason
import com.x12q.randomizer.annotation_processor.param.InvalidParamRandomizerReason
import com.x12q.randomizer.err.ErrorReport
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import com.x12q.randomizer.test.TestSamples.Class1
import com.x12q.randomizer.util.ReflectionUtils.isAssignableToGenericOf
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.reflection.beOpen
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf
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

    class MA1:ClassRandomizer<A1>{
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): A2 {
            TODO("Not yet implemented")
        }

    }
    class MA2:ClassRandomizer<A2>{
        override val paramClassData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicable(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): A2 {
            TODO("Not yet implemented")
        }

    }


    @Test
    fun getValidClassRandomizer() {
        val processor = RdAnnotationProcessor()

        val result = processor.getValidClassRandomizer(
            randomTarget = RDClassData.from<String>(),
            candidates = arrayOf(
                M1::class,
                M2::class,
                M3::class,
                M4::class,
            )
        )
        println(result)

        result.validRandomizers.shouldContainOnly(
            M3::class
        )
        result.invalidRandomizers.shouldContainOnly(
            InvalidClassRandomizerReason.IsAbstract(M4::class),
            InvalidClassRandomizerReason.WrongTargetType(
                rmdClass = M1::class,
                actualTypes = Int::class,
                expectedType = String::class,
            ),
            InvalidClassRandomizerReason.WrongTargetType(
                rmdClass = M2::class,
                actualTypes = Float::class,
                expectedType = String::class,
            ),
        )
    }

    @Test
    fun getValidClassRandomizer_childrenClass() {
        val processor = RdAnnotationProcessor()

        A1::class.isAssignableToGenericOf(MA1::class) shouldBe true

//        val result = processor.getValidClassRandomizer(
//            randomTarget = RDClassData.from<A1>(),
//            candidates = arrayOf(
////                M1::class,
//                MA1::class,
//                MA2::class
//            )
//        )
//        println(result)
//
//        result.validRandomizers.shouldContainOnly(
//            MA1::class,
//            MA2::class
//        )
//        result.invalidRandomizers.shouldContainOnly(
//            InvalidClassRandomizerReason.WrongTargetType(
//                rmdClass = M1::class,
//                actualTypes = Int::class,
//                expectedType = A1::class,
//            ),
//
//        )
    }

    @Test
    fun getValidParamRandomizer_concreteType() {

        class M1 : ParameterRandomizer<Int> {
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

        class M2 : ParameterRandomizer<Float> {
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

        class M3 : ParameterRandomizer<String> {
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

        abstract class M4 : ParameterRandomizer<String>


        class Target<T>(
            val i: String,
            val t: T,
        )

        val iParamType = Target::class.primaryConstructor!!.parameters.first { it.name == "i" }

        val processor = RdAnnotationProcessor()
        val parentDt = RDClassData.from<Target<Int>>()

        val result = processor.getValidParamRandomizer(
            parentClassData = parentDt,
            targetKParam = iParamType,
            candidates = arrayOf(
                M1::class,
                M2::class,
                M3::class,
                M4::class
            )
        )
        result.validRandomizers.shouldContainOnly(M3::class)
        result.invalidRandomizers.shouldContainOnly(
            InvalidParamRandomizerReason.IsAbstract(
                randomizerKClass = M4::class,
                targetKParam = iParamType,
                parentClass = parentDt.kClass
            ),
            InvalidParamRandomizerReason.WrongTargetType(
                randomizerKClass = M1::class,
                targetKParam = iParamType,
                parentClass = parentDt.kClass,
                actualTypes = Int::class,
                expectedType = iParamType.type.classifier as KClass<*>,
            ),
            InvalidParamRandomizerReason.WrongTargetType(
                randomizerKClass = M2::class,
                targetKParam = iParamType,
                parentClass = parentDt.kClass,
                actualTypes = Float::class,
                expectedType = iParamType.type.classifier as KClass<*>,
            )
        )
    }

    @Test
    fun getValidParamRandomizer_genericType() {

        class M1 : ParameterRandomizer<Int> {
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

        class M2 : ParameterRandomizer<Float> {
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

        class M3 : ParameterRandomizer<String> {
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

        abstract class M4 : ParameterRandomizer<String>


        class Target<T>(
            val i: String,
            val t: T
        )

        val tParamType = Target::class.primaryConstructor!!.parameters.first { it.name == "t" }

        val processor = RdAnnotationProcessor()
        val parentDt = RDClassData.from<Target<Int>>()

        val result = processor.getValidParamRandomizer(
            parentClassData = parentDt,
            targetKParam = tParamType,
            candidates = arrayOf(
                M1::class,
                M2::class,
                M3::class,
                M4::class
            )
        )
        result.validRandomizers.shouldContainOnly(M1::class)
        result.invalidRandomizers.shouldContainOnly(
            InvalidParamRandomizerReason.IsAbstract(
                randomizerKClass = M4::class,
                targetKParam = tParamType,
                parentClass = parentDt.kClass
            ),
            InvalidParamRandomizerReason.WrongTargetType(
                randomizerKClass = M3::class,
                targetKParam = tParamType,
                parentClass = parentDt.kClass,
                actualTypes = String::class,
                expectedType = Int::class,
            ),
            InvalidParamRandomizerReason.WrongTargetType(
                randomizerKClass = M2::class,
                targetKParam = tParamType,
                parentClass = parentDt.kClass,
                actualTypes = Float::class,
                expectedType = Int::class,
            )
        )
    }

}
