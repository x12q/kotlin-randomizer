package com.x12q.randomizer

interface Randomizer<T> {

    fun random(): T

    fun random(config:RandomConfig):T
}


