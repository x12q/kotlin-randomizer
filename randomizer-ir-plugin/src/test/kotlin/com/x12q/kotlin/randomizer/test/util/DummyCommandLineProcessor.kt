package com.x12q.kotlin.randomizer.test.util

import com.x__q.randomizer_ir_plugin.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

/**
 * A command line processor that does nothing
 */
@OptIn(ExperimentalCompilerApi::class)
class DummyCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = BuildConfig.IR_PLUGIN_ID
    override val pluginOptions: Collection<CliOption> = listOf()
}
