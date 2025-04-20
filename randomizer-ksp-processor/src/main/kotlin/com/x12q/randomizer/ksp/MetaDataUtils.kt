package com.x12q.randomizer.ksp

import com.x12q.com.x12q.randomizer.internal_utils.crashOnNull
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg

object RdMetaData{
    val randomizableAnnotationFullName = Randomizable::class.qualifiedName.crashOnNull {
        developerErrorMsg("Randomizable is not in the class path of randomizer-ksp-processor")
    }
}

