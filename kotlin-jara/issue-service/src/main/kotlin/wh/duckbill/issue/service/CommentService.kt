package wh.duckbill.issue.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wh.duckbill.issue.domain.Comment
import wh.duckbill.issue.domain.CommentRepository
import wh.duckbill.issue.domain.IssueRepository
import wh.duckbill.issue.exception.NotFoundException
import wh.duckbill.issue.service.model.CommentRequest
import wh.duckbill.issue.service.model.CommentResponse
import wh.duckbill.issue.service.model.toResponse

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val issueRepository: IssueRepository,
) {
    @Transactional
    fun create(issueId: Long, userId: Long, username: String, request: CommentRequest): CommentResponse {
        val issue = issueRepository.findByIdOrNull(issueId)
            ?: throw NotFoundException("이슈가 존재하지 않습니다.")

        val comment = Comment(
            issue = issue,
            userId = userId,
            username = username,
            body = request.body,
        )
        issue.comments.add(comment)
        return commentRepository.save(comment).toResponse()
    }

    @Transactional
    fun edit(commentId: Long, userId: Long, request: CommentRequest): CommentResponse? {
        return commentRepository.findByIdAndUserId(commentId, userId)?.run {
            body = request.body
            commentRepository.save(this).toResponse()
        }
    }
}