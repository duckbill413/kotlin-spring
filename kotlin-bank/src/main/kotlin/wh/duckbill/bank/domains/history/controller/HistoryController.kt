package wh.duckbill.bank.domains.history.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wh.duckbill.bank.domains.history.service.HistoryService
import wh.duckbill.bank.types.dto.History
import wh.duckbill.bank.types.dto.Response

@RestController
@RequestMapping("/api/v1/history")
class HistoryController(
  private val historyService: HistoryService,
) {

  @GetMapping("/{ulid}")
  fun history(@PathVariable ulid: String): Response<List<History>> {
    return historyService.history(ulid)
  }
}