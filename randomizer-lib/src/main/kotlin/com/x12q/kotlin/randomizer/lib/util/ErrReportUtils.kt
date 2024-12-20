package com.x12q.kotlin.randomizer.lib.util

fun developerErrorMsg(msg:String):String{
    val m = if(msg.endsWith(".")){
        msg.substring(0, msg.length-1)
    }else{
        msg
    }
    return "$m. This is most likely a bug by the developer."
}
