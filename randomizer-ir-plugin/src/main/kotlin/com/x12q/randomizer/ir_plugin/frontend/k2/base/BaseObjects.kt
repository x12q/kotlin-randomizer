package com.x12q.randomizer.ir_plugin.frontend.k2.base

import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BaseObjects {
    private const val RANDOMIZABLE_NAME = "com.x12q.randomizer.annotations.Randomizable"
    val randomizableFqName = FqName(RANDOMIZABLE_NAME)
    val randomizableName = Name.identifier(RANDOMIZABLE_NAME)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)
    val rdDeclarationOrigin = RandomizerDeclarationOrigin
    val randomFunctionName = Name.identifier("random")
    val randomizableDeclarationKey = RandomizableDeclarationKey

    object Fir {
        val randomizableDeclarationKey = BaseObjects.randomizableDeclarationKey
        val declarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
        val packageFqName = FqName("com.x12q.randomizer")


        //    val randomizableAnnotation = FqName("com.x12q.randomizer.annotations.Randomizable")

    }
}
