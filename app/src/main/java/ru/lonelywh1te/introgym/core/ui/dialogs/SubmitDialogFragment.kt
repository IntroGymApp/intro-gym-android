package ru.lonelywh1te.introgym.core.ui.dialogs

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import ru.lonelywh1te.introgym.databinding.FragmentSubmitDialogBinding

class SubmitDialogFragment: DialogFragment() {
    private var _binding: FragmentSubmitDialogBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var message: String? = null
    private var icon: Drawable? = null
    @ColorInt private var iconTint: Int? = null
    private var positiveButtonText: String? = null
    private var onPositiveButtonClick: ((dialog: DialogFragment) -> Unit)? = null
    private var negativeButtonText: String? = null
    private var onNegativeButtonClick: ((dialog: DialogFragment) -> Unit)? = null
    private var onDismissDialog: ((dialog: DialogFragment) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { dismiss() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubmitDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = title
        binding.tvDescription.text = message

        binding.ivIcon.apply {
            visibility = if (icon == null) View.GONE else View.VISIBLE

            icon?.let {
                setImageDrawable(icon)
                drawable?.setTint(iconTint ?: ContextCompat.getColor(context, android.R.color.black))
            }
        }

        binding.btnPositive.apply {
            visibility = if (onPositiveButtonClick == null) View.GONE else View.VISIBLE

            onPositiveButtonClick?.let {
                text = positiveButtonText
                setOnClickListener {
                    it(this@SubmitDialogFragment)
                }
            }
        }
        binding.btnNegative.apply {
            visibility = if (onNegativeButtonClick == null) View.GONE else View.VISIBLE

            onNegativeButtonClick?.let {
                text = negativeButtonText
                setOnClickListener {
                    it(this@SubmitDialogFragment)
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissDialog?.invoke(this)
    }

    class Builder {
        private var title: String? = null
        private var message: String? = null
        private var icon: Drawable? = null
        private var iconTint: Int? = null
        private var positiveButtonText: String? = null
        private var onPositiveButtonClick: ((dialog: DialogFragment) -> Unit)? = null
        private var negativeButtonText: String? = null
        private var onNegativeButtonClick: ((dialog: DialogFragment) -> Unit)? = null
        private var onDismissDialog: ((dialog: DialogFragment) -> Unit)? = null

        fun setTitle(title: String) = apply { this.title = title }
        fun setMessage(message: String) = apply { this.message = message }
        fun setIcon(icon: Drawable?) = apply { this.icon = icon }
        fun setIconTint(@ColorInt color: Int) = apply { this.iconTint = color }
        fun setPositiveButton(text: String, onPositiveButtonClick: ((dialog: DialogFragment) -> Unit)?) = apply {
            this.positiveButtonText = text
            this.onPositiveButtonClick = onPositiveButtonClick
        }
        fun setNegativeButton(text: String, onNegativeButtonClick: ((dialog: DialogFragment) -> Unit)?) = apply {
            this.negativeButtonText = text
            this.onNegativeButtonClick = onNegativeButtonClick
        }
        fun setOnDismissDialog(onDismissDialog: ((dialog: DialogFragment) -> Unit)?) = apply {
            this.onDismissDialog = onDismissDialog
        }

        fun build(): SubmitDialogFragment {
            val fragment = SubmitDialogFragment()

            fragment.title = title
            fragment.icon = icon
            fragment.iconTint = iconTint
            fragment.message = message
            fragment.positiveButtonText = positiveButtonText
            fragment.onPositiveButtonClick = onPositiveButtonClick
            fragment.negativeButtonText = negativeButtonText
            fragment.onNegativeButtonClick = onNegativeButtonClick
            fragment.onDismissDialog = onDismissDialog

            return fragment
        }
    }

    companion object {
        const val TAG = "WarningDialogFragment"
    }
}