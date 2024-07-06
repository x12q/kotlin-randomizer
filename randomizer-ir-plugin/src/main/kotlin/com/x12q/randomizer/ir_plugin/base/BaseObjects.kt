package com.x12q.randomizer.ir_plugin.base

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.random.Random

internal object BaseObjects {

    val packageName = "com.x12q.randomizer"

    val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
    val randomizableAnnotationName = Randomizable::class.qualifiedName!!
    val randomizableFqName = FqName(randomizableAnnotationName)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    val declarationOrigin = RandomizerDeclarationOrigin
    val randomizableDeclarationKey = RandomizableDeclarationKey

    val randomFunctionName = Name.identifier("random")
    val randomConfigParamName= Name.identifier("randomConfig")

    val getRandomConfigFromAnnotationFunctionName = Name.identifier("getRandomConfig")


    val defaultConfigClassFqName = FqName(DefaultRandomConfig::class.qualifiedName!!)
    val defaultConfigClassShortName = defaultConfigClassFqName.shortName()
    val defaultRandomConfigClassId = ClassId.topLevel(defaultConfigClassFqName)

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
