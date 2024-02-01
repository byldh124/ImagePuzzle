package com.moondroid.imagepuzzle.data

import androidx.annotation.Keep

@Keep
data class Response(
    val code : Int = 0,
    val message: String = "",
    val result: List<String> = emptyList()
)

@Keep
data class SimpleResponse(
    val code: Int = 0,
    val message: String = ""
)