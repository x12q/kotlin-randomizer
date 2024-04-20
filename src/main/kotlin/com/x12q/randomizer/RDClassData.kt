package com.x12q.randomizer

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor


/**
 * Class data available in runtime
 */
data class RDClassData(
    val kClass: KClass<*>,
    val kType: KType
) {

//    /**
//     * Find a constructor depend on some rule
//     */
//    fun findConstructorRs(constructorRule: ConstructorRule = ConstructorRule.PrimaryConstructorOrFirst): Result<KFunction<Any>, ErrorReport> {
//        // TODO implement filter base on rule
//        val classRef = kClass
//        val allConstructors = classRef.constructors
//        val constructor = classRef.primaryConstructor ?: allConstructors.firstOrNull()
//        val rt = constructor?.let {
//            Ok(it)
//        } ?: Err(RandomizerErrors.NoConstructorFound.report())
//        return rt
//    }

    /**
     * Query [RDClassData] for a particular [kTypeParameter]
     */
    fun getDataFor(kTypeParameter: KTypeParameter): RDClassData? {
        val typeParameterName = kTypeParameter.name
        val typeParameterIndex = kClass.typeParameters.indexOfFirst { it.name == typeParameterName }
        if (typeParameterIndex >= 0) {
            val parameterKType = kType.arguments[typeParameterIndex].type
            val rt = parameterKType?.let {
                val kclass = parameterKType.classifier as KClass<*>
                RDClassData(kclass, parameterKType)
            }
            return rt
        } else {
            return null
        }
    }

    companion object {
        inline fun <reified T> from(): RDClassData {
            return RDClassData(
                T::class, typeOf<T>()
            )
        }
    }
}
