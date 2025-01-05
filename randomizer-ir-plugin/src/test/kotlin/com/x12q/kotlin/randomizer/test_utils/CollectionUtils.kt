package com.x12q.kotlin.randomizer.test_utils

fun <T> makeList(size: Int, sideEffect:()->Unit,makeElement:()->T):List<T>{
    sideEffect()
    return List(size){ makeElement() }
}
