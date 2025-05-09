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

        binding.groupPeriodButtons.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnWeekPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Week())
                    binding.btnMonthPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Month())
                    binding.btnYearPeriod.id -> viewModel.setTotalWeightPeriod(StatsPeriod.Year())
                }
            }
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.totalWeightEntries.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { entries ->
                setTotalWeightData(entries)
            }
            .launchIn(lifecycleScope)

        viewModel.totalWeightPeriod.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { period ->
                val startDate = period.startLocalDate.format(DateAndTimeStringFormatUtils.dateFormatter)
                val endDate = period.endLocalDate.format(DateAndTimeStringFormatUtils.dateFormatter)

                binding.tvPeriodDates.text = "$startDate - $endDate"
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
            setData(BarData(dataSet))
            description = null
            legend.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)
            }

            axisRight.apply {
                typeface = ResourcesCompat.getFont(requireContext(), R.font.geist_regular)
                axisMinimum = 0f
            }
            axisLeft.isEnabled = false

            invalidate()
        }

        binding.tvAverageWeight.text = getString(
            R.string.placeholder_kg,
            String.format(Locale.US, "%.2f", viewModel.getAverageWeight())
        )
    }
}