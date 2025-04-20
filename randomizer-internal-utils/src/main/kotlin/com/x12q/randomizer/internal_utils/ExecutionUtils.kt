package com.x12q.com.x12q.randomizer.internal_utils


/**
 * Evaluate each function in [functionList], stop at the first output that satisfies [resultIsOk]
 */
fun <T> stopAtFirst(
    resultIsOk: (T) -> Boolean,
    vararg functionList: () -> T
): T? {
    for (func in functionList) {
        val result = func()
        if (resultIsOk(result)) {
            return result
        }
    }
    return null
}

/**
 * Evaluate each function in [functionList], stop at the first output that is not null
 */
fun <T> stopAtFirstNotNull(
    vararg functionList: () -> T?
): T? {
    return stopAtFirst({ it != null }, *functionList)
}

inline fun <T> T?.crashOnNull(msg:()->Any):T{
    return requireNotNull(this,msg)
}

fun <T> T?.crashOnNull():T{
    return requireNotNull(this)
}

