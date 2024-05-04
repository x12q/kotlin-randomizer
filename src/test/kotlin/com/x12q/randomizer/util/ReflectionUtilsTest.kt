package com.x12q.randomizer.util


import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.util.ReflectionUtils.canProduceGeneric
import com.x12q.randomizer.util.ReflectionUtils.isAssignableTo
import com.x12q.randomizer.util.ReflectionUtils.isAssignableToGenericOf
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.reflect.typeOf
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

    open class A
    interface B
    open class A1 : B, A()
    class A2 : A1()
    class B1 : B


    @Test
    fun containGeneric() {
        typeOf<List<A>>().canProduceGeneric(A1::class).shouldBeTrue()
        typeOf<List<A>>().canProduceGeneric(A2::class).shouldBeTrue()

        typeOf<List<B>>().canProduceGeneric(A1::class).shouldBeTrue()
        typeOf<List<B>>().canProduceGeneric(A2::class).shouldBeTrue()

        typeOf<List<A1>>().canProduceGeneric(A2::class).shouldBeTrue()

        typeOf<List<A>>().canProduceGeneric(B::class).shouldBeFalse()


    }

    @Test
    fun checkAssignable() {
        A::class.isAssignableTo(typeOf<A>()) shouldBe true
        A1::class.isAssignableTo(typeOf<A>()) shouldBe true

        B::class.isAssignableTo(typeOf<B>()) shouldBe true
        B1::class.isAssignableTo(typeOf<B>()) shouldBe true
        A1::class.isAssignableTo(typeOf<B>()) shouldBe true

        A::class.isAssignableTo(typeOf<A1>()) shouldBe false
        B::class.isAssignableTo(typeOf<B1>()) shouldBe false

        B::class.isAssignableTo(typeOf<A>()) shouldBe false
        B::class.isAssignableTo(typeOf<A1>()) shouldBe false

        A::class.isAssignableTo(typeOf<B>()) shouldBe false
        A1::class.isAssignableTo(typeOf<B1>()) shouldBe false
    }

    @Test
    fun checkGenericAssignable() {

        A::class.isAssignableToGenericOf(typeOf<List<A>>()) shouldBe true
        B::class.isAssignableToGenericOf(typeOf<List<B>>()) shouldBe true
        A1::class.isAssignableToGenericOf(typeOf<List<A1>>()) shouldBe true
        B1::class.isAssignableToGenericOf(typeOf<List<B1>>()) shouldBe true

        A1::class.isAssignableToGenericOf(typeOf<List<A>>()) shouldBe true
        B::class.isAssignableToGenericOf(typeOf<List<A>>()) shouldBe false
        B1::class.isAssignableToGenericOf(typeOf<List<A>>()) shouldBe false

        A::class.isAssignableToGenericOf(typeOf<List<B>>()) shouldBe false
        A1::class.isAssignableToGenericOf(typeOf<List<B>>()) shouldBe true
        B1::class.isAssignableToGenericOf(typeOf<List<B>>()) shouldBe true

        A::class.isAssignableToGenericOf(typeOf<List<A1>>()) shouldBe false
        B::class.isAssignableToGenericOf(typeOf<List<A1>>()) shouldBe false
        B1::class.isAssignableToGenericOf(typeOf<List<A1>>()) shouldBe false

        B::class.isAssignableToGenericOf(typeOf<List<B1>>()) shouldBe false
        A::class.isAssignableToGenericOf(typeOf<List<B1>>()) shouldBe false
        A1::class.isAssignableToGenericOf(typeOf<List<B1>>()) shouldBe false

    }
}
