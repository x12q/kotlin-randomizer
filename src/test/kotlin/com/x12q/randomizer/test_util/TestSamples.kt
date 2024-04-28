package com.x12q.randomizer.test_util

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.classRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.paramRandomizer
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

object TestSamples {

    val comp = DaggerTestComp.create()

    data class Class1(val lst: List<Float>, val tm12: String) {

        companion object {

            val dt = RDClassData.from<Class1>()

            val tm12FixedRandomizer: ParameterRandomizer<String> = paramRandomizer<String>(
                condition = { paramInfo ->
                    val clazzData = paramInfo.paramClass
                    val kParam: KParameter = paramInfo.kParam
                    val parentClass: RDClassData = paramInfo.parentClass
                    parentClass.kClass == Class1::class && kParam.name == "tm12"
                },
                random = { paramInfo ->
                    "${paramInfo.kParam.name}: random value 123}"
                },
            )

            val tm12KParam by lazy {
                Class1::class.primaryConstructor!!.parameters.first { it.name == "tm12" }
            }
        }
    }

    data class Class2(val a: Class1, val t: String) {
        companion object {
            val dt = RDClassData.from<Class2>()
            val classFixedRandomizer = classRandomizer(
                condition = {
                    it.kClass == Class2::class
                },
                random = {
                    Class2(
                        a = Class1(listOf(1.1f, 2.2f), tm12 = ""),
                        t = ""
                    )
                }
            )
        }
    }

    data class ClassWithGeneric<T>(val t: T)
}
