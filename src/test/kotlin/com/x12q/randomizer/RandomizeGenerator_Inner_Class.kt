package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.lookup_node.RDClassData
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_Inner_Class {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    class Q{
        inner class I1(val i: Int,)
    }

    @Test
    fun `random non-annotated inner class`(){
        val q = Q()
        rdm.randomInnerClass(RDClassData.from<Q.I1>(),q).shouldBeInstanceOf<Q.I1>()
    }

    class Q2{
        inner class I1(val i: Int,){
            @Randomizable
            constructor():this(5)

        }
    }

    @Test
    fun `random annotated inner class`(){
        val q = Q2()
        rdm.randomInnerClass(RDClassData.from<Q2.I1>(),q).also {
            it.shouldBeInstanceOf<Q2.I1>()
            it.i shouldBe 5
        }
    }

    companion object {
        val q3 = Q3()
    }

    class Q3{
        @Randomizable(randomizer = Q3I1Randomizer::class)
        inner class I1(val i: Int)

        inner class I2<T>(val i:T)

        @Randomizable(randomizer = Q3I3Randomizer::class)
        inner class I3<T>(val i:T)
    }


    class Q3I1Randomizer : ClassRandomizer<Q3.I1>{
        override val returnedInstanceData: RDClassData = RDClassData.from<Q3.I1>()

        override fun isApplicableTo(classData: RDClassData): Boolean {
            return classData == returnedInstanceData
        }
        override fun random(): Q3.I1 {
            return q3.I1(100)
        }
    }

    class Q3I3Randomizer : ClassRandomizer<Q3.I3<Int>>{
        override val returnedInstanceData: RDClassData = RDClassData.from<Q3.I3<Int>>()

        override fun isApplicableTo(classData: RDClassData): Boolean {
            return classData == returnedInstanceData
        }
        override fun random(): Q3.I3<Int> {
            return q3.I3(100)
        }
    }

    @Test
    fun `random annotated inner class with custom randomizer`(){
        rdm.randomInnerClass(RDClassData.from<Q3.I1>(),q3).also {
            it.shouldBeInstanceOf<Q3.I1>()
            it.i shouldBe Q3I1Randomizer().random().i
        }
    }

    @Test
    fun `random generic inner class`(){
        rdm.randomInnerClass(RDClassData.from<Q3.I2<Int>>(),q3).also {
            it.shouldBeInstanceOf<Q3.I2<Int>>()
        }
    }

    @Test
    fun `random annotated generic inner class`(){
        rdm.randomInnerClass(RDClassData.from<Q3.I3<Int>>(),q3).also {
            it.shouldBeInstanceOf<Q3.I3<Int>>()
            it.i shouldBe Q3I3Randomizer().random().i
        }
    }
}
