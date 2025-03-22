package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.lib.rs.Err
import com.x12q.kotlin.randomizer.lib.rs.Ok
import com.x12q.kotlin.randomizer.lib.rs.RdRs
import kotlin.reflect.typeOf


inline fun <reified T> RandomizerContainer.getRandomizer(): ClassRandomizer<T>?{
    val key = TypeKey.of<T>()
    return randomizersMap[key] as? ClassRandomizer<T>
}

inline fun <reified T> RandomContext.random(): T?{
    val randomizer = this.getRandomizer<T>()
    val resultFromRandomizer = randomizer?.random()
    return resultFromRandomizer
}

inline fun <reified T> RandomContext.randomRs(): RdRs<T?, NoRandomizerErr>{
    val randomizer = this.getRandomizer<T>()
    if(randomizer == null){
        return Err(NoRandomizerErr)
    }else{
        val resultFromRandomizer = randomizer.random()
        return Ok(resultFromRandomizer)
    }
}

inline fun <reified T> RandomContext.randomOrThrow(): T{
    val randomizer = this.getRandomizer<T>()
    if(randomizer!=null){
        val resultFromRandomizer = randomizer.random()
        return resultFromRandomizer
    }else{
        throw UnableToMakeRandomException("Unable to make random of type ${typeOf<T>()}")
    }
}


