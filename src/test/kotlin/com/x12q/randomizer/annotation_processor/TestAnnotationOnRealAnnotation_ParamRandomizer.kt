package com.x12q.randomizer.annotation_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.x12q.randomizer.Randomizable
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test



class TestAnnotationOnRealAnnotation_ParamRandomizer :TestAnnotation(){

    @Test
    fun testOnRealAnnotation_right() {
        val param = Class_1::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizable>().first()
        val processor = RandomizerProcessor()

        test("randomizer class is extracted correctly"){
            processor.getValidParamRandomizer(
                parentClassData = RDClassData.from<Class_1>(),
                targetParam = param,
                randomizerClass = annotation.paramRandomizer
            ) shouldBe Ok(annotation.paramRandomizer)
        }


        test("randomizer class can actually work"){
            val randomizer = annotation.paramRandomizer.createInstance()
            randomizer.isApplicableTo(
                parameterClassData = RDClassData.from<Param1>(),
                parameter = param,
                parentClassData = RDClassData.from<Class_1>(),
            ).shouldBeTrue()

            (randomizer.random(
                parameterClassData = RDClassData.from<Param1>(),
                parameter = param,
                parentClassData = RDClassData.from<Class_1>(),
            ) as Param1) shouldBe Param1.targetRs
        }
    }

    @Test
    fun testOnRealAnnotation_wrong() {
        val param = Class_3::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizable>().first()
        val processor = RandomizerProcessor()

        test("randomizer class is extracted correctly"){
            processor.getValidParamRandomizer(
                parentClassData = RDClassData.from<Class_3>(),
                targetParam = param,
                randomizerClass = annotation.paramRandomizer
            ).shouldBeInstanceOf<Err<*>>()
        }
    }

    @Test
    fun testOnRealAnnotation_default() {
        val param = Class_2::class.primaryConstructor!!.parameters.get(0)
        val annotation = param.findAnnotations<Randomizable>().first()
        annotation.paramRandomizer shouldBe Randomizable.Companion.__DefaultParamRandomizer::class
    }

    class Class_1(
        @Randomizable(
            paramRandomizer = Randomizer_1::class
        )
        val target1:Param1
    )

    class Class_2(
        @Randomizable
        val param2:Param2
    )

    class Class_3(
        @Randomizable(
            paramRandomizer = Randomizer_1::class
        )
        val wronglyAnnotated:Param3
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
        val num:Float
    ){
        companion object{
            val fixed = Param2(123f)
        }
    }

    data class Param3(
        val str:String
    ){
        companion object{
            val fixed = Param3("fsfdigjfg")
        }
    }

    class Randomizer_1 : ParameterRandomizer<Param1> {

        override val paramClassData: RDClassData = RDClassData.from<Param1>()

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            return parameterClassData == paramClassData
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Param1 {
            return Param1.targetRs
        }
    }

    class Randomizer_2 : ParameterRandomizer<Param2> {
        override val paramClassData: RDClassData = RDClassData.from<Param2>()

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            return parameterClassData == paramClassData
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Param2 {
            return Param2.fixed
        }
    }

    class Randomizer_3 : ParameterRandomizer<Param3> {
        override val paramClassData: RDClassData = RDClassData.from<Param3>()

        override fun isApplicableTo(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Boolean {
            return parameterClassData == paramClassData
        }

        override fun random(
            parameterClassData: RDClassData,
            parameter: KParameter,
            parentClassData: RDClassData
        ): Param3 {
            return Param3.fixed
        }
    }
}
