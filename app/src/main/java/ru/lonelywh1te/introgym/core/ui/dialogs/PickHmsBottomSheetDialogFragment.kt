package ru.lonelywh1te.introgym.core.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.databinding.FragmentPickHmsBottomSheetDialogBinding
import java.time.LocalTime

class PickHmsBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private var _binding: FragmentPickHmsBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int {
        return super.getTheme()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPickHmsBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("hours", binding.npHours.value)
        outState.putInt("minutes", binding.npMinutes.value)
        outState.putInt("seconds", binding.npSeconds.value)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            binding.npHours.value = it.getInt("hours")
            binding.npMinutes.value = it.getInt("minutes")
            binding.npSeconds.value = it.getInt("seconds")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.npHours.apply {
            minValue = 0
            maxValue = 23
        }

        binding.npMinutes.apply {
            minValue = 0
            maxValue = 59
        }

        binding.npSeconds.apply {
            minValue = 0
            maxValue = 59
        }

        binding.btnSelect.setOnClickListener {
            setResult()
            dismiss()
        }

        arguments?.let {
            val localtimeString = it.getString("localtime")

            localtimeString?.let {
                val localtime = LocalTime.parse(localtimeString, DateAndTimeStringFormatUtils.fullTimeFormatter)

                binding.npHours.value = localtime.hour
                binding.npMinutes.value = localtime.minute
                binding.npSeconds.value = localtime.second
            }
        }
    }

    private fun setResult() {
        val hours = binding.npHours.value
        val minutes = binding.npMinutes.value
        val seconds = binding.npSeconds.value

        val localTime = LocalTime.of(hours, minutes, seconds)
        val localDateTimeString = localTime.format(DateAndTimeStringFormatUtils.fullTimeFormatter)

        parentFragmentManager.setFragmentResult(REQUEST_KEY, Bundle().apply { putString(RESULT_LOCALTIME_STRING, localDateTimeString) })
    }

    companion object {
        const val TAG = "PickHmsBottomSheetDialogFragment"
        const val REQUEST_KEY = "pick_hours_minutes_seconds_request"
        const val RESULT_LOCALTIME_STRING = "result_localtime_string"

        fun instance(localtime: LocalTime? = null): PickHmsBottomSheetDialogFragment {
            val bundle = Bundle().apply {
                localtime?.let { putString("localtime", localtime.format(DateAndTimeStringFormatUtils.fullTimeFormatter))  }
            }

            return PickHmsBottomSheetDialogFragment().apply { arguments =  bundle}
        }
    }
}