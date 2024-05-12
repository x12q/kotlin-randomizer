package com.x12q.randomizer.randomizer.param

import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

class ParameterRandomGenerator1UtilsTest {


    @Test
    fun paramRandomizerTest() {
        fun condition(paramInfo: ParamInfo): Boolean {
            val kParam: KParameter = paramInfo.kParam
            val parentClass: RDClassData = paramInfo.enclosingClassData
            return parentClass.kClass == TestSamples.Class1::class && kParam.name == "tm12"
        }

        fun ifApplicable(paramInfo: ParamInfo): String {
            return "${paramInfo.kParam.name}: random value 123"
        }

        val rdm = paramRandomizer<String>(
            condition = ::condition,
            random = ::ifApplicable
        )

        val abc = RDClassData.from<TestSamples.Class1>()
        val kParam = abc.kClass.primaryConstructor!!.parameters.first { it.name == "tm12" }


        rdm.isApplicableTo(
            ParamInfo(
                paramClassData = RDClassData.from<Int>(),
                kParam = kParam,
                enclosingClassData = RDClassData.from<TestSamples.Class1>()
            )
        ) shouldBe condition(
            ParamInfo(
                paramClassData = RDClassData.from<Int>(),
                kParam = kParam,
                enclosingClassData = RDClassData.from<TestSamples.Class1>()
            )
        )


        rdm.random(
            parameterClassData = RDClassData.from<Int>(),
            parameter = kParam,
            enclosingClassData = RDClassData.from<TestSamples.Class1>()
        ) shouldBe ifApplicable(
            ParamInfo(
                paramClassData = RDClassData.from<Int>(),
                kParam = kParam,
                enclosingClassData = RDClassData.from<TestSamples.Class1>()
            )
        )
    }
}
