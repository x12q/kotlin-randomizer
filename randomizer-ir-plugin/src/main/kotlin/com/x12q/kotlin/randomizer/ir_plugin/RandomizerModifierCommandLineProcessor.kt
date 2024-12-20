package com.x12q.kotlin.randomizer.ir_plugin

import com.google.auto.service.AutoService
import com.x__q.randomizer_ir_plugin.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class RandomizerModifierCommandLineProcessor : CommandLineProcessor {

  override val pluginId: String = BuildConfig.IR_PLUGIN_ID

  override val pluginOptions: Collection<CliOption> = listOf(
    CliOption(
      optionName = enableOption,
      valueDescription = "boolean",
      description = "enable or disable the ir-plugin",
      required = false,
    ),
  )

  override fun processOption(
    option: AbstractCliOption,
    value: String,
    configuration: CompilerConfiguration
  ) {
    return when (option.optionName) {
      enableOption->configuration.put(argEnable,value.toBooleanStrictOrNull() ?: true)
      else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
    }
  }

  companion object {
    val enableOption = "enable"
    val argEnable = CompilerConfigurationKey<Boolean>(enableOption)
  }
}
