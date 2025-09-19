package wh.duckbill.bank.common.httpClient

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.springframework.stereotype.Component
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode

@Component
class CallClient(
  private val httpClient: OkHttpClient
) {
  fun GET(uri: String, headers: Map<String, String> = emptyMap()): String {
    val requestBuilder = Request.Builder().url(uri)
    headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
    val request = requestBuilder.build()
    val response = httpClient.newCall(request).execute()
    return resultHandler(response)
  }

  fun POST(uri: String, headers: Map<String, String> = emptyMap(), body: RequestBody): String {
    val requestBuilder = Request.Builder().url(uri).post(body)
    headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
    val request = requestBuilder.build()
    val response = httpClient.newCall(request).execute()
    return resultHandler(response)
  }

  private fun resultHandler(response: Response): String {
    response.use {
      if (!it.isSuccessful) {
        val msg = "Http ${it.code}: ${it.body?.string() ?: "Unknown error"}"
        throw CustomException(ErrorCode.FAILED_TO_CALL_CLIENT, msg)
      }

      return it.body?.string() ?: throw CustomException(ErrorCode.CALL_RESULT_BODY_NULL)
    }
  }
}