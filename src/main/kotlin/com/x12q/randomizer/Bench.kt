package com.x12q.randomizer

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
                (1..10).random()
            },
            floatRandomizer {
                1.0f
            },
            stringRandomizer {
                "abc123"
            }
        ),
        paramRandomizers = listOf(
            stringParamRandomizer(
                condition = {
                    it.paramName == "t2" && it.parentIs<A2>()
                },
                random = {
                    "__qwe__"
                }
            )
        )
    ))

    println(random<ABC2>(
        randomizers = randomizers {
            int {
                (1..10).random()
            }
            float {
                1f
            }
            string {
                "abc123"
            }
        },
        paramRandomizers = paramRandomizers {
            string(
                condition = {
                    it.paramName == "t2" && it.parentIs<A2>()
                },
                random = {
                    "__qwe__"
                }
            )
        }
    ))
}

