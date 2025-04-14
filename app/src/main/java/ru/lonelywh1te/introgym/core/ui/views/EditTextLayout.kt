package ru.lonelywh1te.introgym.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isGone
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.extensions.dp

class EditTextLayout(
    context: Context,
    attrs: AttributeSet,
): LinearLayout(context, attrs) {

    private val editTextLinearLayout = LinearLayout(context)
    private val helperTextView = TextView(context)

    private val dividerColor = MaterialColors.getColor(this, R.attr.igHintColor)
    private val helperTextAppearance: Int = R.style.TextAppearance_IntroGym_Footnote_Regular

    private val errorMessageColor = MaterialColors.getColor(this, R.attr.igErrorColor)
    private val helperTextColor = MaterialColors.getColor(this, R.attr.igHintColor)

    private val editTextDividerMap = mutableMapOf<EditText, View>()

    init {
        orientation = VERTICAL

        initEditTextLinearLayout()
        initHelperTextView()

        setAttributeSet(attrs)
    }

    private fun setAttributeSet(attributeSet: AttributeSet?) {
        if (attributeSet == null) return

        context.withStyledAttributes(attributeSet, R.styleable.EditTextLayout) {
            val helperText = getString(R.styleable.EditTextLayout_helperMessage) ?: ""
            val isError = getBoolean(R.styleable.EditTextLayout_isErrorMessage, false)

            if (isError) setErrorMessage(helperText)
            else setHelperText(helperText)
        }
    }

    fun setHelperText(text: String) {
        helperTextView.apply {
            setTextColor(helperTextColor)
            setText(text)
            visibility = if (text.isBlank()) GONE else VISIBLE
        }
    }

    fun setErrorMessage(text: String?) {
        helperTextView.apply {
            setTextColor(errorMessageColor)
            setText(text)
            visibility = if (text.isNullOrBlank()) GONE else VISIBLE
        }
    }

    fun setEditTextVisibility(editText: EditText, isVisible: Boolean) {
        editText.visibility = if (isVisible) View.VISIBLE else View.GONE
        editTextDividerMap[editText]?.isGone = !isVisible
    }

    private fun initHelperTextView() {
        helperTextView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 10.dp, 0, 0)
                setTextAppearance(helperTextAppearance)
            }
            visibility = GONE
        }

        addView(helperTextView)
    }

    private fun initEditTextLinearLayout() {
        editTextLinearLayout.apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            background = ContextCompat.getDrawable(context, R.drawable.shape_rounded_plate_filled)
        }

        addView(editTextLinearLayout)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val editTexts = mutableListOf<EditText>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is EditText) editTexts.add(child)
        }

        editTexts.forEachIndexed { index, editText ->
            removeView(editText)

            if (index > 0) {
                val divider = View(context).apply {
                    setBackgroundColor(dividerColor)
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.dp)
                }
                editTextLinearLayout.addView(divider)
                editTextDividerMap[editText] = divider
            }

            editTextLinearLayout.addView(editText)
        }
    }
}