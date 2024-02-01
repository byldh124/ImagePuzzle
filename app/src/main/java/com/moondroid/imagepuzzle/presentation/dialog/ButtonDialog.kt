package com.moondroid.imagepuzzle.presentation.dialog

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import com.moondroid.imagepuzzle.databinding.DialogButtonBinding
import com.moondroid.imagepuzzle.presentation.base.BaseDialog

class ButtonDialog private constructor(
    context: Context,
    private val message: String = "",
    private val positiveText: String = "확인",
    private val positiveOnClick: () -> Unit,
    private val negativeText: String = "취소",
    private val negativeOnClick: () -> Unit,
    private val showNegativeButton: Boolean = false,
    private val cancelable: Boolean = true,
) : BaseDialog(context) {

    private lateinit var binding: DialogButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogButtonBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
    }

    override fun show() {
        super.show()
        binding.tvMessage.text = message
        binding.btnConfirm.text = positiveText
        binding.btnCancel.text = negativeText
        binding.btnConfirm.setOnClickListener {
            super.cancel()
            positiveOnClick()
        }

        binding.btnCancel.setOnClickListener {
            super.cancel()
            negativeOnClick()
        }

        binding.btnCancel.isVisible = showNegativeButton

        setCancelable(cancelable)
    }

    class Builder(private val context: Context) {
        var message: String = ""
        var cancelable = true

        private var positiveText = "확인"
        private var negativeText = "취소"
        private var positiveButtonOnClick: () -> Unit = {}
        private var negativeButtonOnClick: () -> Unit = {}
        private var showNegativeButton = false

        fun setPositiveButton(text: String, onClick: () -> Unit) {
            positiveText = text
            positiveButtonOnClick = onClick
        }

        fun setNegativeButton(text: String, onClick: () -> Unit) {
            showNegativeButton = true
            negativeText = text
            negativeButtonOnClick = onClick
        }

        fun show() {
            ButtonDialog(
                context,
                message,
                positiveText,
                positiveButtonOnClick,
                negativeText,
                negativeButtonOnClick,
                showNegativeButton,
                cancelable
            ).show()
        }
    }
}

