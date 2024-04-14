package com.siliconwich.randomizer

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.siliconwich.randomizer.config.ConstructorRule
import com.siliconwich.randomizer.err.RandomizerError
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf


/**
 * Class data available in runtime
 */
data class ClassData(
    val kClass: KClass<*>,
    val kType: KType
) {

    /**
     * Find a constructor depend on some rule
     */
    fun findConstructorRs(constructorRule: ConstructorRule = ConstructorRule.PrimaryConstructorOrFirst): Result<KFunction<Any>, RandomizerError>{
        // TODO implement filter base on rule
        val classRef = kClass
        val allConstructors = classRef.constructors
        val constructor = classRef.primaryConstructor ?: allConstructors.firstOrNull()
        val rt = constructor?.let {
            Ok(it)
        }?: Err(RandomizerError.NoConstructorFound)
        return rt
    }

    companion object {
        inline fun <reified T> from(): ClassData {
            return ClassData(
                T::class, typeOf<T>()
            )
        }
    }
}
