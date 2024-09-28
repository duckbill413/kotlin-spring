package wh.duckbill.userservice.exception

sealed class ServerException(
    val code: Int,
    override val message: String,
) : RuntimeException(message)

data class UserExistsException(
    override val message: String = "이미 존재하는 유저 입니다.",
) : ServerException(400, message)

data class UserNotFoundException(
    override val message: String = "존재하지 않는 유저 입니다.",
) : ServerException(404, message)

data class PasswordNotMatchedException(
    override val message: String = "패스워드가 잘못 되었습니다.",
) : ServerException(400, message)