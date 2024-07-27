package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.runRunTest
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomizerBuilder {
    data class Qx<T1>(val i: T1?)
    data class Qx2<T1>(val i: T1)

    val withData = WithData.name
    val qx = Qx::class.qualifiedName!!
    val qx2 = Qx2::class.qualifiedName!!

    @Test
    fun `nullable generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                import $qx
                import $withData
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(AlwaysFalseRandomConfig, randomT1={it.nextInt()}))
                        putData(QxC.random<Int>(AlwaysTrueRandomConfig, randomT1={it.nextInt()}))
                    }
                }
                @Randomizable
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val l = result.runRunTest().getObjs()
                l shouldBe listOf(
                    Qx<Int>(null), Qx(AlwaysTrueRandomConfig.nextInt())
                )
            }
        }
    }
}


