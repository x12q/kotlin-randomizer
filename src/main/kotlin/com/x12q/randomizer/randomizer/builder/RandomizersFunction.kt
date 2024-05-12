package com.x12q.randomizer.randomizer.builder


/**
 * Convenient function to create a [RandomizerListBuilder]
 */
fun randomizers(
    listBuilder: RandomizerListBuilder.()->Unit
):RandomizerListBuilder{
    val builder = RandomizerListBuilder()
    listBuilder(builder)
    return builder
}

/**
 * Convenient function to create [ParamRandomizerListBuilder]
 */
fun paramRandomizers(
    listBuilder: ParamRandomizerListBuilder.()->Unit
):ParamRandomizerListBuilder{
    val builder = ParamRandomizerListBuilder()
    listBuilder(builder)
    return builder
}
