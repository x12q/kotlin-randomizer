package com.x12q.randomizer.ir_plugin.base

import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BaseObjects {
    val irDeclarationOrigin = RandomizerDeclarationOrigin
    val firDeclarationOrigin = FirDeclarationOrigin.Plugin(RandomizableDeclarationKey)
    val firRandomizableDeclarationKey=RandomizableDeclarationKey
    val randomFunctionName = Name.identifier("randomFrom_IR")
    val randomFIRFunctionName = Name.identifier("randomFrom_FIR")
    val packageFqName = FqName("com.x12q.randomizer")
    val companionObjName = Name.identifier("RDCompanion")

//    val randomizableAnnotation = FqName("com.x12q.randomizer.annotations.Randomizable")
    val randomizableFqName = FqName(Randomizable::class.qualifiedName!!)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)
    val randomizableName = Name.identifier(Randomizable::class.qualifiedName!!)

}
