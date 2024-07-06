package com.x12q.randomizer.ir_plugin.backend.transformers.utils

import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.IdSignature
import org.jetbrains.kotlin.ir.util.hasTopLevelEqualFqName


internal fun IrType.isLong2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.LONG
    }else{
        return this.isLong()
    }
}


internal fun IrType.isFloat2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.FLOAT
    }else{
        return this.isFloat()
    }
}


internal fun IrType.isDouble2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.DOUBLE
    }else{
        return this.isDouble()
    }
}


internal fun IrType.isByte2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.BYTE
    }else{
        return this.isByte()
    }
}


internal fun IrType.isChar2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.CHAR
    }else{
        return this.isChar()
    }
}
internal fun IrType.isShort2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.SHORT
    }else{
        return this.isShort()
    }
}
internal fun IrType.isString2(nullable: Boolean):Boolean{
    if(nullable){
        return this.isNullableClassType2(IdSignatureValues.string)
    }else{
        return this.isString()
    }
}

internal fun IrType.isNumber2(nullable: Boolean):Boolean{
    if(nullable){
        return this.isNullableClassType2(IdSignatureValues.number)
    }else{
        return this.isNumber()
    }
}

internal fun IrType.isUnit2(nullable: Boolean):Boolean{
    if(nullable){
        return this.isNullableClassType2(IdSignatureValues.unit)
    }else{
        return this.isUnit()
    }
}

internal fun IrType.isAny2(nullable: Boolean):Boolean{
    if(nullable){
        return this.isNullableAny()
    }else{
        return this.isAny()
    }
}

internal fun IrType.isNothing2():Boolean{
    return this.isNothing() || this.isNullableNothing()
}

internal fun IrType.isBoolean2(nullable: Boolean):Boolean{
    if(nullable){
        return getPrimitiveType() == PrimitiveType.BOOLEAN
    }else{
        return this.isBoolean()
    }
}

internal fun IrType.isInt2(nullable:Boolean):Boolean{
    if(nullable){
        return this.getPrimitiveType() == PrimitiveType.INT
    }else{
        return this.isInt()
    }
}


internal fun IrType.isNullableClassType2(signature: IdSignature.CommonSignature) = isClassType2(signature, nullable = true)

internal fun IrType.isClassType2(signature: IdSignature.CommonSignature, nullable: Boolean? = null): Boolean {
    if (this !is IrSimpleType) return false
    if (nullable != null && this.isMarkedNullable() != nullable) return false
    return signature == classifier.signature ||
            classifier.owner.let { it is IrClass && it.hasFqNameEqualToSignature2(signature) }
}

internal fun IrClass.hasFqNameEqualToSignature2(signature: IdSignature.CommonSignature): Boolean =
    name.asString() == signature.shortName && hasTopLevelEqualFqName(signature.packageFqName, signature.declarationFqName)
