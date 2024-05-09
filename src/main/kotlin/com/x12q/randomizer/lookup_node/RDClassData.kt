package com.x12q.randomizer.lookup_node

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.typeOf


/**
 * Class meta data available at runtime
 */
data class RDClassData(
    val kClass: KClass<*>,
    val kType: KType?,
): TypeGetter {

    /**
     * Query [RDClassData] for a particular [typeParam].
     * Param is matched by name.
     */
    override fun getDataFor(typeParam: KTypeParameter): RDClassData? {
        val typeParameterName = typeParam.name
        val can = kClass.typeParameters
        val typeParameterIndex = can.indexOfFirst { it.name == typeParameterName }
        val args = kType?.arguments
        val immediateRt = if (typeParameterIndex >= 0) {
            val parameterKType = args?.get(typeParameterIndex)?.type
            val rt = parameterKType?.let {
                val kclass = parameterKType.classifier as? KClass<*>
                if(kclass!=null){
                    RDClassData(kclass, parameterKType)
                }else{
                    null
                }
            }
            rt
        } else {
            null
        }
        return immediateRt
    }

    fun getKClassFor(typeParam: KTypeParameter):KClass<*>?{
        val typeParameterName = typeParam.name
        val typeParameterIndex = kClass.typeParameters.indexOfFirst { it.name == typeParameterName }
        if (typeParameterIndex >= 0) {
            val parameterKType = kType?.arguments?.get(typeParameterIndex)?.type
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
    }
}
