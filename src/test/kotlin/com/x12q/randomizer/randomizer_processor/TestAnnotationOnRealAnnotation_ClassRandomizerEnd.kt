package com.x12q.randomizer.randomizer_processor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.x12q.randomizer.Randomizable
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.Randomizer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotations
import kotlin.test.Test



class TestAnnotationOnRealAnnotation_ClassRandomizerEnd {

    @Test
    fun testOnRealAnnotation_right() {
        val annotation = Target_1::class.findAnnotations(Randomizable::class).firstOrNull()!!
        val processor = RandomizerChecker()

        val clazz = annotation.randomizer as KClass<out ClassRandomizer<*>>
        processor.checkValidClassRandomizer(
            targetClass = Target_1::class,
            randomizerClass = clazz
        ) shouldBe Ok(annotation.randomizer)

        val randomizer = clazz.createInstance()
        (randomizer.random() as Target_1) shouldBe Target_1.targetRs

    }

    @Test
    fun testOnRealAnnotation_wrong() {
        val annotation = Target_3_WronglyAnnotated::class.findAnnotations(Randomizable::class).firstOrNull()!!
        val processor = RandomizerChecker()
        val clazz = annotation.randomizer as KClass<out ClassRandomizer<*>>
        processor.checkValidClassRandomizer(
            targetClass = Target_3_WronglyAnnotated::class,
            randomizerClass = clazz
        ).shouldBeInstanceOf<Err<*>>()

       val randomizer = clazz.createInstance()
        randomizer.random().shouldNotBeInstanceOf<Target_3_WronglyAnnotated>()
    }

    @Test
    fun testOnRealAnnotation_default() {
        val annotation = Target_2::class.findAnnotations(Randomizable::class).firstOrNull()!!
        annotation.randomizer shouldBe Randomizer.__DefaultRandomizer::class
    }


    @Randomizable(
        randomizer = Randomizer_1::class
    )
    data class Target_1(
        val str: String,
        val num: Int
    ) {
        companion object {
            val targetRs = Target_1(
                str = "str1",
                num = 123,
            )
        }
    }
    @Randomizable
    data class Target_2(
        val num:Float
    ){
        companion object{
            val fixed = Target_2(123f)
        }
    }
    @Randomizable(
        randomizer = Randomizer_2::class
    )
    data class Target_3_WronglyAnnotated(
        val str:String
    ){
        companion object{
            val fixed = Target_3_WronglyAnnotated("fsfdigjfg")
        }
    }

    class Randomizer_1 : ClassRandomizer<Target_1> {
        override val targetClassData: RDClassData = RDClassData.from<Target_1>()

        override fun isApplicable(classData: RDClassData): Boolean {
            return classData == targetClassData
        }

        override fun random(): Target_1 {
            return Target_1.targetRs
        }
    }

    class Randomizer_2 : ClassRandomizer<Target_2> {
        override val targetClassData: RDClassData = RDClassData.from<Target_2>()

        override fun isApplicable(classData: RDClassData): Boolean {
            return classData == targetClassData
        }

        override fun random(): Target_2{
            return Target_2.fixed
        }
    }

    class Randomizer_3 : ClassRandomizer<Target_3_WronglyAnnotated> {
        override val targetClassData: RDClassData = RDClassData.from<Target_1>()

        override fun isApplicable(classData: RDClassData): Boolean {
            return classData == targetClassData
        }

        override fun random(): Target_3_WronglyAnnotated {
            return Target_3_WronglyAnnotated.fixed
        }
    }
}
