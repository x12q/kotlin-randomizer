package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.primitive.uuidStringRandomizer


annotation class RandomStringUUID{
    companion object{
        fun RandomStringUUID.makeClassRandomizer(): ClassRandomizer<String> {
            return uuidStringRandomizer()
        }
    }
}
