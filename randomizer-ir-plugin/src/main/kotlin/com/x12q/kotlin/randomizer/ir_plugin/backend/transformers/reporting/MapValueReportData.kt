package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.reporting

data class MapValueReportData(
    val valueType:String,
    val paramName:String?,
    val enclosingClassName:String?,
): ReportData {
    companion object{
        private const val errCode = "MAP_VALUE_ERR_1"
    }
    override fun makeMsg(): String {
        val part1 = "$errCode: Unable to generate Map value of type [$valueType]"
        val part2 =  paramName?.let{"stored in param [$paramName]"}
        val ofClass = enclosingClassName?.let{"in class [$enclosingClassName]"}
        val rt= listOfNotNull(part1,part2, ofClass).joinToString(" ")
        return rt
    }
}
