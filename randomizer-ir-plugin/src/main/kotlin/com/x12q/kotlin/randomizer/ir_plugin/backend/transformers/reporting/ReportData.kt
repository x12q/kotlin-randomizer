package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.reporting

sealed interface ReportData{
    fun makeMsg():String?
}
