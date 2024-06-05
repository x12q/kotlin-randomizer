package com.x12q.randomizer.ir_plugin.base

import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BaseObjects {
    val randomizerDeclarationOrigin = RandomizerDeclarationOrigin
    val randomFunctionName = Name.identifier("random")
    val packageFqName = FqName("com.x12q.randomizer")
    val companionObjName = Name.identifier("Companion")

//    val randomizableAnnotation = FqName("com.x12q.randomizer.annotations.Randomizable")
    val randomizableFqName = FqName(Randomizable::class.qualifiedName!!)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)
    val randomizableName = Name.identifier(Randomizable::class.qualifiedName!!)

}
