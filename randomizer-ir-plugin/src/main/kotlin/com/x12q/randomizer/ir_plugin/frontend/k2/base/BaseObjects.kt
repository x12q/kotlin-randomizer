package com.x12q.randomizer.ir_plugin.frontend.k2.base

import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object BaseObjects {
    val RANDOMIZABLE_NAME =/* Randomizable::class.qualifiedName ?:*/ "com.x12q.randomizer.annotations.Randomizable"
    val randomizableFqName = FqName(RANDOMIZABLE_NAME)
    val randomizableName = Name.identifier(RANDOMIZABLE_NAME)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    val declarationOrigin = RandomizerDeclarationOrigin
    val randomizableDeclarationKey = RandomizableDeclarationKey

    val randomFunctionName = Name.identifier("random")

    object Fir {
        val randomizableDeclarationKey = BaseObjects.randomizableDeclarationKey
        val firDeclarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
    }
}
