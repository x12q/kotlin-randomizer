package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


/**
 * This test randomizing something that does not have a constructor, or constructor contains parameter that cannot be generated (such as not having constructor/ interface)
 * but at the same time, a legal randomizer is provided.
 */
@OptIn(ExperimentalCompilerApi::class)
class TestRandomizingImpossibleClass {

    interface Interface123
    data class Imp(val l:Long): Interface123

    data class ABC(
        val timestamp: Instant,
        val intf: Interface123,
        val intf2: Interface123?
    )
    data class QxC<K_Q:Any>(override val data:K_Q):WithData
    private val imports = TestImportsBuilder.stdImport
        .import(ABC::class)
        .import(QxC::class)
        .import(Interface123::class)
        .import(Imp::class)




    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test randomizing impossible class`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        val v = random<ABC>(randomizers = {
                            factory<Instant>{Instant.fromEpochMilliseconds(123)}
                            constant<Interface123>(Imp(123))
                            constant<Interface123?>(Imp(123))
                        })
                        putData(QxC(v))
                    }
                }
            """,
        ) {
            Instant.fromEpochMilliseconds(123)
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val ax = objectList.first()
                ax shouldBe ABC(
                    timestamp = Instant.fromEpochMilliseconds(123),
                    intf =Imp(123),
                    intf2 = Imp(123),
                )
            }
        }
    }
}
