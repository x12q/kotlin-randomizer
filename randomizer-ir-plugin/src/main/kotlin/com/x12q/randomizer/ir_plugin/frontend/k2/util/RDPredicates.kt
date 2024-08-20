package com.x12q.randomizer.ir_plugin.frontend.k2.util

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate

object RDPredicates {
    val annotatedRandomizable = DeclarationPredicate.create {
        annotated(setOf(BaseObjects.randomizableFqName))
    }
}
