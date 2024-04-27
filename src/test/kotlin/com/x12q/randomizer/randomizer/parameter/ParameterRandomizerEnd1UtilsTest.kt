package com.x12q.randomizer.randomizer.parameter

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.test_util.TestSamples
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ParameterRandomizerEnd1UtilsTest {


    @Test
    fun paramRandomizer() {
        fun condition(paramInfo: ParamInfo): Boolean {
            val clazzData = paramInfo.paramClass
            val kParam: KParameter = paramInfo.kParam
            val parentClass: RDClassData = paramInfo.parentClass
            return parentClass.kClass == TestSamples.Class1::class && kParam.name == "tm12"
        }

        fun ifApplicable(paramInfo: ParamInfo): String {
            return "${paramInfo.kParam.name}: random value 123"
        }

        val rdm = com.x12q.randomizer.randomizer.paramRandomizer<String>(
            condition = ::condition,
            makeRandomIfApplicable = ::ifApplicable
        )

        val abc = RDClassData.from<TestSamples.Class1>()
        val kParam = abc.kClass.primaryConstructor!!.parameters.first { it.name == "tm12" }


        rdm.isApplicableTo(
            parameterClassData = RDClassData.from<Int>(),
            parameter = kParam,
            parentClassData = RDClassData.from<TestSamples.Class1>()
        ) shouldBe condition(
            ParamInfo(
                paramClass = RDClassData.from<Int>(),
                kParam = kParam,
                parentClass = RDClassData.from<TestSamples.Class1>()
            )
        )


        rdm.random(
            parameterClassData = RDClassData.from<Int>(),
            parameter = kParam,
            parentClassData = RDClassData.from<TestSamples.Class1>()
        ) shouldBe ifApplicable(
            ParamInfo(
                paramClass = RDClassData.from<Int>(),
                kParam = kParam,
                parentClass = RDClassData.from<TestSamples.Class1>()
            )
        )
    }
}
