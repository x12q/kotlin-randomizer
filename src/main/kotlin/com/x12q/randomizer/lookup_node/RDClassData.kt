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
) : TypeGetter {

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
                if (kclass != null) {
                    RDClassData(kclass, parameterKType)
                } else {
                    null
                }
            }
            rt
        } else {
            null
        }
        return immediateRt
    }


    fun makeConjunctionProvideMap(outerTypeMap: Map<String, KClass<*>>): Map<String, KClass<*>> {
        val direct = this.directProvideMap
        val indirect = this.makeIndirectProvideMap(outerTypeMap)
        return direct+indirect
    }
    /**
     * Make a matching between internal provide type and [outerTypeMap]
     */
    fun makeIndirectProvideMap(outerTypeMap: Map<String, KClass<*>>): Map<String, KClass<*>> {
        // receive
        val receivedTypes = kType?.arguments

        val indexToTypename = receivedTypes
            ?.map { (it.type?.classifier as? KTypeParameter)?.name }
            ?.withIndex()
            ?.mapNotNull { (index, name) ->
                name?.let { index to name }
            }?.toMap() ?: emptyMap()


        val m2: Map<Int, KClass<*>> = indexToTypename.mapNotNull { (index, name) ->
            val type = outerTypeMap[name]
            if (type != null) {
                index to type
            } else {
                null
            }
        }.toMap()

        val provide = kClass.typeParameters.map { it.name }
        val indirect = provide.withIndex().mapNotNull { (index, name) ->
            val t = m2[index]
            if (t != null) {
                name to t
            } else {
                null
            }
        }.toMap()

        return indirect
    }

    val directProvideMap: Map<String, KClass<*>> by lazy {
        val inner1Arguments = kType?.arguments
        // provide
        val inner1TypeParams = kClass.typeParameters.map { it.name }
        val rt: Map<String, KClass<*>> = inner1TypeParams.withIndex().mapNotNull { (i, name) ->
            // the cast here is because only care about received KClass, ignore everything else
            val respective = inner1Arguments?.get(i)?.type?.classifier as? KClass<*>
            respective?.let {
                name to it
            }
        }.toMap()
        rt
    }

    fun getKClassFor(typeParam: KTypeParameter): KClass<*>? {
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
