package com.x12q.randomizer.err

import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter

object RandomizerErrors {

    private val _prefix = "RDERR-"

    private fun errCode(code:Int):String{
        return "${_prefix}${code}"
    }

    object IllegalRandomizer{
        private val header = ErrorHeader(errorCode = errCode(9), "Illegal randomizer")
        fun report(randomizer: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Illegal randomizer: ${randomizer.qualifiedName}")
            val rt = header.toErrorReport()
            return rt
        }
    }

    object CantApplyParamRandomizerToClass {
        private val header = ErrorHeader(errorCode = errCode(8), "Can't apply param randomizer to class")
        fun report(paramRandomizer: ParameterRandomizer<*>, targetClass:KClass<*>): ErrorReport {
            val header =
                header.setDescription("Can't apply param randomizer ${paramRandomizer::class.simpleName} to ${targetClass.simpleName} because it is a ${ParameterRandomizer::class.simpleName}$")
            val rt = header.toErrorReport()
            return rt
        }
    }


    object ClassifierNotSupported {
        val header = ErrorHeader(errorCode = errCode(7), "Classifier not supported")
        fun report(classifier:KClassifier?): ErrorReport {
            val header = header.setDescription(
                "Classifier $classifier is not supported"
            )
            val rt = header.toErrorReport()
            return rt
        }
    }
    object TypeDoesNotExist {
        private val header = ErrorHeader(errorCode = errCode(6), "Type does not exist")
        fun report(kTypeParameter: KTypeParameter, parentClazz: RDClassData): ErrorReport {
            val header = header.setDescription(
                "Type $kTypeParameter in ${parentClazz.kClass} does not exist"
            )
            val rt = header.toErrorReport()
            return rt
        }
    }
}
