package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.randomizer.lib.test_utils.mock_obj.random_config.TestRandomConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.*

class RandomCollectionFunctionsTest{
    data class ABC(val i:Int, val str:String)
    data class DEF(val z:Float)

    lateinit var rdConfig:TestRandomConfig
    lateinit var rdContext:RandomContext

    @BeforeTest
    fun bt(){
        rdConfig = TestRandomConfig()
        rdContext = RandomContextBuilderImp()
            .setRandomConfigAndGenerateStandardRandomizers(rdConfig)
            .add(factoryRandomizer {
                ABC(123,"abc str")
            })
            .build()
    }

    @Test
    fun randomMap(){

        val randomMap =  rdContext.randomMap<Int,String>()
        rdConfig.reset()

        randomMap shouldBe buildMap {
            val mapSize = rdContext.randomCollectionSize()
            repeat(mapSize){
                put(rdContext.nextInt(),rdContext.nextString())
            }
        }
        rdConfig.reset()

        val randomMap2 = rdContext.randomMap<Int,ABC>()
        rdConfig.reset()

        randomMap2 shouldBe buildMap {
            val mapSize = rdContext.randomCollectionSize()
            repeat(mapSize){
                put(rdContext.nextInt(),rdContext.random<ABC>()!!)
            }
        }

        shouldThrow<UnableToMakeRandomException> {
            rdContext.randomMap<Int,DEF>()
        }

        shouldThrow<UnableToMakeRandomException> {
            rdContext.randomMap<DEF,Int>()
        }

        shouldThrow<UnableToMakeRandomException> {
            rdContext.randomMap<DEF,DEF>()
        }
    }

    @Test
    fun randomList(){
        val list1 = rdContext.randomList<Int>()
        rdConfig.reset()
        list1 shouldBe List(rdContext.randomCollectionSize()){
            rdContext.nextInt()
        }

        val list2 = rdContext.randomList<ABC>()
        rdConfig.reset()

        list2 shouldBe List(rdContext.randomCollectionSize()){
            rdContext.random<ABC>()
        }

        shouldThrow<UnableToMakeRandomException> {
            rdContext.randomList<DEF>()
        }
    }
}
