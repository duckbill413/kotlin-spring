package wh.duckbill.userservice.exception

data class ErrorResponse(
    val code: Int,
    val message: String,
)