package com.x12q.kotlin.randomizer.lib.rs

data class Err<out E>(val err:E):RdRs<Nothing,E>()
