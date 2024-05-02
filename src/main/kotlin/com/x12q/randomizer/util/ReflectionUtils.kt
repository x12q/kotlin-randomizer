package com.x12q.randomizer.util

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVariance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType

object ReflectionUtils {

    /**
     * Check if a [KType] contains any generic type that match [targetClass] or is children of [targetClass]
     */
    fun KType.canProduceGeneric(targetClass: KClass<*>):Boolean{
        return this.arguments.map {
            // take variance into consideration
            // it.variance
            val variance = it.variance
            when(variance){
                KVariance.INVARIANT,KVariance.OUT ->{
                    it.type?.classifier
                }
                else -> null
            }
        }.any{classifier->
            (classifier as? KClass<*>)?.let{
                targetClass == it || targetClass.isSubclassOf(it)
            } ?: false
        }
    }

    /**
     * Check if a [KClass] can be assigned to any generic types within [KType].
     * Eg:
     * check if A is assignable the generic of List<A>
     */
    fun KClass<*>.isAssignableToGenericOf(kType: KType):Boolean{
        val parentTypes = this.supertypes + this.starProjectedType
        return kType.arguments.any {arg->
            parentTypes.contains(arg.type)
        }
    }

    /**
     * This function is more completed. It can check for generic assignability even at children class.
     * Such as: class ABC: List<Int>
     *     Int.isAssignableToGenericOf(ABC) -> is true
     *     The problem with this one is that, it is very slow.
     *     only need to check assignable to a specific interface, a better way is to check it directly against that interface.
     *     -> no need to lookup for parent class or anything.
     */
    fun KClass<*>.isAssignableToGenericOf(kClass:KClass<*>):Boolean{
        val allTypes = this.supertypes + this.starProjectedType
        val allArg = (kClass.supertypes.flatMap { it.arguments } + kClass.starProjectedType.arguments).map { it.type }
        return allTypes.any{
            it in allArg
        }
    }

    /**
     * check if a [KClass] can be assigned to a particular [KType]
     * Eg:
     * if class A is assignable to class B
     */
    fun KClass<*>.isAssignableTo(kType: KType):Boolean{
        val parentTypes = supertypes + starProjectedType
        return parentTypes.contains(kType)
    }

}


class A<T>{
    fun add(i:T){

    }
}


fun qwe(){
    val a = A<Number>()
    val i:Int = 1
    a.add(i)
}