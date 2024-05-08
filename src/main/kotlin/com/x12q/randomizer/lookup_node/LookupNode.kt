package com.x12q.randomizer.lookup_node

import kotlin.reflect.KTypeParameter

/**
 * Something that can find [RDClassData] for a [KTypeParameter].
 */
interface LookupNode {
    fun getDataFor(typeParam: KTypeParameter): RDClassData?
}


