package com.x12q.randomizer.ir_plugin.backend.transformers.support

import org.jetbrains.kotlin.ir.declarations.IrClass

class InitMetaData(
    /**
     * mapping from [targetClass] type param -> random function type params
     */
    val initTypeMap: TypeMap,
    /**
     * target class is the type that is returned by the random function
     */
    val targetClass: IrClass,
)
