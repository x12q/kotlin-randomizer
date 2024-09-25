package com.x12q.randomizer.ir_plugin.backend.transformers.reporting

sealed interface ReportData{
    fun makeMsg():String?
}