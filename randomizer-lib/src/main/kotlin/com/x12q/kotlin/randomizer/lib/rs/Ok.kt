package com.x12q.kotlin.randomizer.lib.rs

data class Ok<out V>(val value:V): RdRs<V,Nothing>()
