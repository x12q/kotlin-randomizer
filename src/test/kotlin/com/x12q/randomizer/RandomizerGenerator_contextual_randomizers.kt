package com.x12q.randomizer

import com.x12q.randomizer.randomizer.builder.randomizers
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.*

class RandomizerGenerator_contextual_randomizers {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    sealed class SealA {
        data class A1<T>(val t: T) : SealA()
    }

    @Test
    fun `contextual randomizer on generic sealed class`() {
        shouldThrow<Throwable> {
            // because type T is unknown -> exception is thrown
            rdm.random(RDClassData.from<SealA>())
        }
        random<SealA>(
            randomizers = randomizers {
                randomizerForClass<SealA.A1<Int>>()
                int {
                    123
                }
            }
        ) shouldBe SealA.A1(123)
    }

    data class B<T>(val t: T)

    @Test
    fun `contextual randomizer on generic class`() {

        random<B<Any>>(
            randomizers = randomizers {
                randomizerForClass<B<Int>>()
            }
        ).shouldBeInstanceOf<B<Int>>()

        random<B<Any>>(
            randomizers = randomizers {
                randomizerForClass<B<Int>>()
                int{123}
            }
        ) shouldBe B(123)

    }

}
