package com.x12q.randomizer.test.util

class TestOutputImp: TestOutput {
    private val strBuilder=StringBuilder()
    override fun printOutput(any: Any?){
        strBuilder.appendLine(any)
    }

    override fun getStr():String{
        return strBuilder.toString().trim()
    }
}
