package com.x12q.kotlin.randomizer.lib.rs

import com.x12q.kotlin.randomizer.lib.NoRandomizerErr
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RdRsTest {
    @Test
    fun qwe(){
        val r: RdRs<Int, NoRandomizerErr> = Ok(1)
        r.isOk<Int, NoRandomizerErr>() shouldBe true
        r.isErr() shouldBe false

        val r2:RdRs<Int, NoRandomizerErr> = Err(NoRandomizerErr)
        r2.isOk() shouldBe false
        r2.isErr() shouldBe true
    }
}
