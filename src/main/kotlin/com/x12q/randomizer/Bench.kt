package com.x12q.randomizer

import com.x12q.randomizer.randomizer.builder.paramRandomizers
import com.x12q.randomizer.randomizer.builder.randomizers
import com.x12q.randomizer.randomizer.primitive.*
import kotlinx.serialization.Serializable


@Serializable
data class ABC(val lst: List<Float>, val tm12: Int)
data class A2(val t2: String)
data class ABC2(
    val a: ABC,
    val t: String,
    val b: ABC,
    val t2: String,
    val a2: A2
)

data class Q<T>(val t: T)

fun main() {
    println(random<ABC2>(
        randomizers = listOf(
            intRandomizer {
                99
            },
            floatRandomizer {
                1.0f
            },
            stringRandomizer {
                "abc123"
            },
            listRandomizer {
                listOf(1f,2f)
            }
        ),
        paramRandomizers = listOf(
            stringParamRandomizer(
//                condition = {
//                    it.paramName == "t2" && it.parentIs<A2>()
//                },
                random = {
                    "${it.paramName}:__qwe__"
                }
            )
        )
    ))

    println(random<ABC2>(
        randomizers = randomizers {
            int {
                99
            }
            float {
                1f
            }
            string {
                "abc123"
            }
            list{
                listOf(1f,2f)
            }
        },
        paramRandomizers = paramRandomizers {
            string(
//                condition = {
//                    it.paramName == "t2" && it.parentIs<A2>()
//                },
                random = {
                    "${it.paramName}:__qwe__"
                }
            )
        }
    ))
}

