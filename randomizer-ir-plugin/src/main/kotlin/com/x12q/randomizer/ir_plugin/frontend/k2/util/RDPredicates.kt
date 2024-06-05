package com.x12q.randomizer.ir_plugin.frontend.k2.util

import com.x12q.randomizer.ir_plugin.base.BaseObjects
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate

object RDPredicates {
    val annotatedRandomizable = LookupPredicate.create {
        annotated(BaseObjects.randomizableFqName)
    }
}
