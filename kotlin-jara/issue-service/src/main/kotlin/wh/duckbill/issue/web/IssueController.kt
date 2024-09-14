package wh.duckbill.issue.web

import org.springframework.web.bind.annotation.*
import wh.duckbill.issue.config.AuthUser
import wh.duckbill.issue.domain.enums.IssueStatus
import wh.duckbill.issue.service.IssueService
import wh.duckbill.issue.service.model.IssueRequest

@RestController
@RequestMapping("/api/v1/issues")
class IssueController(
    private val issueService: IssueService,
) {
    /**
     * 이슈 생성 API
     * @param authUser 인증 유저 정보
     * @param request 생성하고자 하는 이슈 정보
     * @return IssueResponse 생성된 이슈 정보
     */
    @PostMapping
    fun create(
        authUser: AuthUser,
        @RequestBody request: IssueRequest
    ) = issueService.create(authUser.userId, request)

    @GetMapping
    fun getAll(
        authUser: AuthUser,
        @RequestParam(required = false, defaultValue = "TODO") status: IssueStatus
    ) = issueService.getAll(status)

    @GetMapping("/{id}")
    fun get(
        authUser: AuthUser,
        @PathVariable id: Long
    ) = issueService.get(id)
}