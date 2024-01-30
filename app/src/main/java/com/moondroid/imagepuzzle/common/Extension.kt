package com.moondroid.imagepuzzle.common

import android.util.Log

fun Any.debug(msg: String) {
    Log.e("ImagePuzzle", "${this.javaClass.simpleName.trim()} | $msg")
}
fun Any.logException(msg: Throwable) {
    Log.e("ImagePuzzle", "${this.javaClass.simpleName.trim()} LogException | ${msg.stackTraceToString()}")
}
