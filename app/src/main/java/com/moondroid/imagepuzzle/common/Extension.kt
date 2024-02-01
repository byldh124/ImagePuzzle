package com.moondroid.imagepuzzle.common

import android.app.Activity
import android.util.Log

fun Any.debug(msg: String) {
    Log.e("ImagePuzzle", "${this.javaClass.simpleName.trim()} | $msg")
}
fun Any.logException(msg: Throwable) {
    Log.e("ImagePuzzle", "${this.javaClass.simpleName.trim()} LogException | ${msg.stackTraceToString()}")
}

fun Activity.exitApp() {
    this.moveTaskToBack(true)
    this.finish()
    android.os.Process.killProcess(android.os.Process.myPid())
}