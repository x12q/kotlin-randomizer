package com.x12q.randomizer.lib

class UnableToGenerateRandomException(
    targetClassName:String,
    paramName:String,
    paramType:String,
) : Exception("Unable to generate random param $paramName: $paramType of class $targetClassName")
