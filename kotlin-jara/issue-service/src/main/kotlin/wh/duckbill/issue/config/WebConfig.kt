package wh.duckbill.issue.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

/**
 * WebMvcConfigurationSupport() 클래스
 *
 * 스프링 MVC의 기본기능을 지원하는 클래스
 */
@Configuration
class WebConfig(
    private val authUserHandlerArgResolver: AuthUserHandlerArgResolver,
) : WebMvcConfigurationSupport() {

    // 주입 받은 authUserHandlerArgResolver 를 HandlerMethodArgResolver 리스트에 추가
    // 스프링은 컨트롤러 메서드의 인자를 처리할 때 AuthUserHandlerArgResolver 를 사용
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.apply {
            add(authUserHandlerArgResolver) // AuthUser 를 받는 Controller 에 전달
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations(
                "classpath:/META-INF/resources/",
                "classpath:/resources/",
                "classpath:/static/",
                "classpath:/public/"
            )
    }
}

/**
 * HandlerMethodArgumentResolver
 *
 * 사용자 인증 정보를 처리하는 커스텀 HandlerMethodArgumentResolver
 * Controller의 메소드 인자로 AuthUser가 있는 경우 생성
 */
@Component
class AuthUserHandlerArgResolver : HandlerMethodArgumentResolver {

    /**
     * supportsParameter: 해당 메서드는 이 HandlerMethodArgumentResolver 가
     * 특정 컨트롤러 메서드 파라미터를 지원하는지 여부를 결정
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        AuthUser::class.java.isAssignableFrom(parameter.parameterType) // 파라미터가 AuthUser 타입이거나 그 서브클래스인지 확인하여, 맞다면 true 를 반환

    /**
     * resolveArgument
     *
     * 실제로 컨트롤러 메서드의 파라미터에 값을 제공하는 역할을 수행
     * 더미 AuthUser 를 생성
     * @param parameter 메서드 파라미터에 대한 메타정보를 제공
     * @param mavContainer MVC에서 사용하는 컨테이너로, 주로 ModelAndView 를 관리
     * @param webRequest 현재 HTTP 요청 정보를 담고 있는 객체
     * @param binderFactory 데이터 바인딩을 처리하는 팩토리
     * @return 인증 유저 정보
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        // TODO: 추후 재정의 예정
        return AuthUser(
            userId = 1,
            username = "테스트",
        )
    }

}

data class AuthUser(
    val userId: Long,
    val username: String,
    val profileUrl: String? = null,
)