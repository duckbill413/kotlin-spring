package org.example.starter.jvmstatic;

public class JvmStaticExample {
    public static void main(String[] args) {
//        String hello = HelloClass.Companion.hello();
//        String hi = HiObject.INSTANCE.hi();

        String hello = HelloClass.hello();
        String hi = HiObject.hi();
        System.out.println(hello + "\t" + hi);
    }
}
