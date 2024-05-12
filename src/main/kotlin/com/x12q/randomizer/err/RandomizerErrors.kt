package com.x12q.randomizer.err

import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.RDClassData
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KTypeParameter


class RandomizerError(message:String): Exception(message)

object RandomizerErrors {

    private val _prefix = "RDERR-"

    private fun errCode(code:Int):String{
        return "${_prefix}${code}"
    }

    object IllegalRandomizer{
        private val header = ErrorHeader(errorCode = errCode(4), "Illegal randomizer")
        fun report(randomizer: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Illegal randomizer: [${randomizer}]")
            val rt = header.toErrorReport()
            return rt
        }
    }

    object CantApplyParamRandomizerToClass {
        private val header = ErrorHeader(errorCode = errCode(3), "Can't apply param randomizer to class")
        fun report(paramRandomizer: ParameterRandomizer<*>, targetClass:KClass<*>): ErrorReport {
            val header =
                header.setDescription("Can't apply param randomizer [${paramRandomizer::class}] to [${targetClass}] because it is a [${ParameterRandomizer::class}]")
            val rt = header.toErrorReport()
            return rt
        }
    }


    object ClassifierNotSupported {
        val header = ErrorHeader(errorCode = errCode(2), "Classifier not supported")
        fun report(classifier:KClassifier?): ErrorReport {
            val header = header.setDescription(
                "Classifier [$classifier] is not supported"
            )
            val rt = header.toErrorReport()
            return rt
        }
    }
    object TypeDoesNotExist {
        val header = ErrorHeader(errorCode = errCode(1), "Type does not exist")
        fun report(kTypeParameter: KTypeParameter, parentClazz: RDClassData): ErrorReport {
            val header = header.setDescription(
                "Concrete type for [$kTypeParameter] in [${parentClazz.kClass}] does not exist"
            )
            val rt = header.toErrorReport()
            return rt
        }
    }
}
