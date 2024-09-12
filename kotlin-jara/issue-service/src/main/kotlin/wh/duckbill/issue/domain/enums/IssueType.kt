package wh.duckbill.issue.domain.enums

enum class IssueType {
    BUG, TASK;

    companion object {
        fun of(type: String) = valueOf(type.uppercase())

        // operator invoke
        operator fun invoke(type: String) = valueOf(type.uppercase())
    }
}

fun main() {
    val type = "bug"
    val issueType1 = IssueType.of(type)
    val issueType2 = IssueType(type) // 함수를 생성자 처럼 사용하여 객체 초기화
    println("$issueType1 $issueType2")
}
