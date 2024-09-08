package com.x12q.randomizer.ir_plugin.base

import com.x12q.randomizer.lib.*
import com.x12q.randomizer.lib.annotations.Randomizable
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.random.Random

internal object BaseObjects {
    /**
     * TODO:
     *  - add more descriptive err message when name is null, and provide suggestion (such as adding required libraries)
     */

    val packageName = "com.x12q.randomizer"

    val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
    val randomizableAnnotationName = Randomizable::class.qualifiedName!!
    val randomizableFqName = FqName(randomizableAnnotationName)
    val randomizableClassId = ClassId.topLevel(randomizableFqName)

    @Deprecated("consider before using, see if this a duplicated concept because there's already a a randomizableDeclarationKey")
    val declarationOrigin = RandomizerDeclarationOrigin

    val randomizableDeclarationKey: RandomizableDeclarationKey = RandomizableDeclarationKey

    val randomFunctionName = Name.identifier("random")
    val randomConfigParamName= Name.identifier("randomConfig")

    val getRandomConfigFromAnnotationFunctionName = Name.identifier("getRandomConfig")
    val randomizerFunctionName = Name.identifier("randomizer")

    private val defaultConfigClassFqName = FqName(RandomConfigImp::class.qualifiedName!!)
    val defaultConfigClassShortName = defaultConfigClassFqName.shortName()
    val DefaultRandomConfig_ClassId = ClassId.topLevel(defaultConfigClassFqName)

    val RandomConfig_ClassId = ClassId.topLevel(FqName(requireNotNull(RandomConfig::class.qualifiedName){
        "RandomConfig interface does not exist in the class path."
    }))

    val RandomContext_ClassId = ClassId.topLevel(FqName(requireNotNull(RandomContext::class.qualifiedName){
        "RandomContext interface does not exist in the class path."
    }))
    val Random_ClassId = ClassId.topLevel(FqName(Random::class.qualifiedName!!))
    val Function0_ClassId = ClassId(packageFqName = FqName("kotlin"), topLevelName = Name.identifier("Function0"))

    val Function1_ClassId = ClassId.topLevel(FqName(Function1::class.qualifiedName!!))
    val Function2_ClassId = ClassId.topLevel(FqName(Function2::class.qualifiedName!!))

    val RandomizerCollection_Id = ClassId.topLevel(
        FqName(
            requireNotNull(RandomizerCollection2::class.qualifiedName){
                "ClassRandomizerCollection interface does not exist in the class path"
            }
        )
    )

    val RandomContextBuilder_Id = ClassId.topLevel(
        FqName(
            requireNotNull(RandomContextBuilder::class.qualifiedName){
                "Class RandomContextBuilder interface does not exist in the class path"
            }
        )
    )

    val randomContextBuilderConfigFunctionParamName = Name.identifier("randomizers")


    object Std{
        val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
        val ByteArrayClassId = ClassId.topLevel(FqName(ByteArray::class.qualifiedName!!))
    }


    object Fir {
        val firDeclarationOrigin = FirDeclarationOrigin.Plugin(randomizableDeclarationKey)
        val randomConfigClassId = BaseObjects.RandomConfig_ClassId
    }

    object Ir{
        val statementOrigin = IrStatementOriginImpl("GENERATED_BY_RANDOMIZER_BACKEND")
    }
}
