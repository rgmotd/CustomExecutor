import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

fun main() {
    val exec = CustomExecutor<Int, Int>(CoroutineScope(Dispatchers.Default), 2)

    runBlocking {
        val futures = exec.map(::longRunningTask, listOf(1, 2, 3, 4))

        futures.forEach {
            println("${it.getResult()}")
        }
    }
}

fun getTime(): String? {
    val sdf = SimpleDateFormat("hh:mm:ss")
    return sdf.format(Date())
}

suspend fun longRunningTask(number: Int): Int {
    delay(2000)
    return number * 2
}