package com.x12q.randomizer.err

fun Throwable.toErrorReport():ErrorReport{
    return CommonErrors.ExceptionError.report(this)
}
