package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import com.x12q.randomizer.test.util.assertions.codeGenAssertions
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.MetaSerializable
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TestBench {
    @Test
    fun testGenCode() {
        testGeneratedCodeUsingStandardPlugin(
            """
                package com.x12q.randomizer.sample_app
                import com.x12q.randomizer.annotations.Randomizable
                fun main(){
                    println(Q123.random())
                }
                @Randomizable
                class Q123
            """.trimIndent(),
        ) {
            afterVisitClassNew = { irClass, statement ->
                if (irClass.name.toString().contains("Q123")) {
                    val companionObj = irClass.companionObject()
                    companionObj.shouldNotBeNull()
                    val randomFunction = companionObj.functions.firstOrNull {
                        it.name.toString() == "random"
                    }
                    randomFunction.shouldNotBeNull()
                    randomFunction.origin shouldBe IrDeclarationOrigin.GeneratedByPlugin(BaseObjects.randomizableDeclarationKey)
                }
            }
            testCompilation = {
                it.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
        }
    }
}

