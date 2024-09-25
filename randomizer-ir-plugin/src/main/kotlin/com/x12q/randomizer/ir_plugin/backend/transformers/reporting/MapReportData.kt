package com.x12q.randomizer.ir_plugin.backend.transformers.reporting

data class MapReportData(
    val keyType:String,
    val valueType:String,
    val paramName:String?,
    val enclosingClassName:String?,
) : ReportData {
    override fun makeMsg(): String {
        val part1 = "Unable to generate Map<$keyType, $valueType>"
        val part2 =  paramName?.let{"stored in param $paramName"}
        val ofClass = enclosingClassName?.let{"in class $enclosingClassName"}
        val rt= listOfNotNull(part1,part2, ofClass).joinToString(" ")
        return rt
    }
}