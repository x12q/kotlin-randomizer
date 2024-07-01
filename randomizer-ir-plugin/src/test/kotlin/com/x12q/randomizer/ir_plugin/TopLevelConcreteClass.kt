package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.functions
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TopLevelConcreteClass {
    @Test
    fun `random functions exist`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                }

                @Randomizable(
                    randomConfig = DefaultRandomConfig::class
                )
                data class Q123(val i:Int)
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

    @Test
    fun `empty class with a defined RandomConfig object`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random(DefaultRandomConfig))
                    println(Q123.random())
                }
                @Randomizable(
                    randomConfig = DefaultRandomConfig::class
                )
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString() == "Q123") {

                }
            }
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `empty class with default RandomConfig`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random(DefaultRandomConfig))
                    println(Q123.random())
                }
                @Randomizable
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }



    @Test
    fun `empty class with custom legal random config class`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.LegalRandomConfig

                fun main(){
                    println(Q123.random(LegalRandomConfig()))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `empty class with custom legal random config object`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.LegalRandomConfigObject

                fun main(){
                    println(Q123.random(LegalRandomConfigObject))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }


    @Test
    fun `empty class with custom illegal random config`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.IllegalRandomConfig

                fun main(){
                    println(Q123.random())
                }
                @Randomizable(randomConfig = IllegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
        }
    }



    @Test
    fun qwe(){
        val z = DefaultRandomConfig::class
        z.objectInstance shouldBe null
    }
}
