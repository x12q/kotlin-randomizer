package com.x12q.randomizer.err


object CommonErrors {
    private const val prefix = "COMMON_ERR-"

    private fun errCode(code:Int):String{
        return "$prefix${code}"
    }
    private fun makeCommonExceptionErrorReport(templateHeader: ErrorHeader,detail:String?,exception: Throwable): ErrorReport {
        return SingleErrorReport(
            header = templateHeader.let { errHeader ->
                detail?.let {
                    errHeader.setDescription(detail)
                } ?: templateHeader
            },
        )
    }


    /**
     * this error indicates that an exception was caught
     */
    object ExceptionError {
        val header = ErrorHeader(errCode(3), "Exception error")

        fun report(exception: Throwable): ErrorReport {
            return makeCommonExceptionErrorReport(header.appendDescription(":${exception}"),null,exception)
        }
        fun report(message:String?,exception: Throwable): ErrorReport {
            return makeCommonExceptionErrorReport(header,message,exception)
        }
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
