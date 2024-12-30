package com.x12q.kotlin.randomizer.test.util

fun withTestOutput(run:TestOutput.()->Unit):TestOutput{
    val rt = TestOutputImp()
    run(rt)
    return rt
}

fun testOutput(vararg data: WithData): TestOutput{
    return withTestOutput {
        for(d in data){
            putData(d)
        }
    }
}
