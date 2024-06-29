package com.x12q.randomizer.ir_plugin.base

import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object BaseObjects {

    val RANDOMIZABLE_NAME =/* Randomizable::class.qualifiedName ?:*/ "com.x12q.randomizer.annotations.Randomizable"
    val packageName = "com.x12q.randomizer"
    val randomizableFqName = FqName(RANDOMIZABLE_NAME)
    val randomizableName = Name.identifier(RANDOMIZABLE_NAME)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    val declarationOrigin = RandomizerDeclarationOrigin
    val randomizableDeclarationKey = RandomizableDeclarationKey

    val randomFunctionName = Name.identifier("random")
    val randomConfigParamName= Name.identifier("randomConfig")

    val getRandomConfigFromAnnotationFunctionName = Name.identifier("getRandomConfig")

    object Fir {
        val randomizableDeclarationKey = BaseObjects.randomizableDeclarationKey
        val firDeclarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
        val randomConfigClassId = ClassId(FqName(packageName), Name.identifier("RandomConfig"))

    }
}
