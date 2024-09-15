package wh.duckbill.issue.web

import org.springframework.web.bind.annotation.*
import wh.duckbill.issue.config.AuthUser
import wh.duckbill.issue.service.CommentService
import wh.duckbill.issue.service.model.CommentRequest

@RestController
@RequestMapping("/api/v1/issues/{issueId}/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @PostMapping
    fun create(
        authUser: AuthUser,
        @PathVariable issueId: Long,
        @RequestBody request: CommentRequest,
    ) = commentService.create(issueId, authUser.userId, authUser.username, request)
}