package com.x12q.kotlin.randomizer.lib.rs

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <V,E> RdRs<V,E>.isErr(): Boolean{
    contract {
        returns(true) implies (this@isErr is Err<E>)
        returns(false) implies (this@isErr is Ok<V>)
    }
    return this is Err
}

@OptIn(ExperimentalContracts::class)
fun <V,E> RdRs<V,E>.isOk(): Boolean{
    contract {
        returns(true) implies (this@isOk is Ok<V>)
        returns(false) implies (this@isOk is Err<E>)
    }
    return this is Ok
}
