package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test




@OptIn(ExperimentalCompilerApi::class)
class TestRandomFunction {

    data class Q123(
        val b1:B1?,
        val c2:C2?,
        val d2:D2?,
    )

    data object B1{
        val i = 123
    }

    object C2{
        val c = "ccc"
    }
    data class D2(val x:Int)

    data class Q4(
        val boolean: Boolean,
        val b1:B1,
        val c2:C2,
    )

    data class Q5(
        val boolean: Boolean,
        val enum:MyEnumClass,
    )

    enum class MyEnumClass{
        V1,V2,V3,V4,V5,V6
    }

    data class Q6(
        val a:AA
    )

    data class AA(val int:Int, val bb:BB, val c:CC)
    data class BB(val str:String,val cc:CC)
    data class CC(val aa:Float)

    val imports = TestImportsBuilder
        .stdImport
        .import(Q123::class)
        .import(Q4::class)
        .import(Q5::class)
        .import(Q6::class)
        .import(AA::class)
        .import(BB::class)
        .import(CC::class)
        .import(B1::class)
        .import(C2::class)
        .import(D2::class)
        .build()
    // putData(random<QxC<String>>(makeRandom={
    //     QxC("abc")
    // },randomConfig=AlwaysTrueRandomConfig))

    @Test
    fun `test random function 2`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<String>>(makeRandom = {QxC("zzz")},randomConfig=AlwaysTrueRandomConfig))
                        putData(random<QxC<String>>(randomConfig=AlwaysTrueRandomConfig))
                    }
                }

                data class QxC<T1:Any>(override val data:T1):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction{ testOutput->
                    testOutput.getObjs() shouldBe listOf(
                        "zzz",
                        AlwaysTrueRandomConfig.nextString()
                    )
                }
            }
        }
    }
}


