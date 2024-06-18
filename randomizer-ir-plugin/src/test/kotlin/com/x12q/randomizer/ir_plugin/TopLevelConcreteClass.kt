package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.backend.common.serialization.proto.IrType
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TopLevelConcreteClass {
    @Test
    fun `empty class`() {
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
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString().contains("Q123")) {
                    val companionObj = irClass.companionObject()
                    companionObj.shouldNotBeNull()

                    val randomFunction = companionObj.functions.firstOrNull {
                        it.name.toString() == "random"
                    }

                    randomFunction.shouldNotBeNull()
                    randomFunction.returnType shouldBe irPluginContext.irBuiltIns.intType
                }
            }
            testCompilation = {
                it.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
        }
    }
}

