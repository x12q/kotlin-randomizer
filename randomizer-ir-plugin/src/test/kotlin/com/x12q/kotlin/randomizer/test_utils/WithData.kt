package com.x12q.kotlin.randomizer.test_utils

interface WithData {
    val data:Any

    companion object{
        val name = WithData::class.qualifiedName!!
    }
}
