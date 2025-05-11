package ru.lonelywh1te.introgym.features.stats.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.databinding.FragmentStatsBinding
import ru.lonelywh1te.introgym.features.stats.domain.StatsPeriod
import java.util.Locale

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<StatsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupTotalWeightPeriodButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnWeekTotalWeightPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Week())
                    binding.btnMonthTotalWeightPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Month())
                    binding.btnYearTotalWeightPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Year())
                }
            }
        }

        binding.groupMusclesPeriodButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnWeekMusclesPeriod.id -> viewModel.setMusclesPeriod(StatsPeriod.Week())
                    binding.btnMonthMusclesPeriod.id -> viewModel.setMusclesPeriod(StatsPeriod.Month())
                    binding.btnYearMusclesPeriod.id -> viewModel.setMusclesPeriod(StatsPeriod.Year())
                }
            }
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.totalWeightData.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { entries ->
                setTotalWeightData(entries)
            }
            .launchIn(lifecycleScope)

        viewModel.musclesData.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { entries ->
                setMusclesData(entries)
            }
            .launchIn(lifecycleScope)

        viewModel.totalWeightPeriod.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { period ->
                binding.tvTotalWeightPeriodDates.text = getPeriodString(period)
            }
            .launchIn(lifecycleScope)

        viewModel.musclesPeriod.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { period ->
                binding.tvMusclesPeriodDates.text = getPeriodString(period)
            }
            .launchIn(lifecycleScope)
    }

    private fun setTotalWeightData(entries: List<BarEntry>) {
        val dataSet = BarDataSet(entries, null).apply {
            setColor(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value != 0f) {
                        value.toString()
                    } else {
                        ""
                    }
                }
            }
            valueTypeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)

        }

        binding.chartTotalWeight.apply {
            data = BarData(dataSet)
            description = null
            legend.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)
            }

            axisLeft.apply {
                axisMinimum = 0f
                isEnabled = false
            }

            axisRight.apply {
                typeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)
                axisMinimum = 0f
            }

            animateY(200)
            invalidate()
        }

        binding.tvAverageWeight.text = getString(
            R.string.placeholder_kg,
            String.format(Locale.US, "%.2f", viewModel.getAverageWeight())
        )
    }

    private fun setMusclesData(entries: List<RadarEntry>) {
        val labels = viewModel.getMuscleLabels()

        val dataSet = RadarDataSet(entries, null).apply {
            setColor(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
            setDrawFilled(true)
            setDrawValues(false)
            fillColor = MaterialColors.getColor(binding.root, R.attr.igPrimaryColor)
        }

        binding.chartMuscles.apply {
            data = RadarData(dataSet)

            description = null
            legend.isEnabled = false

            xAxis.apply {
                typeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)
            }

            yAxis.apply {
                axisMinimum = 0f
                isEnabled = false
            }

            if (labels.isNotEmpty()) {
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                notifyDataSetChanged()
            }

            animateY(200)
        }
    }

    private fun getPeriodString(period: StatsPeriod): String {
        val startDate = period.startLocalDate.format(DateAndTimeStringFormatUtils.dateFormatter)
        val endDate = period.endLocalDate.format(DateAndTimeStringFormatUtils.dateFormatter)

        return "$startDate - $endDate"
    }
}