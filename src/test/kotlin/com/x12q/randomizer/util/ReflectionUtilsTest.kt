package com.x12q.randomizer.util


import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.lookup_node.RDClassData
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

class ReflectionUtilsTest {

    data class Inner1<I1, I2, I3>(val i1: I1, val i2: I2, val i3: I2)
    data class Q6<Q6_1, Q6_2>(
        val l: Inner1<Q6_1, Double, Q6_2>,
        val t:List<Q6_2>,
        val t2:Q6_2
    )

    @Test
    fun makeTypeMap(){
        val enclosure = RDClassData.from<Q6<Int, String>>()
        // l param
        enclosure.kClass.primaryConstructor!!.parameters[0].also { parameter ->
            val typeMapFromEnclosure = ReflectionUtils.makeTypeMap(parameter, enclosure)
            typeMapFromEnclosure shouldBe mapOf(
                0 to RDClassData.from<Int>(),
                2 to RDClassData.from<String>()
            )
        }
        // t param
        enclosure.kClass.primaryConstructor!!.parameters[1].also { parameter ->
            val typeMapFromEnclosure = ReflectionUtils.makeTypeMap(parameter, enclosure)
            typeMapFromEnclosure shouldBe mapOf(
                0 to RDClassData.from<String>(),
            )
        }
        // t2 param
        enclosure.kClass.primaryConstructor!!.parameters[2].also { parameter ->
            val typeMapFromEnclosure = ReflectionUtils.makeTypeMap(parameter, enclosure)
            typeMapFromEnclosure shouldBe emptyMap()
        }
    }

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
