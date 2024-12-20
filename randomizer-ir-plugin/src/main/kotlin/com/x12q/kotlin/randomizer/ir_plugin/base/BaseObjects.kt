package com.x12q.kotlin.randomizer.ir_plugin.base

import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.random.Random

internal object BaseObjects {

    const val COM_X12Q_KOTLIN_RANDOMIZER = "com.x12q.kotlin.randomizer"

    @Deprecated("kept for reference only")
    val randomizableAnnotationName = Randomizable::class.qualifiedName!!

    @Deprecated("kept for reference only")
    val randomizableFqName = FqName(randomizableAnnotationName)

    @Deprecated("kept for reference only")
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    val declarationOrigin = RandomizerDeclarationOrigin

    val Random_ClassId = ClassId.topLevel(FqName(Random::class.qualifiedName!!))

    object Std {
        val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
    }

    /**
     * Metadata of independent random functions
     */
    object IndependentRandomFunction {
        val randomFunctionPackage = FqName("$COM_X12Q_KOTLIN_RANDOMIZER.lib")
        val randomFunctionName = Name.identifier("random")
        val fullFqName = FqName("${randomFunctionPackage}.${randomFunctionName}")
        val makeRandomParamName = Name.identifier("makeRandom")
        val randomConfigParamName = Name.identifier("randomConfig")
        val randomizersParamName = Name.identifier("randomizers")
    }
}
