package com.x12q.randomizer.randomizer_processor

import com.x12q.randomizer.err.ErrorHeader
import com.x12q.randomizer.err.ErrorReport
import kotlin.reflect.KClass

object InvalidRandomizerReason {

    private val _prefix = "INV_RD"
    private fun errCode(code:Int):String{
        return "${_prefix}${code}"
    }

    object InvalidRandomizerClass{
        private val header = ErrorHeader(errorCode = errCode(3), "Invalid randomizer class")
        fun report(randomizer: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Invalid randomizer class${randomizer.qualifiedName}")
            val rt = header.toErrorReport()
            return rt
        }
    }

    object IsAbstract{
        private val header = ErrorHeader(errorCode = errCode(2), "Abstract randomizer")
        fun report(randomizer: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Randomizer is abstract: ${randomizer.qualifiedName}")
            val rt = header.toErrorReport()
            return rt
        }
    }
    /**
     * When the checked class is a [ClassRandomizer] but cannot generate random instance of [targetClass]
     */
    object UnableToGenerateTargetType{
        private val header = ErrorHeader(errorCode = errCode(1), "Wrong type Randomizer")
        fun report(randomizer: KClass<*>, targetClass: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Randomzer ${randomizer} can't generate instances of ${targetClass}")
            val rt = header.toErrorReport()
            return rt
        }
    }
}
