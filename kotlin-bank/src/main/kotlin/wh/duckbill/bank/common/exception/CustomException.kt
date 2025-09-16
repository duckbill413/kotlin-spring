package wh.duckbill.bank.common.exception

import java.lang.RuntimeException

class CustomException(
  private val errorCode: CodeInterface,
  private val additionalMessage: String? = null
): RuntimeException(if (additionalMessage == null) {
  errorCode.message
} else {
  "${errorCode.message} - $additionalMessage"
}) {

}