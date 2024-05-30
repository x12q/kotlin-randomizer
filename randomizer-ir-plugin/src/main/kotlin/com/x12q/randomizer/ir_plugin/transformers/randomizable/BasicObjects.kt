package com.x12q.randomizer.ir_plugin.transformers.randomizable

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BasicObjects {
    val randomizerDeclarationOrigin = RandomizerDeclarationOrigin
    val randomFunctionName = Name.identifier("random")
    val packageFqName = FqName("com.x12q.randomizer")
    val randomizableAnnotation = FqName("com.x12q.randomizer.annotations.Randomizable")
}
