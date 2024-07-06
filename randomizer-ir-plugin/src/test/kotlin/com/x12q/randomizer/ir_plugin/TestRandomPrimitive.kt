package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TestRandomPrimitive {

    @Test
    fun `randomize primitive parameter with default RandomConfig`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                }

                @Randomizable
                data class Q123(
                    val int:Int,
                    val long:Long,
                    val float:Float,
                    val double:Double,
                    val byte:Byte,
                    val char:Char,
                    val short: Short,
                    val string:String,
                    val number:Number,
                )
            """,
            fileName = "main.kt"
        ) {
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString() == "Q123") {
                    val companionObj = irClass.companionObject()
                    companionObj.shouldNotBeNull()

                    val randomFunction = companionObj.functions.firstOrNull {
                        it.name == BaseObjects.randomFunctionName && it.valueParameters.size==1
                    }

                    randomFunction.shouldNotBeNull()
                    randomFunction.returnType.classFqName.toString() shouldBe "Q123"
                    randomFunction.body.shouldNotBeNull()


                    val randomFunctionWithRandomConfig = companionObj.functions.firstOrNull {
                        it.name == BaseObjects.randomFunctionName && it.valueParameters.size ==1
                    }
                    randomFunctionWithRandomConfig.shouldNotBeNull()

                }
            }
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

}
