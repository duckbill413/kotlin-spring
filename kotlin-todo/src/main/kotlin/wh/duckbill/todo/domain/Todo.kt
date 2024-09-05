package org.example.todo.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "todos")
class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,
    @Column(name = "title")
    var title: String,

    /**
     * Large Object 의 줄임말
     *
     * 큰 크기의 데이터를 담아야 하는 컬럼은 LOB 형식을 갖는다.
     * LOB 형식은 일반적으로 BLOB(Binary Large Object), CLOB(Character Large Object)로 구분되는데,
     * BLOB은 이진 데이터를 저장하는데 사용되며, CLOB는 문자 데이터를 저장하는데 사용된다.
     * @Lob 는 특별한 속성이 없다.
     */
    @Lob
    @Column(name = "description")
    var description: String,
    @Column(name = "done")
    var done: Boolean,
    @Column(name = "created_at")
    var createdAt: LocalDateTime,
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) {
    fun update(title: String, description: String, done: Boolean) {
        this.title = title
        this.description = description
        this.done = done
        this.updatedAt = LocalDateTime.now()
    }
}