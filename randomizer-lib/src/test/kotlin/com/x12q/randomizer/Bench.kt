package com.x12q.randomizer


import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.builder.paramRandomizers
import com.x12q.randomizer.randomizer.builder.randomizers
import com.x12q.randomizer.randomizer.clazz.BaseClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.param.BaseParameterRandomizer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.jvm.functions.FunctionN
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.internal.impl.builtins.functions.FunctionTypeKind
import kotlin.reflect.jvm.reflect

@Serializable
class AX1(val i:UInt)

fun qwewqe(){
    AX1.serializer()
}










data class Q1<K, V>(val l: Map<K, V>)
data class Q2<T>(val l: List<T>)
data class Q3<T>(val q2: Q2<T>, val l2: List<T>)
data class Q4<T>(val q3: Q3<T>)
data class A(val d: Double, val str: String)
data class Q5<E>(val q1: Q1<Int, E>)


data class Inner0<I0_1, I0_2>(
    val t1: I0_1,
    val t2: I0_2
)

data class Inner1<I1_1, I1_2, I1_3>(
    val inner0: Inner0<I1_2, I1_3>,
)

data class Q6<Q6_1, Q6_2>(
    val l: Inner1<Q6_1, Double, Q6_2>
)

sealed class SealB {
    data class B1<T>(val t: T) : SealB()
}

class MF:Function0<Int>{
    override fun invoke():Int {
        return 123
    }
}

class Q:FunctionN<Int>{
    override val arity: Int
        get() = 0

    override fun invoke(vararg args: Any?): Int {
        return 321
    }

}
@Serializable
class A123(
    val f:(String,Float)->Int
)
data class B2(
    @RandomIntWithin(123,123)
    val f:Float
)
class ABC
class MyABCRandomizerClass : BaseClassRandomizer<ABC>() {
    override val returnedInstanceData: RDClassData = RDClassData.from<ABC>()

    override fun random(): ABC {
        return ABC()
    }
}

class QWE
class QWE_Randomizer:BaseParameterRandomizer<QWE>(){
    override val paramClassData: RDClassData = RDClassData.from<QWE>()

    override fun random(parameterClassData: RDClassData, parameter: KParameter, enclosingClassData: RDClassData): QWE? {
        TODO()
    }
}

@Serializable
data class BN<T>(
    val uInt: Int,
    val t:T,
)
fun main() {

}
