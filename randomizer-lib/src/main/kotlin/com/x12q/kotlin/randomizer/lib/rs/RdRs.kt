package com.x12q.kotlin.randomizer.lib.rs
import com.x12q.kotlin.randomizer.lib.rs.Ok as _OK
import com.x12q.kotlin.randomizer.lib.rs.Err as _Err

sealed class RdRs<out V, out E>{
    companion object{
        fun <V> Ok(value:V): RdRs<V,Nothing>{
            return _OK(value)
        }

        fun <E> Err(err:E): RdRs<Nothing,E>{
            return _Err(err)
        }
    }
}
