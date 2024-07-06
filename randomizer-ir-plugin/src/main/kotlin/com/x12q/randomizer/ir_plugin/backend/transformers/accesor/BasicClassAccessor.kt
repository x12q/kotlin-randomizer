package com.x12q.randomizer.ir_plugin.backend.transformers.accesor

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.isObject
import javax.inject.Inject

class BasicClassAccessor @Inject constructor(
    pluginContext: IrPluginContext
) {
    val kotlinRandomClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.randomClassId)) {
            "kotlin.random.Random class is not in the class path."
        }
    }
    val randomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.randomConfigClassId)) {
            "RandomConfig interface is not in the class path."
        }
    }
    val defaultRandomConfigClass by lazy {
        requireNotNull(pluginContext.referenceClass(BaseObjects.defaultRandomConfigClassId)) {
            "impossible, DefaultRandomConfig class must exist in the class path"
        }
    }

    val defaultRandomConfigCompanionObject by lazy {
        requireNotNull(defaultRandomConfigClass.owner.companionObject()) {
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
