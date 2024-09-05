package wh.duckbill.todo.api.model

data class TodoRequest(
    var title: String,
    var description: String,
    var done: Boolean = false
) {

}
