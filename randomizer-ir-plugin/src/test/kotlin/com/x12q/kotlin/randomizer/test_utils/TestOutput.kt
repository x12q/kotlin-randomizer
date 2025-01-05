package com.x12q.kotlin.randomizer.test_utils


interface TestOutput{
    fun printOutput(any: Any?)
    fun putObject(any:Any?)
    fun putData(data: WithData)
    fun getObjs():List<Any?>
    fun getWithData():List<WithData>
    fun getStr():String
}
