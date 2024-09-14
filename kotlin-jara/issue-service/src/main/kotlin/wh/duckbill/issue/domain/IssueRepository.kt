package wh.duckbill.issue.domain

import org.springframework.data.jpa.repository.JpaRepository
import wh.duckbill.issue.domain.enums.IssueStatus

interface IssueRepository : JpaRepository<Issue, Long> {
    fun findAllByStatusOrderByCreatedAtDesc(status: IssueStatus): List<Issue>?
}