package com.x12q.randomizer.ir_plugin.backend.transformers.utils

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Contain standard objects that never change.
 */
object Standards {
    val printlnCallId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
}
