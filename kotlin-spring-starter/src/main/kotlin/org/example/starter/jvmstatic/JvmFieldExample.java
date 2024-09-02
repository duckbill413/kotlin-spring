package org.example.starter.jvmstatic;

public class JvmFieldExample {
    public static void main(String[] args) {
//        int id = JvmFieldClass.Companion.getId();
//        String name = JvmFieldObject.INSTANCE.getName();

        // const val 에 대해서 는 직접 접근이 가능
        int code = JvmFieldClass.CODE;
        String familyName = JvmFieldObject.FAMILY_NAME;

        // 상수가 아닌 경우 @JvmField 어노테이션으로 직접 접근
        int id = JvmFieldClass.id;
        String name = JvmFieldObject.name;
    }
}
