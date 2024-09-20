package com.x12q.randomizer.lib

class UnableToMakeRandomException(
    targetClassName: String?,
    paramName: String?,
    type: String?,
) : Exception(
    """
    Unable to generate random for:
        class: $targetClassName
        paramName: $paramName
        type: $type
    """.trimIndent()
)
