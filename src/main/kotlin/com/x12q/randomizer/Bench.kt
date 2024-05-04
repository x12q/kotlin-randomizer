package com.x12q.randomizer

import com.x12q.randomizer.randomizer.primitive.floatRandomizer
import com.x12q.randomizer.randomizer.primitive.intParamRandomizer
import com.x12q.randomizer.randomizer.primitive.intRandomizer
import com.x12q.randomizer.randomizer.primitive.stringRandomizer
import kotlinx.serialization.Serializable


@Serializable
data class ABC(val lst: List<Float>, val tm12: Int)
data class ABC2(val a: ABC, val t: String, val b: ABC, val t2: String)
data class Q<T>(val t: T)

enum class EN {
    t1, t2, t3, t4, t5, t6
}

sealed class SC {
    object C1 : SC()
    object C2 : SC()
    class C3() : SC()
}


fun main() {

    println(makeRandomInstance<ABC2>(
        randomizers = listOf(
            intRandomizer {
                (1..10).random()
            },
            floatRandomizer {
                1.0f
            },
            stringRandomizer {
                "abc"
            }
        ),
        paramRandomizers = listOf(
            intParamRandomizer(
                {
                    it.kParam.name == "tm12"
                },
                {
                    -100
                }
            )
        )
    ))
//    println(makeRandomInstanceForInnerClass<Q2.I1>(Q2()).i)

//    repeat(10){
//        println(makeRandomInstance<Q<SC>>(
//            randomizers = listOf(
//            ),
//            paramRandomizers = listOf()
//
//        ))
//    }


//    println(tc.isSubclassOf(Enum::class))
//    println(tc.sealedSubclasses)


//    println(makeRandomInstance<Q<EN>>(
//        randomizers = listOf(),
//        paramRandomizers = listOf()
//    ))


//    println(makeRandomInstance<ABC2>(
//        randomizers = listOf(
//            classRandomizer(
//                condition = {rd->
//                    rd == RDClassData.from<ABC>()
//                },
//                makeRandomIfApplicable = {
//                    ABC(
//                        lst = listOf(1f,2f),
//                        tm12 = 22,
//                    )
//                }
//            )
//        ),
//        paramRandomizers = listOf(
//            paramRandomizer(
//                condition = {pr->
//                    pr.kParam.type.classifier == String::class && pr.parentClass.kClass == ABC2::class
//                },
//                random = { pr->
//                    "${pr.kParam.name} : ${UUID.randomUUID()}"
//                }
//            )
//        )
//    ))


//    val comp = DaggerRDComponent.builder().build()
//    println(comp.random().nextInt())
////    val abc = makeRandomInstance<ABC>()
////    println(abc)
////    val q=
////        makeRandomInstance<Q<Int>>()
////    println(q)
////
////    println(makeRandomInstance<ABC2>())
//    makeRandomInstance<ABC>(
//        randomizers = listOf(
//            randomizer<Int>(
//                condition = {true},
//                makeRandomIfApplicable = {
//                    123
//                }
//            ) ,
//        ),
//        paramRandomizers = listOf(
//            paramRandomizer<Int>(
//                condition = { paramInfo ->
//                    val clazzData = paramInfo.paramClass
//                    val kParam: KParameter = paramInfo.kParam
//                    val parentClass: RDClassData = paramInfo.parentClass
//                    parentClass.kClass == ABC::class && kParam.name == "tm12"
//                },
//                makeRandomIfApplicable = { paramInfo ->
//                    123
//                }
//            ),
//        )
//    )
}

