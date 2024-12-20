package com.x12q.kotlin.randomizer.ir_plugin.backend.transformers.reporting

data class MapKeyReportData(
    val keyType:String,
    val paramName:String?,
    val enclosingClassName:String?,
): ReportData {
    companion object{
        private const val errCode = "MAP_KEY_ERR_1"
    }
    override fun makeMsg(): String {
        val part1 = "$errCode: Unable to generate Map key of type [$keyType]"
        val part2 =  paramName?.let{"stored in param [$paramName]"}
        val ofClass = enclosingClassName?.let{"in class [$enclosingClassName]"}
        val rt= listOfNotNull(part1,part2, ofClass).joinToString(" ")
        return rt
    }
}
