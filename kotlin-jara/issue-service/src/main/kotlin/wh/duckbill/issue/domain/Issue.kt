package wh.duckbill.issue.domain

import jakarta.persistence.*
import wh.duckbill.issue.domain.enums.IssuePriority
import wh.duckbill.issue.domain.enums.IssueStatus
import wh.duckbill.issue.domain.enums.IssueType

@Entity
@Table(name = "issue")
class Issue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column
    var userId: Long,
    @Column
    var summary: String,
    @Column
    var description: String,
    @Column
    @Enumerated(EnumType.STRING)
    var type: IssueType,
    @Column
    @Enumerated(EnumType.STRING)
    var priority: IssuePriority,
    @Column
    var status: IssueStatus,
) : BaseEntity() {
}