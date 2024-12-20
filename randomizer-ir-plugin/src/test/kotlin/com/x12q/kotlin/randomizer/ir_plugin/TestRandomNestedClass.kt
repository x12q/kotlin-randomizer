package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.test.util.WithData
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test




@OptIn(ExperimentalCompilerApi::class)
class TestRandomNestedClass {

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
        .import(QxC::class)
        .build()

    data class QxC<T1:Any>(override val data:T1):WithData
    @Test
    fun `randomize nullable nested object - always not null`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<String>>(randomConfig=AlwaysTrueRandomConfig))
                    }
                }

            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction{ testOutput->
                    testOutput.getObjs() shouldBe listOf(
                        AlwaysTrueRandomConfig.nextString()
                    )
                }
            }
        }
    }


    @Test
    fun `randomize nullable nested object - always null`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Q123>>(randomConfig=AlwaysFalseRandomConfig))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction{ testRs->
                    testRs.getObjs() shouldBe listOf(Q123(
                        b1 =  null, c2 =  null, d2 =null,
                    )
)                }
            }
        }
    }


    @Test
    fun `randomize nested object`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Q4>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction{ testRs->
                    testRs.getObjs() shouldBe listOf(
                        Q4(
                            boolean = TestRandomConfig().nextBoolean(),
                            b1 = B1,
                            c2 = C2,
                        ),
                    )
                }
            }
        }
    }

    @Test
    fun `randomize nested enum`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Q5>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Q5>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Q5>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val rdConfig = TestRandomConfig()
                result.executeRunTestFunction { testRs->
                    testRs.getObjs() shouldBe List(3){
                        rdConfig.resetRandomState()
                        Q5(rdConfig.nextBoolean(), enum = MyEnumClass.values().random(rdConfig.random))
                    }
                }
            }
        }
    }

    @Test
    fun `randomize nested concrete class`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Q6>>())
                        putData(random<QxC<Q6>>())
                        putData(random<QxC<Q6>>())
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction { testRs->
                    testRs.getObjs().forEach {
                        it.shouldBeInstanceOf<Q6>()
                    }
                }
            }
        }
    }
}


