package com.x12q.kotlin.randomizer.test.util

fun <T> makeList(size: Int, sideEffect:()->Unit,makeElement:()->T):List<T>{
    sideEffect()
    return List(size){ makeElement() }
}
