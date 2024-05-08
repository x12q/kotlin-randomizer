package com.x12q.randomizer.lookup_node

import kotlin.reflect.KTypeParameter

/**
 * An indirect node perform lookup by checking if the input is similar to a pre-defined [frontParam], then do the actual lookup with a completely different [backParam].
 * lookupInput -> ? is [frontParam] -> use [backParam] to lookup
 */
class IndirectLookupNode(
    val frontParam: KTypeParameter,
    val backParam: KTypeParameter,
    val backNode: LookupNode,
): LookupNode {

    private val rs by lazy {
        /**
         * Because the [backParam] + [backNode] are fixed, the lookup result is also fixed too.
         */
        backNode.getDataFor(backParam)
    }

    override fun getDataFor(typeParam: KTypeParameter): RDClassData? {
        if(typeParam == frontParam){
            return rs
        }else{
            return null
        }
    }
}
