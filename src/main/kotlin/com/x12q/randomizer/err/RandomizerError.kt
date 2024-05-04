package com.x12q.randomizer.err

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ParameterRandomizer
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

    object CantGenerateRandom {
        private val header = ErrorHeader(errorCode = errCode(5), "Can't generate random instances")
        fun report(clazz: KClass<*>?): ErrorReport {
            val header =
                clazz?.let { header.setDescription("generate random instances for $clazz") } ?: header
            val rt = header.toErrorReport()
            return rt
        }
    }

    object CantApplyRandomizer {
        private val header = ErrorHeader(errorCode = errCode(4), "Can't apply randomizer")
        fun report(paramRandomizer: ParameterRandomizer<*>, kParam: KParameter): ErrorReport {
            val header =
                header.setDescription("Can't apply randomizer ${paramRandomizer::class.simpleName} to $kParam")
            val rt = header.toErrorReport()
            return rt
        }
    }

    object CantRandomizeInterface {
        private val header = ErrorHeader(errorCode = errCode(3), "Can't randomize interface")
        fun report(clazz: KClass<*>?): ErrorReport {
            val header = clazz?.let { header.setDescription("Can't randomize interface: $clazz") } ?: header
            val rt = header.toErrorReport()
            return rt
        }
    }

    object CantRandomizeAbstractClass {
        private val header = ErrorHeader(errorCode = errCode(2), "Can't randomize abstract class")
        fun report(clazz: KClass<*>): ErrorReport {
            val header = header.setDescription("Can't randomize abstract class: $clazz")
            val rt = header.toErrorReport()
            return rt
        }
    }


    object NoConstructorFound {
        private val header = ErrorHeader(errorCode = errCode(1), "No constructor found")
        fun report(clazz: KClass<*>): ErrorReport {
            val header = header.setDescription("No constructor found for class: $clazz")
            val rt = header.toErrorReport()
            return rt
        }
    }
}
