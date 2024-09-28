package wh.duckbill.userservice.service

import org.springframework.stereotype.Service
import wh.duckbill.userservice.domain.entity.User
import wh.duckbill.userservice.domain.repository.UserRepository
import wh.duckbill.userservice.exception.UserExistsException
import wh.duckbill.userservice.model.SignUpRequest
import wh.duckbill.userservice.utils.BCryptUtils

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExistsException()
            }

            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username = username,
            )

            userRepository.save(user)
        }
    }
}