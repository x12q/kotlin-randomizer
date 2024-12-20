package com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.rd_lib

import com.x12q.kotlin.randomizer.ir_plugin.backend.accessor.ClassAccessor
import com.x12q.kotlin.randomizer.ir_plugin.util.crashOnNull
import com.x12q.kotlin.randomizer.lib.RandomConfigImp
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import javax.inject.Inject


class DefaultRandomConfigAccessor @Inject constructor(
    val pluginContext: IrPluginContext
) : ClassAccessor() {
    private val defaultConfigClassFqName = FqName(RandomConfigImp::class.qualifiedName!!)
    val defaultConfigClassShortName = defaultConfigClassFqName.shortName()
    private val DefaultRandomConfig_ClassId = ClassId.topLevel(defaultConfigClassFqName)

    override val clzz: IrClassSymbol by lazy {
        pluginContext.referenceClass(DefaultRandomConfig_ClassId)
            .crashOnNull {
                "impossible, DefaultRandomConfig class must exist in the class path"
            }
    }

    val defaultRandomConfigCompanionObject by lazy {
        clzz.owner.companionObject()
            .crashOnNull {
                "impossible, ${defaultConfigClassShortName}.Companion must exist"
            }
    }

    val getDefaultRandomConfigInstance by lazy {
        if (defaultRandomConfigCompanionObject.isObject) {
            defaultRandomConfigCompanionObject.getPropertyGetter("default")
                .crashOnNull {
                    "Impossible, ${defaultConfigClassShortName}.Companion must contain a \"default\" variable"
                }
        } else {
            throw IllegalArgumentException("Impossible, ${defaultConfigClassShortName}.Companion must be an object")
        }
    }
}
