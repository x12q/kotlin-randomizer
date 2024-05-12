package com.x12q.randomizer.util

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Check if a class is enum or not
 */
internal fun KClass<*>.isEnum():Boolean{
    return this.isSubclassOf(Enum::class)
}

/**
 * Extract enum value from a [KClass]. For jvm only.
 */
internal fun getEnumValueJvm(clazz: KClass<*>): Array<*>? {
    if(clazz.isEnum()){
        return clazz.java.enumConstants
    }else{
        return null
    }
}

internal fun getEnumValue(clazz:KClass<*>):Array<*>?{
    return getEnumValueJvm(clazz)
}
