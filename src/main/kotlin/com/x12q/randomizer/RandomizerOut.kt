package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRDComponent
import com.x12q.randomizer.randomizer.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Make a random instance of [T]
 */
inline fun <reified T : Any> makeRandomInstance(
    random: Random = Random,
): T {
    val comp = DaggerRDComponent.builder()
        .setRandom(random)
        .build()
    val randomizer = comp.randomizer()
    // generate random
    val q = RDClassData.from<T>()
    return randomizer.random(q) as T
}

/**
 * Make a random instance of [T] with the option to specify [randomizers] and [paramRandomizers] that
 * can override default random logic for particular classes or constructor parameters
 */
inline fun <reified T : Any> makeRandomInstance(
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>>,
    paramRandomizers: Collection<ParameterRandomizer<*>>,
): T {
    val comp = DaggerRDComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer()

    val randomizer2 = randomizer.copy(
        lv1RandomizerCollection = randomizer.lv1RandomizerCollection
            .addParamRandomizer(*paramRandomizers.toTypedArray())
            .addRandomizers(*randomizers.toTypedArray())
    )

    // generate random
    val q = RDClassData.from<T>()
    return randomizer2.random(q) as T
}

@Serializable
data class ABC(val lst: List<Float>, val tm12: Int)
data class ABC2(val a: ABC, val t: String, val b:ABC, val t2:String)
data class Q<T>(val t: T)

fun main() {

//    println(makeRandomInstance<Pair<List<Int>,String>>())
//    println(makeRandomInstance<Map<String,Int>>())


    println(makeRandomInstance<Q<Int>>(
        randomizers = listOf(
            classRandomizer<Int>(
                condition ={
                    it == RDClassData.from<Int>()
                },
                random = {
                    12
                }
            )
        ),
        paramRandomizers = emptyList()
    ))


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

