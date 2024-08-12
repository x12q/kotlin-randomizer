package com.x12q.randomizer.test.util.test_code

import com.x12q.randomizer.ir_plugin.mock_objects.*
import com.x12q.randomizer.lib.*
import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.test.util.TestOutput
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.withTestOutput
import io.mockk.declaringKotlinFile
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * This class is essentially a string builder that builds import statements in string. These statements are used in test subject code to test compilation.
 */
data class TestImportsBuilder(
    val classList: List<KClass<*>>,
    val functionList: List<KFunction<*>>,
    val packages:List<String>,
    val literalImports:List<String>,
) {
    /**
     * return a simple name of a particular class that is a part of the imports statement produced by this class
     */
    fun nameOf(kClass: KClass<*>): String {
        return requireNotNull(classList.firstOrNull { it == kClass }?.simpleName) {
            "${kClass.qualifiedName} is not imported in the test code yet"
        }
    }
    /**
     * return a simple name of a particular function that is a part of the imports statement produced by this class
     */
    fun nameOf(function: KFunction<*>): String {
        return requireNotNull(functionList.firstOrNull { it == function }?.name) {
            "function ${function.name} is not imported in the test code yet"
        }
    }

    /**
     * Instruct this builder to make import statement for [packageName]
     */
    fun importPackage(packageName:String):TestImportsBuilder{
        return copy(
            packages = packages + packageName
        )
    }

    /**
     * copy import data from another builder
     */
    fun import(another: TestImportsBuilder): TestImportsBuilder {
        return copy(
            classList = this.classList + another.classList,
            functionList = this.functionList + another.functionList,
        )
    }

    /**
     * Instruct this builder to make imports for classes and functions in [classList] and [functionList]
     */
    fun import(
        classList: List<KClass<*>> = emptyList(),
        functionList: List<KFunction<*>> = emptyList(),
    ): TestImportsBuilder {
        return copy(
            classList = this.classList + classList,
            functionList = this.functionList + functionList,
        )
    }

    /**
     * Instruct this builder to make imports for classes in [classList].
     */
    fun import(
        vararg classList: KClass<*>,
    ): TestImportsBuilder {
        return copy(
            classList = this.classList + classList,
        )
    }

    /**
     * Instruct this builder to make imports for function in [functionList].
     */
    fun import(
        vararg functionList: KFunction<*>,
    ): TestImportsBuilder {
        return copy(
            functionList = this.functionList + functionList,
        )
    }


    /**
     * Generate the final imports statement
     */
    val importCode: String
        get() {
            val importStatements = classList.map { kclass ->
                makeImportStatement(kclass)
            } + functionList.map { f ->
                makeImportTopLevelFunction(f)
            } + packages.map {
                makeImportPackageStatement(it)
            } + literalImports
            return importStatements.joinToString("\n")
        }

    private fun makeImportPackageStatement(packageName: String):String{
        return "import ${packageName}.*"
    }

    private fun makeImportStatement(kClass: KClass<*>): String {
        return "import ${kClass.qualifiedName!!}"
    }

    private fun makeImportTopLevelFunction(function: KFunction<*>): String {
        return "import ${function.declaringKotlinFile.qualifiedName!!.dropLast(function.declaringKotlinFile.simpleName!!.length)}${function.name}"
    }

    override fun toString(): String {

        return importCode
    }

    companion object {
        val stdImport = TestImportsBuilder(
            classList = listOf(
                LegalRandomConfigObject2::class,
                LegalRandomConfig::class,
                FactoryClassRandomizer::class,
                ConstantClassRandomizer::class,
                RandomizerCollectionImp::class,
                RandomizerCollection::class,
                RandomContextBuilder::class,
                NonNullRandomConfig::class,
                NullRandomConfig::class,
                RandomConfigForTest::class,
                RandomConfigImp::class,
                Randomizable::class,
                LegalRandomConfigObject::class,
                AlwaysFalseRandomConfig::class,
                AlwaysTrueRandomConfig::class,
                TestOutput::class,
                WithData::class,
            ),
            functionList = listOf(
                ::withTestOutput,
                ::constantRandomizer,
            ),
            packages = listOf(
                "kotlin.collections"
            ),
            literalImports = listOf(
                "import com.x12q.randomizer.lib.random"
            )
        )
    }
}

