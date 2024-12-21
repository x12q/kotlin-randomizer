package com.x12q.kotlin.randomizer.ir_plugin.base

import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin

object RandomizerDeclarationOrigin: IrDeclarationOrigin {
    override val name: String = "KOTLIN_RANDOMIZER_ORIGIN"
    override val isSynthetic: Boolean = true

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else {
            if (other is IrDeclarationOrigin){
                return name == other.name
            }else{
                return false
            }
        }
    }
}
