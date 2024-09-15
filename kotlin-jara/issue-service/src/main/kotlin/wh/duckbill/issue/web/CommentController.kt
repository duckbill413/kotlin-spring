package wh.duckbill.issue.web

import org.springframework.http.HttpStatus
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

    @PutMapping("/{commentId}")
    fun edit(
        authUser: AuthUser,
        @PathVariable commentId: Long,
        @RequestBody request: CommentRequest
    ) = commentService.edit(commentId, authUser.userId, request)

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        authUser: AuthUser,
        @PathVariable issueId: Long,
        @PathVariable commentId: Long,
    ) = commentService.delete(issueId, commentId, authUser.userId)
}