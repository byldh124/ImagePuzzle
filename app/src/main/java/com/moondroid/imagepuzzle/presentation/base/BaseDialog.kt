package com.moondroid.imagepuzzle.presentation.base

import android.app.Dialog
import android.content.Context
import com.moondroid.imagepuzzle.R

open class BaseDialog(context: Context) : Dialog(context, R.style.DialogTheme) {
    fun exit() {
        if (isShowing) super.cancel()
    }
}

inline var BaseDialog.isShow: Boolean
    get() = isShowing
    set(value) {
        if (value) this.show() else this.exit()
    }

