package wh.duckbill.bank.domains.bank.controller

import org.springframework.web.bind.annotation.*
import wh.duckbill.bank.domains.bank.service.BankService
import wh.duckbill.bank.types.dto.Response
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/bank")
class BankController(
  private val bankService: BankService,
) {

  @PostMapping("/create/{ulid}")
  fun createAccount(@PathVariable ulid: String): Response<String> {
    return bankService.createAccount(ulid)
  }

  @GetMapping("/balance/{userUlid}/{accountUlid}")
  fun balance(
    @PathVariable userUlid: String,
    @PathVariable accountUlid: String
  ): Response<BigDecimal> {
    return bankService.balance(userUlid, accountUlid)
  }

  @PostMapping("/remove/{userUlid}/{accountUlid}")
  fun removeAccount(
    @PathVariable userUlid: String,
    @PathVariable accountUlid: String
  ): Response<String> {
    return bankService.removeAccount(userUlid, accountUlid)
  }
}