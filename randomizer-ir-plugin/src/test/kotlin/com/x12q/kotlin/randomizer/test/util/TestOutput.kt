package com.x12q.kotlin.randomizer.test.util

interface TestOutput{
    fun printOutput(any: Any?)
    fun putObject(any:Any?)
    fun putData(data:WithData)
    fun getObjs():List<Any?>
    fun getWithData():List<WithData>
    fun getStr():String
}


fun withTestOutput(run:TestOutput.()->Unit):TestOutput{
    val rt = TestOutputImp()
    run(rt)
    return rt
}
