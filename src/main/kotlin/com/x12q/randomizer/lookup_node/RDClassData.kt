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
        return direct + indirect
    }

    fun makeConjunctionProvideMap2(outerTypeMap: Map<String, RDClassData>): Map<String, RDClassData> {
        val direct = this.directProvideMap2
        val indirect = this.makeIndirectProvideMap2(outerTypeMap)
        return direct + indirect
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

        val receivedArguments = kType?.arguments
        val provideTypeParamNames = kClass.typeParameters.map { it.name }

        val rt: Map<String, KClass<*>> = provideTypeParamNames.withIndex().mapNotNull { (i, name) ->
            // the cast here is because only care about received KClass, ignore everything else
            val respective = receivedArguments?.get(i)?.type?.classifier as? KClass<*>
            respective?.let { clzz ->
                name to clzz
            }
        }.toMap()
        rt
    }

    /**
     * a map that map name of provide type of [RDClassData].
     * This is direct because it only uses information within this object.
     */
    val directProvideMap2: Map<String, RDClassData> by lazy {

        val receivedArguments = kType?.arguments
        val provideTypeParamNames = kClass.typeParameters.map { it.name }

        val rt: Map<String, RDClassData> =
            provideTypeParamNames
                .withIndex()
                .mapNotNull { (argIndex, name) ->
                    /**
                     * Match provide type names with receive arguments.
                     */
                    val type =
                        receivedArguments?.get(argIndex)?.type // the cast here is because only care about received KClass, ignore everything else
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
     * - Get the names of all the received arguments
     * - Then find a [RDClassData] (from [outerTypeMap]) for each received arg name if it is available
     * - Then map each of the found [RDClassData] to provide type name. This is done through the index of receive args and provide types. Each received argument is for the provide type at the same index.
     */
    fun makeIndirectProvideMap2(outerTypeMap: Map<String, RDClassData>): Map<String, RDClassData> {

        val receivedArguments = kType?.arguments

        /**
         * create a map<Int,String> of names of received arguments and their indices (in the order of their appearance).
         */
        val indexToTypename: Map<Int, String> = receivedArguments
            ?.map { (it.type?.classifier as? KTypeParameter)?.name }
            ?.withIndex()
            ?.mapNotNull { (index, name) ->
                name?.let { index to name }
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

        val provideTypeNames: List<String> = kClass.typeParameters.map { it.name }

        val indirect: Map<String, RDClassData> = provideTypeNames
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
