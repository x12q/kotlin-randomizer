package com.x12q.randomizer.lookup_node

import kotlin.reflect.KTypeParameter

/**
 * Something that can find [RDClassData] for a [KTypeParameter].
 */
@Deprecated("remove this interface")
interface TypeGetter {
    fun getDataFor(typeParam: KTypeParameter): RDClassData?
}
