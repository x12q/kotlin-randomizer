package com.x12q.randomizer

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test

class RandomizeGenerator_Enum {


    lateinit var rdm: RandomGenerator

    enum class EN {
        t1, t2, t3, t4, t5, t6;
    }

    @BeforeTest
    fun bt(){
        rdm = TestSamples.comp.randomizer()
    }

    @Test
    fun `random on class`(){
        shouldNotThrow<Throwable> {
            val rs = rdm.random(RDClassData.from<EN>())
            rs.shouldNotBeNull()
        }
    }

    class Q(val e:EN)

    @Test
    fun `random on concrete param`(){
        shouldNotThrow<Throwable> {
            val rs = rdm.random(RDClassData.from<Q>())
            rs.shouldNotBeNull()
            rs.shouldBeInstanceOf<Q>()
        }
    }

    class Q2<T>(val e:T)
    @Test
    fun `random on generic param`(){
        shouldNotThrow<Throwable> {
            val rs = rdm.random(RDClassData.from<Q2<EN>>())
            rs.shouldNotBeNull()
            rs.shouldBeInstanceOf<Q2<EN>>()
        }
    }
}
