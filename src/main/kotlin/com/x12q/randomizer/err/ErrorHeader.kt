package com.x12q.randomizer.err

import java.util.*


data class ErrorHeader(val errorCode: String, val errorDescription: String){
    override fun toString(): String {
        return "${errorCode}: $errorDescription"
    }
    fun isType(errorHeader: ErrorHeader):Boolean{
        return this.errorCode == errorHeader.errorCode
    }

    /**
     * convenient method for non-production operation
     */
    fun toErrorReport(): ErrorReport {
        return SingleErrorReport(
            header =this,
        )
    }
    fun appendDescription(moreInfo:String): ErrorHeader {
        return this.copy(errorDescription = this.errorDescription + moreInfo)
    }
    fun setDescription(newDescription:String): ErrorHeader {
        return this.copy(errorDescription =  newDescription)
    }

}
