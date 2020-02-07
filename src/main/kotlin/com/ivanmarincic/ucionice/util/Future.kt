package com.ivanmarincic.ucionice.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier

private val executor = Executors.newFixedThreadPool(5)

fun <T> futureOf(supplier: Supplier<T>): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(supplier, executor)
}
