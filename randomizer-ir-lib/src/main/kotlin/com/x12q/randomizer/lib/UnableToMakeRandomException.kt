package com.x12q.randomizer.lib

class UnableToMakeRandomException(
    targetClassName:String?,
    paramName:String?,
    type:String?,
) : Exception(
    run {
        val paramPrefix = listOfNotNull(paramName,type).joinToString(":")
        val param = if(paramPrefix.isNotEmpty()){
            "param [$paramPrefix]"
        }else{
            ""
        }

        val clazz = targetClassName?.let {
            "of class [$targetClassName]"
        } ?: ""

        if(clazz.isEmpty() && param.isEmpty()){
            "Unable to randomize"
        }else{
            listOf(
                "Unable to randomize", param, clazz
            ).joinToString(" ")
        }
    }
)
