package org.example.starter.extensions;

public class ExtensionsExample {
    public static void main(String[] args) {
        // 코틀린의 확장함수를 사용했을때 자바에서의 호출
//        "ABCD".first();
//        "ABCD".addFirst("Z");

        System.out.println(MyExtensionsKt.first("ABCD"));
        System.out.println(MyExtensionsKt.addFirst("ABCD", 'Z'));
    }
}
