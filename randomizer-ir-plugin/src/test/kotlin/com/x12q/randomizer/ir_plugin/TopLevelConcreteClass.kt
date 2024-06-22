package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.backend.common.serialization.proto.IrType
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TopLevelConcreteClass {
    @Test
    fun `empty class`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                package somepackage.abc
                import com.x12q.randomizer.annotations.Randomizable
                fun main(){
                    println(Q123.random())
                }
                @Randomizable
                class Q123
            """,
        ) {
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString().contains("Q123")) {
                    val companionObj = irClass.companionObject()
                    companionObj.shouldNotBeNull()

//                    val randomFunction = companionObj.functions.firstOrNull {
//                        it.name == BaseObjects.randomFunctionName
//                    }
//
//                    randomFunction.shouldNotBeNull()
//                    randomFunction.returnType.classFqName.toString() shouldBe "somepackage.abc.Q123"
//                    randomFunction.body.shouldNotBeNull()


                    val randomFunction2 = companionObj.functions.firstOrNull {
                        it.name == BaseObjects.randomFunctionName2
                    }
                    randomFunction2.shouldNotBeNull()

                }
            }
            testCompilation = {
                it.exitCode shouldBe KotlinCompilation.ExitCode.OK
            }
        }
    }
}

