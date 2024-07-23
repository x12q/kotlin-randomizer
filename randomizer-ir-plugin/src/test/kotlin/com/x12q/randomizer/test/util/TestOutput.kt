package com.x12q.randomizer.test.util

interface TestOutput{
    fun printOutput(any: Any?)
    fun getStr():String
}


fun withTestOutput(run:TestOutput.()->Unit):TestOutput{
    val rt = TestOutputImp()
    run(rt)
    return rt
}

