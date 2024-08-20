package com.x12q.randomizer.lib.util

import java.util.UUID

/**
 * This served as the bridge between different platform
 */
internal fun randomUUIDStr():String{
    return UUID.randomUUID().toString()
}
