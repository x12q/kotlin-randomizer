package com.x12q.randomizer.lookup_node

import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

class TypeGetterImpTest {

    class RD1<T1>(val e: T1)

    class RD2<T2_1, T2_2, T2_3>(
        val t: RD1<T2_2>,
        val d: T2_1,
        val x: T2_3,
    )

    class RD3<T3_1, T3_2, T3_3, T3_4>(
        val rd2: RD2<T3_1, T3_2, T3_4>,
        val c: T3_3,
    )

    @Test
    fun `append and getDataFor` () {

        val rd3 = RDClassData.from<RD3<Double, Short, Float, Int>>()
        val finder = TypeFinderImp(rd3)

        rd3.kClass.primaryConstructor!!.parameters.also { paramListRd3 ->
            paramListRd3.forEach {
                finder.updateWith(rd3.kClass,it)
            }
            val rd2 = paramListRd3[0]

            val rd2Clazz = rd2.type.classifier as KClass<*>
            rd2Clazz.primaryConstructor!!.parameters.also { paramListRd2 ->
                paramListRd2.forEach {
                    finder.updateWith(rd2Clazz,it)
                }

                val rd1 = paramListRd2[0]
                val rd1Clazz = rd1.type.classifier as KClass<*>
                rd1Clazz.primaryConstructor!!.parameters.forEach {
                    finder.updateWith(rd1Clazz,it)
                }

                /**
                 * Test on RD1
                 */
                rd1Clazz.primaryConstructor!!.typeParameters.map {
                    finder.getDataFor(rd1Clazz,it)
                } shouldBe listOf(
                    RDClassData.from<Short>()
                )
            }
            /**
             * Test on RD2
             */
            rd2Clazz.primaryConstructor!!.typeParameters.map {
                finder.getDataFor(rd2Clazz,it)
            } shouldBe listOf(
                RDClassData.from<Double>(),
                RDClassData.from<Short>(),
                RDClassData.from<Int>(),
            )
        }
    }
}
