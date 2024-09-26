package com.x12q.randomizer.ir_plugin.backend.utils

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.builtins.StandardNames.COLLECTIONS_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.descriptors.Modality.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getPublicSignature
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import kotlin.reflect.KClass

fun IrClass.isMap():Boolean{
    return this.hasSignature(
        getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Map")
    )
}

fun IrClass.isList():Boolean{
    return this.hasSignature(
        getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"List")
    )
}

fun IrClass.isCollection():Boolean{
    return this.hasSignature(
        getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Collection")
    )
}

fun IrClass.isIterable():Boolean{
    return this.hasSignature(
        getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Iterable")
    )
}

fun IrClass.isListAssignable():Boolean{
    return isList() || isCollection() || isIterable()
}

fun IrClass.isSet():Boolean{
    return this.hasSignature(
        getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Set")
    )
}
@Deprecated("kept just in case")
fun IrClass.isClass(clazz: KClass<*>): Boolean {
    if(this.fqNameWhenAvailable?.asString() == clazz.qualifiedName){
        return true
    }
    for (superClass in this.superTypes) {
        if (superClass.classFqName?.asString() == clazz.qualifiedName) {
            return true
        }
    }
    return false
}

fun IrClass.hasSignature(sig:IdSignature.CommonSignature): Boolean {
    return this.hasFqNameEqualToSignature2(sig)
}

fun IrClass.isAnnotatedWithRandomizable(): Boolean {
    return this.isAnnotatedWith(BaseObjects.randomizableFqName)
}

fun IrClass.isAnnotatedWith(annotationClazz: KClass<*>): Boolean {
    val rt = annotationClazz.qualifiedName?.let { annotationName ->
        this.annotations.any {
            it.isAnnotation(FqName(annotationName))
        }
    } ?: false
    return rt
}


fun IrClass.isAnnotatedWith(annotationName: FqName): Boolean {
    return this.getAnnotation(annotationName) != null
}


fun IrClass.isFinalOrOpenConcrete(): Boolean {
    return when (modality) {
        FINAL,
        OPEN -> true

        SEALED,
        ABSTRACT -> false
    }
}

fun IrClass.isAbstract(): Boolean {
    return modality == ABSTRACT
}

fun IrClass.isSealed(): Boolean {
    return modality == SEALED
}

internal fun IrClass.hasFqNameEqualToSignature2(signature: IdSignature.CommonSignature): Boolean =
    name.asString() == signature.shortName && hasTopLevelEqualFqName(
        signature.packageFqName,
        signature.declarationFqName
    )
