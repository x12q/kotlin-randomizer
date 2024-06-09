package com.x12q.randomizer.ir_plugin.frontend.k2.base

import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

object BaseObjects {
    val randomizableFqName = FqName(Randomizable::class.qualifiedName!!)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)
    val randomizableName = Name.identifier(Randomizable::class.qualifiedName!!)
    val irDeclarationOrigin = RandomizerDeclarationOrigin

    val randomFunctionName = Name.identifier("randomFrom_IR")
    object Fir {
        val randomizableDeclarationKey = RandomizableDeclarationKey
        val declarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
        val packageFqName = FqName("com.x12q.randomizer")


        //    val randomizableAnnotation = FqName("com.x12q.randomizer.annotations.Randomizable")

    }
}
