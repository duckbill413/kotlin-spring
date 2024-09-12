package wh.duckbill.issue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class IssueServiceApplication

fun main(args: Array<String>) {
    runApplication<IssueServiceApplication>(*args)
}
