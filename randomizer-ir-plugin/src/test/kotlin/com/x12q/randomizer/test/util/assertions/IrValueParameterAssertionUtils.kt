package com.x12q.randomizer.test.util.assertions

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.classFqName
import kotlin.reflect.KClass

fun IrValueParameter.shouldBeInstanceOf(kclass:KClass<*>){
    this.type.classFqName.toString() shouldBe  kclass.qualifiedName
}

fun IrValueParameter.isInstanceOf(kclass:KClass<*>):Boolean{
    return this.type.classFqName.toString()  == kclass.qualifiedName
}

fun IrValueParameter.shouldBeInstanceOf(name:String){
    this.type.classFqName.toString() shouldBe name
}

fun IrValueParameter.isInstanceOf(name:String):Boolean{
    return this.type.classFqName.toString() == name
}
