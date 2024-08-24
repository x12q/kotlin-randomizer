package com.x12q.randomizer.lib

class UnableToMakeRandomException(
    targetClassName:String,
    paramName:String,
    paramType:String,
) : Exception("Unable to randomize param [$paramName: $paramType] of class [$targetClassName]")
