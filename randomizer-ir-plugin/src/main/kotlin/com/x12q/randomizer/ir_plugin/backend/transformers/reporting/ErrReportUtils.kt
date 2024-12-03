package com.x12q.randomizer.ir_plugin.backend.transformers.reporting

fun developerErrorMsg(msg:String):String{
    val m = if(msg.endsWith(".")){
        msg.substring(0, msg.length-1)
    }else{
        "$msg."
    }
    return "$m This is a bug by the developer."
}
