package com.x12q.randomizer.util


import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.lookup_node.RDClassData
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.test.Test

class ReflectionUtilsTest {

    abstract class R0:ClassRandomizer<Int>{
        override val returnedInstanceData: RDClassData
            get() = TODO("Not yet implemented")

        override fun isApplicableTo(classData: RDClassData): Boolean {
            TODO("Not yet implemented")
        }

        override fun random(): Int {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun `createClassRandomizer on abstract class`(){
        shouldThrow<IllegalArgumentException> {
            ReflectionUtils.createClassRandomizer(R0::class)
        }
    }

    class R1 : R0()

    @Test
    fun `createClassRandomizer on normal class`(){
        shouldNotThrow<Throwable> {
            ReflectionUtils.createClassRandomizer(R1::class)
        }
    }

    object R2:R0()
    @Test
    fun `createClassRandomizer on object`(){
        shouldNotThrow<Throwable> {
            ReflectionUtils.createClassRandomizer(R2::class) shouldBe R2
        }
    }


     abstract class PR0:ParameterRandomizer<Int>{
         override val paramClassData: RDClassData
             get() = TODO("Not yet implemented")

         override fun isApplicableTo(paramInfo: ParamInfo): Boolean {
             TODO("Not yet implemented")
         }

         override fun random(
             parameterClassData: RDClassData,
             parameter: KParameter,
             enclosingClassData: RDClassData
         ): Int? {
             TODO("Not yet implemented")
         }

     }

    @Test
    fun `createParamRandomizer on abstract class`(){
        shouldThrow<IllegalArgumentException> {
            ReflectionUtils.createParamRandomizer(PR0::class)
        }
    }


    class PR1 : PR0()

    @Test
    fun `createParamRandomizer on normal class`(){
        shouldNotThrow<Throwable> {
            ReflectionUtils.createParamRandomizer(PR1::class)
        }
    }

    object PR2:PR0()
    @Test
    fun `createParamRandomizer on object`(){
        shouldNotThrow<Throwable> {
            ReflectionUtils.createParamRandomizer(PR2::class) shouldBe PR2
        }
    }
}
