package com.x12q.randomizer.randomizer

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.defaultType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf


/**
 * Class data available at runtime
 */
data class RDClassData(
    val kClass: KClass<*>,
    val kType: KType
) {

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

    fun getKClassFor(kTypeParameter: KTypeParameter):KClass<*>?{
        val typeParameterName = kTypeParameter.name
        val typeParameterIndex = kClass.typeParameters.indexOfFirst { it.name == typeParameterName }
        if (typeParameterIndex >= 0) {
            val parameterKType = kType.arguments[typeParameterIndex].type
            val rt = parameterKType?.let {
                parameterKType.classifier as KClass<*>
            }
            return rt
        } else {
            return null
        }
    }

    companion object {
        inline fun <reified T> from(): RDClassData {
            return RDClassData(
                kClass = T::class,
                kType = typeOf<T>(),
            )
        }

        fun from(i:KClass<*>):RDClassData{
            return RDClassData(
                kClass = i,
                kType = i.starProjectedType
            )
        }
    }
}
