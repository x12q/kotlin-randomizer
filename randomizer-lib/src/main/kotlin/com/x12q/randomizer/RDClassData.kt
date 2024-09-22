package com.x12q.randomizer

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
) {

    /**
     * Get [RDClassData] for a particular [typeParam].
     * Param is matched by name.
     */
    fun getDataFor(typeParam: KTypeParameter): RDClassData? {
        val typeParameterName = typeParam.name
        val candidates = kClass.typeParameters
        val typeParameterIndex = candidates.indexOfFirst { it.name == typeParameterName }
        val args = kType?.arguments
        val immediateRt = if (typeParameterIndex >= 0) {
            val parameterKType = args?.get(typeParameterIndex)?.type
            val rt: RDClassData? = parameterKType?.let {
                val clzz: KClass<*>? = parameterKType.classifier as? KClass<*>
                if (clzz != null) {
                    RDClassData(clzz, parameterKType)
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

    /**
     * Merged [directTypeMap] with indirect type map created by [makeIndirectTypeMap]
     */
    fun makeCombineTypeMap(outerTypeMap: Map<String, RDClassData>): Map<String, RDClassData> {
        val direct = this.directTypeMap
        val indirect = this.makeIndirectTypeMap(outerTypeMap)
        return indirect + direct
    }

    /**
     * This is a mapping between received type arguments and declared types of this class.
     * This type map is constructed using only information with this class data.
     */
    internal val directTypeMap: Map<String, RDClassData> by lazy {

        val receivedTypes = kType?.arguments
        val declaredTypeNames = kClass.typeParameters.map { it.name }

        val rt: Map<String, RDClassData> =
            declaredTypeNames
                .withIndex()
                .mapNotNull { (argIndex, name) ->
                    /**
                     * Match provide type names with receive arguments.
                     */
                    val type = receivedTypes?.get(argIndex)?.type
                    // the cast here is because only care about received KClass, ignore everything else
                    val clzz = type?.classifier as? KClass<*>
                    if (type != null && clzz != null) {
                        Pair(name, RDClassData(clzz, type))
                    } else {
                        null
                    }
                }.toMap()
        rt
    }

    /**
     * This function does:
     * - Get the names of all the received type arguments
     * - Then find a [RDClassData] (from [outerTypeMap]) for each received arg name if it is available
     * - Then map each of the found [RDClassData] to provide type name. This is done through the index of receive args and provide types. Each received argument is for the provide type at the same index.
     */
    internal fun makeIndirectTypeMap(outerTypeMap: Map<String, RDClassData>): Map<String, RDClassData> {

        val receivedArguments = kType?.arguments

        /**
         * create a map<Int,String> of names of received arguments and their indices (in the order of their appearance).
         */
        val indexToTypename: Map<Int, String> = receivedArguments
            ?.map { (it.type?.classifier as? KTypeParameter)?.name }
            ?.withIndex()
            ?.mapNotNull { (index, name) ->
                val indexToNamePair = name?.let { index to name }
                indexToNamePair
            }?.toMap() ?: emptyMap()

        /**
         * find the [RDClassData] in [outerTypeMap], whose type name (type name = key in [outerTypeMap]) match the name of the received arguments.
         * Then map such [RDClassData] to the respective index.
         */
        val indexToRdClassData: Map<Int, RDClassData> = indexToTypename.mapNotNull { (index, name) ->
            val rd: RDClassData? = outerTypeMap[name]
            if (rd != null) {
                index to rd
            } else {
                null
            }
        }.toMap()

        val declaredTypeNames: List<String> = kClass.typeParameters.map { it.name }

        val indirect: Map<String, RDClassData> = declaredTypeNames
            .withIndex()
            .mapNotNull { (index, name) ->
                val rd: RDClassData? = indexToRdClassData[index]
                if (rd != null) {
                    name to rd
                } else {
                    null
                }
            }.toMap()

        return indirect
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
