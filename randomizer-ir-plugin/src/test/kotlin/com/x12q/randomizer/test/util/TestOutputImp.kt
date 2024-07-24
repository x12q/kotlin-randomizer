package com.x12q.randomizer.test.util

class TestOutputImp: TestOutput {
    private val strBuilder=StringBuilder()
    override fun printOutput(any: Any?){
        strBuilder.appendLine(any)
    }


    private val objs = mutableListOf<Any?>()
    override fun putObject(any: Any?) {
        objs.add(any)
    }

    override fun putData(data: WithData) {
        objs.add(data.data)
    }

    override fun getObjs(): List<Any?> {
        return objs
    }

    override fun getStr():String{
        return strBuilder.toString().trim()
    }
}
