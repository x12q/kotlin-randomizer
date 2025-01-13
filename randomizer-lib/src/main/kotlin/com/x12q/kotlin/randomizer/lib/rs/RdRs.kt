package com.x12q.kotlin.randomizer.lib.rs

sealed class RdRs<out V, out E>
data class Ok<out V>(val value:V): RdRs<V,Nothing>()
data class Err<out E>(val err:E):RdRs<Nothing,E>()
