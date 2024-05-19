package com.x12q.randomizer

import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_GetLv3 {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    @Randomizer
    class A @Randomizer constructor(val i: Int, val str: String, val l: List<Float>) {
        @Randomizer(randomizer = R1::class)
        constructor(i: Int) : this(i, "str${i}", emptyList())
        @Randomizer
        constructor(str: String) : this(0, str, listOf(str.length.toFloat()))
    }

    @Test
    fun `pickConstructor on annotated constructor but with wrong type`() {
        shouldThrow<Throwable> {
            rdm.getLv3Randomizer(A::class)
        }
    }

    @Randomizer
    class B @Randomizer constructor(val i: Int, val str: String, val l: List<Float>) {
        @Randomizer(B.RB1::class)
        constructor(i: Int) : this(i, "str${i}", emptyList())
        @Randomizer
        constructor(str: String) : this(0, str, listOf(str.length.toFloat()))

        abstract class RB0:ClassRandomizer<B>{
            override val returnedInstanceData: RDClassData
                get() = TODO("Not yet implemented")

            override fun isApplicableTo(classData: RDClassData): Boolean {
                TODO("Not yet implemented")
            }

            override fun random(): B {
                TODO("Not yet implemented")
            }
        }
        class RB1: RB0()
    }

    @Test
    fun `pickConstructor on annotated class`() {
        rdm.getLv3Randomizer(B::class).shouldBeInstanceOf<B.RB1>()
    }



    class C(val i: Int, val str: String, val b: Boolean) {
        constructor(i:Int):this(i,"str1",false)
        constructor(str:String):this(100,str,true)
    }

    @Test
    fun `getLv3Randomizer with no constructor`() {
        rdm.getLv3Randomizer(C::class).shouldBeNull()
    }

    @Randomizer
    class C2 @Randomizer constructor(val i: Int, val str: String, val b: Boolean) {
        @Randomizer
        constructor(i:Int):this(i,"str1",false)
        @Randomizer
        constructor(str:String):this(100,str,true)
    }

    @Test
    fun `random with annotated primary constructor`() {
        rdm.getLv3Randomizer(C2::class).shouldBeNull()
    }

    abstract class R0:ClassRandomizer<Int>{
        override val returnedInstanceData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): Int {
            TODO("Not yet implemented")
        }
    }

    class R1: R0()

}
