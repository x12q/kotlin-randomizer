package com.x12q.randomizer.ir_plugin.backend.transformers.accessor.rd_lib

import com.x12q.randomizer.ir_plugin.backend.transformers.accessor.ClassAccessor
import com.x12q.randomizer.ir_plugin.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.crashOnNull
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.isObject
import javax.inject.Inject


class DefaultRandomConfigAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(BaseObjects.DefaultRandomConfig_ClassId)
            .crashOnNull {
                "impossible, DefaultRandomConfig class must exist in the class path"
            }
    }

    val defaultRandomConfigCompanionObject by lazy {
        clzz.owner.companionObject()
            .crashOnNull {
                "impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must exist"
            }
    }

    val getDefaultRandomConfigInstance by lazy {
        if (defaultRandomConfigCompanionObject.isObject) {
            defaultRandomConfigCompanionObject.getPropertyGetter("default")
                .crashOnNull {
                    "Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must contain a \"default\" variable"
                }
        } else {
            throw IllegalArgumentException("Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must be an object")
        }
    }
}
