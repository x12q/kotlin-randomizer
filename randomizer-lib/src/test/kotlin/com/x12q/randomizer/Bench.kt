package com.x12q.randomizer


import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.builder.paramRandomizers
import com.x12q.randomizer.randomizer.builder.randomizers
import com.x12q.randomizer.randomizer.clazz.BaseClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import com.x12q.randomizer.randomizer.param.BaseParameterRandomizer
import kotlinx.serialization.Serializable
import kotlin.jvm.functions.FunctionN
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.internal.impl.builtins.functions.FunctionTypeKind
import kotlin.reflect.jvm.reflect


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
/**
 * Function0 -> 22: synthetic interface
 * KFunction2 : synthetic interface, it extends KFunction
 * KFunction: belong to reflection lib
 * What I want:
 *  check the type of a function property, then find a correct implementation for it.
 */
fun main() {


    println(random<B2>(
        randomizers = randomizers {

        },
        paramRandomizers = paramRandomizers{

        }
    ))

//    println(q.kType?.classifier)


//    random<List<Int>>().also { println(it) }

//    repeat(100){
//        random<Array<Int>>(
//            randomizers = randomizers {
//                int(5)
//            }
//        ).also {
//            println(it)
//        }
//    }
}


//
//@Serializable
//data class ABC(val lst: List<Float>, val tm12: Int)
//
//data class A2(val t2: String) {
//    companion object {
//        class A2Randomizer : BaseParamRandomizer<A2>() {
//            override val paramClassData: RDClassData = RDClassData.from<A2>()
//
//            override fun random(
//                parameterClassData: RDClassData,
//                parameter: KParameter,
//                enclosingClassData: RDClassData
//            ): A2? {
//                return A2("from custom randomizer")
//            }
//        }
//    }
//}
//
//data class ABC2(
//    val abc: ABC,
//    val str: String,
//    val abc2: ABC,
//    val str2: String,
//    @Randomizer(A2Randomizer::class)
//    val a2: A2
//)
//
//data class A3(val i: Int, val str: String) {
//
//    @Randomizer(A3.Companion.A3Randomizer::class)
//    constructor(f: Float) : this(f.toInt(), "pppp")
//
//    companion object {
//        class A3Randomizer : BaseClassRandomizer<A3>() {
//            override val returnedInstanceData: RDClassData = RDClassData.from<A3>()
//
//            override fun random(): A3 {
//                return A3(1, "-")
//            }
//        }
//    }
//}
//
//class A2Randomizer : BaseParamRandomizer<A2>() {
//    override val paramClassData: RDClassData = RDClassData.from<A2>()
//
//    override fun random(parameterClassData: RDClassData, parameter: KParameter, enclosingClassData: RDClassData): A2? {
//        return A2("from custom randomizer")
//    }
//}
//
//
//class RD1<T1>(val t1: T1)
//
//class RD2<T2_1, T2_2, T2_3>(
//    val rd1: RD1<T2_2>,
//    val d: T2_1,
//    val x: T2_3,
//)
//
//class RD3<T3_1, T3_2, T3_3, T3_4>(
//    val rd2: RD2<T3_1, T3_2, T3_4>,
//    val c: T3_3,
//)
//
//val rd3 = RDClassData.from<RD3<Double, Short, Float, Int>>()
//
//
//fun main() {
//
//
//
//
//
//
//
////    RDClassData.from<Q3<Int>>().also {
////        println(it.kClass.typeParameters)
////        println(it.kType?.arguments)
////    }
//
////    println(List::class.isSubclassOf(Iterable::class))
////    println(Iterable::class.isSuperclassOf(List::class))
////    println(List::class.isSuperclassOf(List::class))
////    println(random<ABC2>(
////        randomizers = listOf(
////            intRandomizer {
////                99
////            },
////            floatRandomizer {
////                1.0f
////            },
////            stringRandomizer {
////                "abc123"
////            },
////            listRandomizer {
////                listOf(1f, 2f)
////            }
////        ),
////        paramRandomizers = listOf(
////            stringParamRandomizer(
//////                condition = {
//////                    it.paramName == "t2" && it.parentIs<A2>()
//////                },
////                random = {
////                    "${it.paramName}:__qwe__"
////                }
////            )
////        )
////    ))
////
////    println(
////        random<ABC2>(
////            randomizers = randomizers {
////                int {
////                    99
////                }
////                float {
////                    1f
////                }
////                string {
////                    "abc123"
////                }
////                list {
////                    listOf(1f, 2f)
////                }
////                add(classRandomizer {
////
////                })
////            },
////            paramRandomizers = paramRandomizers {
//////                add(paramRandomizer {
//////                    OtherClass(123)
//////                })
//////                add(paramRandomizer(
//////                    condition = {paramInfo ->
//////                        paramInfo.paramName == "someParamName"
//////                    },
//////                    random= {
//////                        OtherClass(456)
//////                    }
//////                ))
//////                string { paramInfo->
//////                    "${paramInfo.paramName} -- some str"
//////                }
//////                int(
//////                    condition = { paramInfo->
//////                        paramInfo.paramName="age" && paramInfo.enclosingKClass == Person::class
//////                    },
//////                    random= {
//////                        Random.nextInt(1000)
//////                    }
//////                )
////            }
////        )
////    )
//}
//
