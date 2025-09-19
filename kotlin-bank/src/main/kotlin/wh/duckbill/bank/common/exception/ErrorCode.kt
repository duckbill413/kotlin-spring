package wh.duckbill.bank.common.exception

interface CodeInterface {
  val code: Int
  var message: String
}

enum class ErrorCode(
  override val code: Int,
  override var message: String
) : CodeInterface {
  AUTH_CONFIG_NOT_FOUND(-100, "auth config not found"),
  FAILED_TO_CALL_CLIENT(-101, "Failed to call client"),
  CALL_RESULT_BODY_NULL(-102, "body is null"),
}

