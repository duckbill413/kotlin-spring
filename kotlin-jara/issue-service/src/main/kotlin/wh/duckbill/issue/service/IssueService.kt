package wh.duckbill.issue.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wh.duckbill.issue.domain.Issue
import wh.duckbill.issue.domain.IssueRepository
import wh.duckbill.issue.domain.enums.IssueStatus
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
    fun getAll(status: IssueStatus): List<IssueResponse>? {
        return issueRepository.findAllByStatusOrderByCreatedAtDesc(status)
            ?.map { IssueResponse(it) }
    }
}