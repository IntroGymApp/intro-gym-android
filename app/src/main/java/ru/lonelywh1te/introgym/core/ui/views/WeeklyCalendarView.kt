package ru.lonelywh1te.introgym.core.ui.views

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.extensions.dp
import ru.lonelywh1te.introgym.databinding.ItemCalendarDayBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

// TODO: навести порядок

class WeeklyCalendarView(context: Context, attrs: AttributeSet? = null): LinearLayout(context, attrs) {
    private val weekViewPager = ViewPager2(context)
    private val textViewSelectedDate = TextView(context)

    private val currentDate = LocalDate.now()
    var selectedDate: LocalDate = currentDate
        private set(value) {
            if (field == value) return

            field = value
            (weekViewPager.adapter as? WeekViewPagerAdapter)?.notifyDataSetChanged()
            updateTextViewDate()

            onDateSelectedListener?.invoke(value)
        }

    private val centerWeekPosition = Int.MAX_VALUE / 2
    private var currentWeekPosition: Int = calculateCurrentWeekPosition()

    private val locale = Locale.getDefault()

    private var onWeekChangedListener: ((position: Int) -> Unit)? = null
    private var onDateSelectedListener: ((date: LocalDate) -> Unit)? = null

    private val markedDaysOnWeek: List<LocalDate> = listOf()

    init {
        gravity = CENTER
        orientation = VERTICAL

        initWeekViewPager()
        initTextViewDate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
            putString(SELECTED_DATE_KEY, selectedDate.toString())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            state.getString(SELECTED_DATE_KEY)?.let {
                selectedDate = LocalDate.parse(it)
            }

            weekViewPager.setCurrentItem(calculateCurrentWeekPosition(), false)

            val superState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                state.getParcelable(SUPER_STATE_KEY, Parcelable::class.java)
            } else {
                state.getParcelable(SUPER_STATE_KEY)
            }

            super.onRestoreInstanceState(superState)
        }
    }

    private fun initWeekViewPager() {
        weekViewPager.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            adapter = WeekViewPagerAdapter()
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            overScrollMode = OVER_SCROLL_NEVER
            setCurrentItem(currentWeekPosition, false)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    onWeekChangedListener?.invoke(position)
                }
            })
        }


        updateTextViewDate()
        addView(weekViewPager)
    }

    private fun initTextViewDate() {
        textViewSelectedDate.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setPadding(10.dp, 10.dp, 10.dp, 10.dp)
            }
            gravity = CENTER

            setTextAppearance(R.style.TextAppearance_IntroGym_Body_Regular)
        }

        addView(textViewSelectedDate)
    }


    private fun updateTextViewDate() {

        val dayOfWeekName = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

        val day = selectedDate.dayOfMonth

        val month = selectedDate.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

        val year = selectedDate.year

        textViewSelectedDate.text = "$dayOfWeekName, $month $day, $year"
    }

    fun getWeek(position: Int = 0): List<LocalDate> {
        val weekOffset = position - centerWeekPosition
        val startOfWeek = currentDate.plusWeeks(weekOffset.toLong()).with(DayOfWeek.MONDAY)

        return List(7) { startOfWeek.plusDays(it.toLong()) }
    }

    private fun calculateCurrentWeekPosition(): Int {
        val weekOffset = ChronoUnit.WEEKS.between(currentDate.with(DayOfWeek.MONDAY), selectedDate.with(DayOfWeek.MONDAY)).toInt()
        return centerWeekPosition + weekOffset
    }

    fun setOnChangeWeekListener(listener: (Int) -> Unit) {
        onWeekChangedListener = listener
    }

    fun setOnDateSelectedListener(listener: ((LocalDate) -> Unit)?) {
        onDateSelectedListener = listener
    }

    private inner class WeekViewPagerAdapter: RecyclerView.Adapter<WeekViewHolder>() {
        private val activeViewHolders = mutableListOf<WeekViewHolder>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
            val recyclerView = RecyclerView(parent.context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                adapter = DayItemAdapter(getWeek(currentWeekPosition))
                overScrollMode = OVER_SCROLL_NEVER

                layoutManager = GridLayoutManager(parent.context, 7)
            }

            return WeekViewHolder(recyclerView).also {
                activeViewHolders.add(it)
            }
        }

        override fun onViewRecycled(holder: WeekViewHolder) {
            super.onViewRecycled(holder)
            activeViewHolders.remove(holder)
        }

        override fun getItemCount(): Int = Int.MAX_VALUE

        override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
            holder.recyclerView.apply {
                val week = getWeek(position)
                (adapter as DayItemAdapter).update(week)
            }
        }
    }

    private inner class WeekViewHolder(rv: RecyclerView): RecyclerView.ViewHolder(rv) {
        val recyclerView: RecyclerView = rv
    }

    private inner class DayItemAdapter(
        private var days: List<LocalDate> = listOf(),
    ): RecyclerView.Adapter<DayItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DayItemViewHolder(binding)
        }

        override fun getItemCount(): Int = days.size

        override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
            val date = days[position]

            holder.bind(date, date == selectedDate)

            holder.itemView.setOnClickListener {
                selectedDate = date
            }
        }

        fun update(list: List<LocalDate>) {
            days = list
            notifyDataSetChanged()
        }
    }

    private inner class DayItemViewHolder(private val binding: ItemCalendarDayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(date: LocalDate, isSelected: Boolean) {
            binding.tvDayOfWeekShort.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
            binding.tvDayNumber.text = String.format(date.dayOfMonth.toString())

            binding.tvDayNumber.background = if (date == selectedDate) {
                ContextCompat.getDrawable(context, R.drawable.shape_circle_background)
            } else {
                null
            }

            binding.tvDayNumber.setTextColor(if (isSelected) {
                MaterialColors.getColor(binding.root, R.attr.igBackgroundColor)
            } else {
                MaterialColors.getColor(binding.root, R.attr.igTextPrimaryColor)
            })

            binding.ivIndicator.visibility = if (date in markedDaysOnWeek) VISIBLE else INVISIBLE
        }
    }

    companion object {
        private const val SUPER_STATE_KEY = "super_state_key"
        private const val SELECTED_DATE_KEY = "selected_date"
    }
}


