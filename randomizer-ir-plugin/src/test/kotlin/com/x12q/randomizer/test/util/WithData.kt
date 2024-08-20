package com.x12q.randomizer.test.util

interface WithData {
    val data:Any

    companion object{
        val name = WithData::class.qualifiedName!!
    }
}
