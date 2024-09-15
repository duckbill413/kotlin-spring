package wh.duckbill.issue.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wh.duckbill.issue.domain.Issue
import wh.duckbill.issue.domain.IssueRepository
import wh.duckbill.issue.domain.enums.IssueStatus
import wh.duckbill.issue.exception.NotFoundException
import wh.duckbill.issue.service.model.IssueRequest
import wh.duckbill.issue.service.model.IssueResponse

@Service
class IssueService(
    private val issueRepository: IssueRepository,
) {
    @Transactional
    fun create(userId: Long, request: IssueRequest): IssueResponse {
        val issue = Issue(
            summary = request.summary,
            description = request.description,
            userId = userId,
            type = request.type,
            priority = request.priority,
            status = request.status,
        )

        return IssueResponse(issueRepository.save(issue))
    }

    @Transactional(readOnly = true)
    fun getAll(status: IssueStatus): List<IssueResponse> =
        issueRepository.findAllByStatusOrderByCreatedAtDesc(status)?.map { IssueResponse(it) }
            ?: emptyList()

    @Transactional(readOnly = true)
    fun get(id: Long): IssueResponse =
        IssueResponse(
            issueRepository.findByIdOrNull(id)
                ?: throw NotFoundException("이슈가 존재하지 않습니다.")
        )

    @Transactional
    fun edit(userId: Long, id: Long, request: IssueRequest): IssueResponse {
        val issue = issueRepository.findByIdOrNull(id) ?: throw NotFoundException("이슈가 존재하지 않습니다.")

        return with(issue) {
            summary = request.summary
            description = request.description
            this.userId = userId
            type = request.type
            priority = request.priority
            status = request.status
            IssueResponse(issueRepository.save(this))
        }
    }

    @Transactional
    fun delete(id: Long) {
        issueRepository.deleteById(id)
    }
}