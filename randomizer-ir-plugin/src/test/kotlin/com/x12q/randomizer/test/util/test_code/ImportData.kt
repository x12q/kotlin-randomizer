package com.x12q.randomizer.test.util.test_code

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.mock_objects.*
import com.x12q.randomizer.lib.randomizer.ClassRandomizerCollectionBuilder
import com.x12q.randomizer.lib.randomizer.ClassRandomizerCollectionImp
import com.x12q.randomizer.lib.randomizer.ConstantClassRandomizer
import com.x12q.randomizer.lib.randomizer.FactoryClassRandomizer
import com.x12q.randomizer.test.util.TestOutput
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.withTestOutput
import io.mockk.declaringKotlinFile
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import com.x12q.randomizer.lib.randomizer.random
data class ImportData(
    val classList: List<KClass<*>>,
    val functionList: List<KFunction<*>>,
    val packages:List<String>,
    val literalImports:List<String>,
) {
    fun nameOf(kClass: KClass<*>): String {
        return requireNotNull(classList.firstOrNull { it == kClass }?.simpleName) {
            "${kClass.qualifiedName} is not imported in the test code yet"
        }
    }

    fun nameOf(function: KFunction<*>): String {
        return requireNotNull(functionList.firstOrNull { it == function }?.name) {
            "function ${function.name} is not imported in the test code yet"
        }
    }

    fun importPackage(packageName:String):ImportData{
        return copy(
            packages = packages + packageName
        )
    }

    fun import(another: ImportData): ImportData {
        return copy(
            classList = this.classList + another.classList,
            functionList = this.functionList + another.functionList,
        )
    }

    fun import(
        classList: List<KClass<*>> = emptyList(),
        functionList: List<KFunction<*>> = emptyList(),
    ): ImportData {
        return copy(
            classList = this.classList + classList,
            functionList = this.functionList + functionList,
        )
    }


    fun import(
        vararg classList: KClass<*>,
    ): ImportData {
        return copy(
            classList = this.classList + classList,
        )
    }

    fun import(
        vararg functionList: KFunction<*>,
    ): ImportData {
        return copy(
            functionList = this.functionList + functionList,
        )
    }


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
        val stdImport = ImportData(
            classList = listOf(
                FactoryClassRandomizer::class,
                ConstantClassRandomizer::class,
                ClassRandomizerCollectionImp::class,
                ClassRandomizerCollectionBuilder::class,
                NonNullRandomConfig::class,
                NullRandomConfig::class,
                RandomConfigForTest::class,
                DefaultRandomConfig::class,
                Randomizable::class,
                LegalRandomConfigObject::class,
                AlwaysFalseRandomConfig::class,
                AlwaysTrueRandomConfig::class,
                TestOutput::class,
                WithData::class,
            ),
            functionList = listOf(::withTestOutput),
            packages = listOf(
                "kotlin.collections"
            ),
            literalImports = listOf(
                "import com.x12q.randomizer.lib.randomizer.random"
            )
        )
    }
}

