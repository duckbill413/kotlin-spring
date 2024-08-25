package org.example.exception

import java.io.FileWriter

fun main() {
    // kotlin 의 try~with~resources
    FileWriter("test.txt")
        .use {
            it.write("Hello Kotlin")
        }
}