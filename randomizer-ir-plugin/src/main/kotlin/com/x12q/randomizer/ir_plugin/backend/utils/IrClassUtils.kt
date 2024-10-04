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


private val mapSig by lazy{
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Map")
}
fun IrClass.isMap():Boolean{
    return this.hasSignature(mapSig)
}

private val listSig by lazy{
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"List")
}

fun IrClass.isList():Boolean{
    return this.hasSignature(listSig)
}

private val collectionSig by lazy{
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Collection")
}

fun IrClass.isCollection():Boolean{
    return this.hasSignature(collectionSig)
}

private val iterableSig by lazy{
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Iterable")
}
fun IrClass.isIterable():Boolean{
    return this.hasSignature(iterableSig)
}


private val ktArrayListSig by lazy {
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"ArrayList")
}
private val jvmArrayListSig by lazy{
    getPublicSignature(FqName("java.util"),"ArrayList")
}

fun IrClass.isArrayList():Boolean{
    return this.hasSignature(ktArrayListSig) || this.hasSignature(jvmArrayListSig)
}

fun IrClass.isListAssignable():Boolean{
    return isList() || isCollection() || isIterable()
}

private val setSig by lazy{
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"Set")
}
fun IrClass.isSet():Boolean{
    return this.hasSignature(setSig)
}

private val ktHashSetSig by lazy {
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"HashSet")
}

private val jvmHashSetSig by lazy{
    getPublicSignature(FqName("java.util"),"HashSet")
}

fun IrClass.isHashSet():Boolean{
    return this.hasSignature(ktHashSetSig) || this.hasSignature(jvmHashSetSig)
}

private val ktLinkedHashSetSig by lazy {
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"LinkedHashSet")
}
private val jvmLinkedHashSetSig by lazy{
    getPublicSignature(FqName("java.util"),"LinkedHashSet")
}
fun IrClass.isLinkedHashSet():Boolean{
    return this.hasSignature(ktLinkedHashSetSig) || this.hasSignature(jvmLinkedHashSetSig)
}


private val ktLinkedHashMapSig by lazy {
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"LinkedHashMap")
}
private val jvmLinkedHashMapSig by lazy{
    getPublicSignature(FqName("java.util"),"LinkedHashMap")
}
fun IrClass.isLinkedHashMap():Boolean{
    return this.hasSignature(ktLinkedHashMapSig) || this.hasSignature(jvmLinkedHashMapSig)
}


private val ktHashMapSig by lazy {
    getPublicSignature(COLLECTIONS_PACKAGE_FQ_NAME,"HashMap")
}
private val jvmHashMapSig by lazy{
    getPublicSignature(FqName("java.util"),"HashMap")
}
fun IrClass.isHashMap():Boolean{
    return this.hasSignature(ktHashMapSig) || this.hasSignature(jvmHashMapSig)
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
