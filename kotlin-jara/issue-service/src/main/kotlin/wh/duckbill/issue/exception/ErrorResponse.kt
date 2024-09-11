package wh.duckbill.issue.exception

data class ErrorResponse(
    val code: Int,
    val message: String,
)