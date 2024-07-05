package com.x12q.randomizer.ir_plugin.base

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.random.Random

internal object BaseObjects {
    val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
    val RANDOMIZABLE_NAME = Randomizable::class.qualifiedName!!
    val packageName = "com.x12q.randomizer"
    val randomizableFqName = FqName(RANDOMIZABLE_NAME)
    val randomizableName = Name.identifier(RANDOMIZABLE_NAME)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    val declarationOrigin = RandomizerDeclarationOrigin
    val randomizableDeclarationKey = RandomizableDeclarationKey

    val randomFunctionName = Name.identifier("random")
    val randomConfigParamName= Name.identifier("randomConfig")
    val randomizerParamName= Name.identifier("randomizer")

    val getRandomConfigFromAnnotationFunctionName = Name.identifier("getRandomConfig")
    val defaultRandomConfigClassId = ClassId(FqName("com.x12q.randomizer"), topLevelName = Name.identifier("DefaultRandomConfig"))

    val randomConfigClassId = ClassId.topLevel(FqName(RandomConfig::class.qualifiedName!!))
    val randomClassId = ClassId.topLevel(FqName(Random::class.qualifiedName!!))

    object Std{
        val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
        val ByteArrayClassId = ClassId.topLevel(FqName(ByteArray::class.qualifiedName!!))
    }


    object Fir {
        val randomizableDeclarationKey = BaseObjects.randomizableDeclarationKey
        val firDeclarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
        val randomConfigClassId = BaseObjects.randomConfigClassId
    }
}
