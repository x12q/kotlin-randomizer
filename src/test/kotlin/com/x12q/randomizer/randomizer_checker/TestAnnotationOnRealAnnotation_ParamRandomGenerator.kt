package com.x12q.randomizer.randomizer_checker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.randomizer.CommonRandomizer
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test


class TestAnnotationOnRealAnnotation_ParamRandomGenerator {


    @Test
    fun `check valid param randomizer 1`() {
        val param = Class_1::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizer>().first()
        val processor = RandomizerChecker()
        val randomizerClass = annotation.randomizer as KClass<out ParameterRandomizer<*>>
        processor.checkValidParamRandomizer(
            parentClassData = RDClassData.from<Class_1>(),
            targetParam = param,
            randomizerClass = randomizerClass
        ) shouldBe Ok(randomizerClass)
    }

    @Test
    fun `check valid param randomizer 2`() {
        val param = Class_3::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizer>().first()
        val processor = RandomizerChecker()
        val randomizerClass = annotation.randomizer as KClass<out ParameterRandomizer<*>>

        processor.checkValidParamRandomizer(
            parentClassData = RDClassData.from<Class_3>(),
            targetParam = param,
            randomizerClass = randomizerClass
        ).shouldBeInstanceOf<Err<*>>()

    }

    @Test
    fun `randomizer class extracted from annotation can actually work`() {

        val param = Class_1::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizer>().first()
        val processor = RandomizerChecker()
        val randomizerClass = annotation.randomizer as KClass<out ParameterRandomizer<*>>

        val randomizer = randomizerClass.createInstance()

        randomizer.isApplicableTo(
            ParamInfo(
                paramClassData = RDClassData.from<Param1>(),
                kParam = param,
                enclosingClassData = RDClassData.from<Class_1>(),
            )
        ).shouldBeTrue()

        (randomizer.random(
            parameterClassData = RDClassData.from<Param1>(),
            parameter = param,
            enclosingClassData = RDClassData.from<Class_1>(),
        ) as Param1) shouldBe Param1.targetRs

    }


    @Test
    fun testOnRealAnnotation_default() {
        val param = Class_2::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizer>().first()
        annotation.randomizer shouldBe CommonRandomizer.__DefaultRandomizer::class
    }

    class Class_1(
        @Randomizer(
            randomizer = Randomizer_1::class
        )
        val target1: Param1
    )

    class Class_2(
        @Randomizer
        val param2: Param2
    )

    class Class_3(
        @Randomizer(
            randomizer = Randomizer_1::class
        )
        val wronglyAnnotated: Param3
    )

    data class Param1(
        val str: String,
        val num: Int
    ) {
        companion object {
            val targetRs = Param1(
                str = "str1",
                num = 123,
            )
        }
    }

    data class Param2(
        val num: Float
    ) {
        companion object {
            val fixed = Param2(123f)
        }
    }

    data class Param3(
        val str: String
    ) {
        companion object {
            val fixed = Param3("fsfdigjfg")
        }
    }



    class Randomizer_1 : ParameterRandomizer<Param1> {

        override val paramClassData: RDClassData = RDClassData.from<Param1>()

        override fun isApplicableTo(paramInfo: ParamInfo): Boolean {
            return paramInfo.paramClassData == paramClassData
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            enclosingClassData: RDClassData
        ): Param1 {
            return Param1.targetRs
        }
    }
}
