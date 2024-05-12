package com.x12q.randomizer.err


object CommonErrors {
    private const val prefix = "COMMON_ERR-"

    private fun errCode(code:Int):String{
        return "$prefix${code}"
    }

    object MultipleErrors {
        val header = ErrorHeader(errCode(4), "Multiple errors")

        fun report(errorList: List<ErrorReport>): MultiErrorReport {
            return MultiErrorReport(
                header = header,
                singleErrorReportList =errorList
            )
        }
    }
}
