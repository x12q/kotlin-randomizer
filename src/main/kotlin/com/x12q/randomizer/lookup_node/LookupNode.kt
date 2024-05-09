package com.x12q.randomizer.lookup_node

import kotlin.reflect.KTypeParameter

/**
 * Something that can find [RDClassData] for a [KTypeParameter].
 */
interface LookupNode {
    fun getDataFor(typeParam: KTypeParameter): RDClassData?
}


/**
 * WHere to initialize lookup node.
 * Where to pass it down.
 *
 * I have to construct a new lookup node every time I descend down into a paramter.
 * The new lookup node must encapsulate the one above it.
 */
