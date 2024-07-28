package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
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
        requireNotNull(pluginContext.referenceClass(BaseObjects.DefaultRandomConfig_ClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    val defaultRandomConfigCompanionObject by lazy {
        requireNotNull(clzz.owner.companionObject()) {
            "impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must exist"
        }
    }

    val getDefaultRandomConfigInstance by lazy {
        if (defaultRandomConfigCompanionObject.isObject) {
            requireNotNull(defaultRandomConfigCompanionObject.getPropertyGetter("default")) {
                "Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must contain a \"default\" variable"
            }
        } else {
            throw IllegalArgumentException("Impossible, ${BaseObjects.defaultConfigClassShortName}.Companion must be an object")
        }
    }
}
