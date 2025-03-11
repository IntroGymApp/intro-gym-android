package ru.lonelywh1te.introgym.core.ui.extensions

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import com.google.android.material.color.MaterialColors

fun TextView.setClickableSpan(substring: String = this.text.toString(), onClick: () -> Unit) {
    val spannable = SpannableString(this.text)

    val startIndex = this.text.toString().indexOf(substring)
    val endIndex = startIndex + substring.length

    if (startIndex == -1) return

    spannable.setSpan(object: ClickableSpan() {
        override fun onClick(p0: View) {
            onClick.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            highlightColor = Color.TRANSPARENT
        }

    }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setColorSpan(substring: String = this.text.toString(), @AttrRes colorAttr: Int) {
    val spannable = SpannableString(this.text)
    val color = MaterialColors.getColor(this, colorAttr)

    val startIndex = this.text.toString().indexOf(substring)
    val endIndex = startIndex + substring.length

    if (startIndex == -1) return

    spannable.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance()
}