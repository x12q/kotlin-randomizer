package com.x12q.randomizer.ir_plugin.frontend.k1

import com.x12q.randomizer.ir_plugin.frontend.k2.base.BaseObjects
import com.x12q.randomizer.ir_plugin.util.isRandomizable
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

/**
 * This responsible for generating the front end meta obj
 */
class RandomizableSyntheticResolveExtension : SyntheticResolveExtension {
    override fun getPossibleSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name>? {
        return listOf(BaseObjects.randomFunctionName)
    }

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        if(thisDescriptor.isRandomizable()){

        }
        return listOf(BaseObjects.randomFunctionName)
    }
}
