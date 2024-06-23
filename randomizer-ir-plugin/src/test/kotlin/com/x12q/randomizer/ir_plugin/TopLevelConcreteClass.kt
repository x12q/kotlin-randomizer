package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.test.util.assertions.runMain
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
import java.lang.reflect.InvocationTargetException
import kotlin.test.Test
import kotlin.test.fail

@OptIn(ExperimentalCompilerApi::class)
class TopLevelConcreteClass {
    @Test
    fun `empty class`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.DefaultRandomConfig

                fun main(){
                    println(Q123.random(DefaultRandomConfig))
                    println(Q123.random())
                }

                @Randomizable
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString() == "Q123") {
                    val companionObj = irClass.companionObject()
                    companionObj.shouldNotBeNull()

                    val randomFunction = companionObj.functions.firstOrNull {
                        it.name == BaseObjects.randomFunctionName && it.valueParameters.isEmpty()
                    }

                    randomFunction.shouldNotBeNull()
                    randomFunction.returnType.classFqName.toString() shouldBe "Q123"
                    randomFunction.body.shouldNotBeNull()


                    val randomFunction2 = companionObj.functions.firstOrNull {
                        it.name == BaseObjects.randomFunctionName && it.valueParameters.size ==1
                    }
                    randomFunction2.shouldNotBeNull()

                }
            }
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }
}



