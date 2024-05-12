package com.x12q.randomizer.randomizer.config

import javax.inject.Inject
import javax.inject.Singleton

/**
 * This configuration data is used only by the default and lowest-level randomizer.
 * Therefore, if there are prioritized randomizers (such as one provided by users) then these config are ignore entirely for applicable type/class.
 *
 * For example:
 * - If there's no prioritized randomizer, a request to generate a random list of integer will use this config to constraint the list's length.
 * - If there's a prioritized randomizer targeting list of int, then this config will not be used to generate any list of int. But, it is still used to generate lists of string or any other type.
 */
@Singleton
data class RandomizerConfig(
    /**
     * To constraint [Map], [List], [Set] size
     */
    val collectionSize: IntRange,
    /**
     * To constraint random string size
     */
    val stringSize: IntRange,
    val charRange:CharRange
) {
    @Inject
    constructor() : this(
        collectionSize = 1..20,
        stringSize = 1..30,
        charRange = ('A'..'z')
    )

    companion object {

        val default by lazy { RandomizerConfig() }
    }
}
