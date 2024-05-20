package com.x12q.randomizer.err

import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ClassRandomizer
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KTypeParameter


class RandomizerError(message:String): Exception(message)

object RandomizerErrors {

    private val _prefix = "RDERR-"

    private fun errCode(code:Int):String{
        return "${_prefix}${code}"
    }

    /**
     * This is for when a [ParameterRandomizer] is not applicable to a class
     */
    object CantApplyClassRandomizerToClass {
        private val header = ErrorHeader(errorCode = errCode(5), "Can't apply class randomizer to class")
        fun report(randomizer: ClassRandomizer<*>, targetClass:KClass<*>): ErrorReport {
            val header =
                header.setDescription("Can't apply class randomizer [${randomizer::class}] to [${targetClass}]")
            val rt = header.toErrorReport()
            return rt
        }
    }


    /**
     * This is for when a non-randomizer class is provided as a randomizer
     */
    object IllegalRandomizer{
        private val header = ErrorHeader(errorCode = errCode(4), "Illegal randomizer")
        fun report(randomizer: KClass<*>): ErrorReport {
            val header =
                header.setDescription("Illegal randomizer: [${randomizer}]")
            val rt = header.toErrorReport()
            return rt
        }
    }

    /**
     * This is for when a [ParameterRandomizer] is not applicable to a class
     */
    object CantApplyParamRandomizerToClass {
        private val header = ErrorHeader(errorCode = errCode(3), "Can't apply param randomizer to class")
        fun report(randomizer: ParameterRandomizer<*>, targetClass:KClass<*>): ErrorReport {
            val header =
                header.setDescription("Can't apply param randomizer [${randomizer::class}] to [${targetClass}]")
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
