package com.x12q.randomizer.lib

class UnableToMakeRandomException(
    targetClassName:String?,
    paramName:String?,
    type:String,
) : Exception(
    if(targetClassName!=null && paramName!=null){
        "Unable to randomize param [$paramName: $type] of class [$targetClassName]"
    }else{
        "Unable to randomize $type]"
    }
)
