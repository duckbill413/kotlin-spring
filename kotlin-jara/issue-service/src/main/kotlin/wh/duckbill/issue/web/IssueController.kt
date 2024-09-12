package wh.duckbill.issue.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wh.duckbill.issue.config.AuthUser
import wh.duckbill.issue.service.IssueService
import wh.duckbill.issue.service.model.IssueRequest

@RestController
@RequestMapping("/api/v1/issues")
class IssueController(
    private val issueService: IssueService,
) {
    @PostMapping
    fun create(
        authUser: AuthUser,
        @RequestBody request: IssueRequest
    ) = issueService.create(authUser.userId, request)
}