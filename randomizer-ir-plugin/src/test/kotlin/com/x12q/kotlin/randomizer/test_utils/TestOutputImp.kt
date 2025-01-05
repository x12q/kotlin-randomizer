package com.x12q.kotlin.randomizer.test_utils

class TestOutputImp: TestOutput {
    private val strBuilder=StringBuilder()
    override fun printOutput(any: Any?){
        strBuilder.appendLine(any)
    }

    private val objs = mutableListOf<Any?>()
    private val withDataList = mutableListOf<WithData>()

    override fun putObject(any: Any?) {
        objs.add(any)
    }

    override fun putData(data: WithData) {
        objs.add(data.data)
        withDataList.add(data)
    }

    override fun getObjs(): List<Any?> {
        return objs
    }

    override fun getWithData(): List<WithData> {
        return withDataList
    }

    override fun getStr():String{
        return strBuilder.toString().trim()
    }
}
