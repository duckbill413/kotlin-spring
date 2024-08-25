package org.example.exception

fun getStr(): Nothing = throw Exception("예외 발생 기본 값으로 초기화")

fun main() {
    // runCatching,getOrElse
    println("=========== runCatching ===========")
    val result = runCatching {
        getStr()
    }.getOrElse {
        println(it.message)
        "기본값"
    }
    println(result)

    // exceptionOrNull
    println("=========== exceptionOrNull ===========")
    val exceptionOrNull = runCatching { getStr() }
        .exceptionOrNull()

    exceptionOrNull?.let { error ->
        println(error.message)
    }

    // getOrDefault
    // 실패시 default value 반환
    println("=========== getOrDefault ===========")
    runCatching { getStr() } // 에러 발생
        .getOrDefault("기본 값")
        .let { println(it) }

    // map 내부에서 실패시 에러 처리
    println("=========== mapCatching ===========")
    runCatching { "안녕" }
        .mapCatching {
            throw Exception("예외") // 에러 발생
        }.getOrDefault("mapCatching Default")
        .let { println(it) }

    // recover 실패시 복구
    // recover 안에서 다른 로직 수행 가능
    // recoverCatching 도 존재 (recover 내부의 에러를 처리)
    println("=========== recover ===========")
    runCatching { getStr() } // 에러 발생
        .recover { "recover" }
        .getOrNull()
        .let { println(it) }

    // map, recover 같은 경우 mapCatching, recoverCatching 을 사용하지 않으면
    // getOrNull 함수는 map, recover 함수 내에서 발생한 에러에 대한 재처리가 이루어지지 않음
    runCatching { getStr() } // 에러 발생
        .recover { getStr() } // recover 안에서도 에러가 발생
        .getOrNull()
        .let { println("recover success") } // 재처리가 이루어진다면 `recover success` 출력
}