package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRDComponent
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.clazz.classRandomizer
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.isSubclassOf

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

enum class EN{
    t1, t2, t3, t4, t5, t6
}

fun main() {

    repeat(10){
        println(makeRandomInstance<Q<EN>>(
            randomizers = listOf(
//            classRandomizer {
//                EN.t1
//            }
            ),
            paramRandomizers = listOf()

        ))

    }


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

