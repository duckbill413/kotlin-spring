package wh.duckbill.bank.common.exception

class CustomException(
  private val codeInterface: CodeInterface,
  private val additionalMessage: String? = null
) : RuntimeException(
  if (additionalMessage == null) {
    codeInterface.message
  } else {
    "${codeInterface.message} - $additionalMessage"
  }
) {

  fun getCodeInterface(): CodeInterface {
    if (additionalMessage != null) {
      codeInterface.message += additionalMessage
    }
    return codeInterface
  }
}