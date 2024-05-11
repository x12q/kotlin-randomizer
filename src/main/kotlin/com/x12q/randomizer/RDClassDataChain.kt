package com.x12q.randomizer

import kotlin.reflect.KTypeParameter

data class RDClassDataChain(
    val l:List<RDClassData>
) {
    fun getDataFor(kTypeParameter: KTypeParameter): RDClassData? {
        for(r in l){
            val rt = r.getDataFor(kTypeParameter)
            if(rt!=null){
                return rt
            }
        }
        return null
    }

    fun add(r: RDClassData):RDClassDataChain{
        val newList = listOf(r) + l
        return this.copy(l=newList)
    }

    companion object {
        fun from(vararg rdClassData: RDClassData):RDClassDataChain{
            return RDClassDataChain(rdClassData.toList())
        }
    }
}
