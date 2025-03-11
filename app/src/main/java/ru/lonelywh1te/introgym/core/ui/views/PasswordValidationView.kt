package ru.lonelywh1te.introgym.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import ru.lonelywh1te.introgym.R

class PasswordValidationView<T>(
    context: Context,
    attributeSet: AttributeSet?,
): LinearLayout(context, attributeSet) {
    var errorRequirements: Map<T, String> = mapOf()
        set(value) {
            field = value
            resetView()
            initIndicatorLayout()
            initTextRequirements()
        }

    private val indicatorLayout = LinearLayout(context)
    private val indicators: MutableList<View> = mutableListOf()

    private val textViewRequirements: MutableMap<T, TextView> = mutableMapOf()

    @ColorRes var defaultIndicatorColor: Int = R.color.plate_color
    @ColorRes var textRequirementsIncompleteColor: Int = R.color.dark_hint_color
    @ColorRes var requirementCompleteColor: Int = R.color.accent_color

    var textRequirementsAppearance: Int = R.style.TextAppearance_IntroGym_Footnote_Regular

    init {
        orientation = VERTICAL
        setAttributeSet(attributeSet)
    }

    private fun setAttributeSet(attributeSet: AttributeSet?) {
        if (attributeSet == null) return

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PasswordValidationView)
        val errorCount = typedArray.getInt(R.styleable.PasswordValidationView_errorCount, 0)
        typedArray.recycle()

        if (isInEditMode) {
            val fakeErrorRequirements = mutableMapOf<T, String>()
            val emptyErrorList = List(errorCount) { "$it" as T }

            emptyErrorList.forEachIndexed { index, fakeError ->
                fakeErrorRequirements[fakeError] = "Условие №${index + 1}"
            }

            errorRequirements = fakeErrorRequirements
        }
    }

    fun setCurrentErrors(errors: List<T>) {
        updateView(errors)
    }

    private fun resetView() {
        indicatorLayout.removeAllViews()
        textViewRequirements.clear()
    }

    private fun initIndicatorLayout() {
        indicatorLayout.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 5.dp())
        }

        initIndicators()
        addView(indicatorLayout)
    }

    private fun initIndicators() {

        indicatorLayout.weightSum = errorRequirements.size.toFloat()

        for (i in 0 until errorRequirements.size) {

            val indicator = View(context).apply {
                background = ContextCompat.getDrawable(context, R.drawable.shape_rounded_plate)
                background.setTint(ContextCompat.getColor(context, defaultIndicatorColor))
            }

            val indicatorLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)

            when (i) {
                0 -> indicatorLayoutParams.setMargins(0, 0, 2.5f.dp(), 0)
                errorRequirements.size - 1 -> indicatorLayoutParams.setMargins(2.5f.dp(), 0, 0, 0)
                else -> indicatorLayoutParams.setMargins(2.5f.dp(), 0, 2.5f.dp(), 0)
            }

            indicator.layoutParams = indicatorLayoutParams
            indicators.add(indicator)
            indicatorLayout.addView(indicator)

        }
    }

    private fun initTextRequirements() {
        for ((error, requirement) in errorRequirements) {
            val textView = TextView(context).apply {
                text = requirement
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 10.dp(), 0, 0)
                }
                setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_point), null, null, null)
                setTextAppearance(textRequirementsAppearance)
                TextViewCompat.setCompoundDrawableTintList(this, ContextCompat.getColorStateList(context, textRequirementsIncompleteColor))
                compoundDrawablePadding = 10.dp()
            }

            textViewRequirements[error] =  textView
            addView(textView)
        }
    }

    private fun updateView(currentErrors: List<T>) {
        val completeColor = ContextCompat.getColor(context, requirementCompleteColor)
        val inCompleteColor = ContextCompat.getColor(context, defaultIndicatorColor)

        indicators.forEachIndexed { index, view ->
            view.background.setTint(if (index < indicators.size - currentErrors.size) completeColor else inCompleteColor)
        }

        textViewRequirements.forEach { (validationError, textView) ->
            val color = if (currentErrors.contains(validationError)) textRequirementsIncompleteColor else requirementCompleteColor
            TextViewCompat.setCompoundDrawableTintList(textView, ContextCompat.getColorStateList(context, color))
        }
    }


    private fun Int.dp(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()
    private fun Float.dp(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics).toInt()
}