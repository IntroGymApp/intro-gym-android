package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.ui.dialogs.PickHmsBottomSheetDialogFragment
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.core.ui.utils.InputFilters
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExerciseSetEditorBottomSheetBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExerciseSetEditorViewModel
import java.time.LocalTime
import java.util.UUID

class WorkoutExerciseSetEditorFragment: BottomSheetDialogFragment() {
    private var _binding: FragmentWorkoutExerciseSetEditorBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutExerciseSetEditorViewModel by viewModel()

    private lateinit var mapOfButtonAndInputs: Map<Button, EditText>
    private lateinit var mapOfButtonsAndEfforts: Map<Button, Effort>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(EXERCISE_SET_ID_KEY)?.let {
            viewModel.setWorkoutExerciseSetId(UUID.fromString(it))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutExerciseSetEditorBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapOfButtonAndInputs = mapOf(
            (binding.btnSelectWeight to binding.etWeight),
            (binding.btnSelectReps to binding.etReps),
            (binding.btnSelectTime to binding.etTime),
            (binding.btnSelectDistance to binding.etDistance),
        )

        mapOfButtonsAndEfforts = mapOf(
            (binding.btnWarmUpEffort to Effort.WARMUP),
            (binding.btnLowEffort to Effort.LOW),
            (binding.btnMidEffort to Effort.MEDIUM),
            (binding.btnHardEffort to Effort.HARD),
            (binding.btnMaxEffort to Effort.MAX),
        )

        binding.etTime.setOnClickListener {
            showPickHmsDialog()
        }

        binding.btnSave.setOnClickListener {
            saveWorkoutExerciseSet()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteWorkoutExerciseSet()
        }

        binding.etWeight.filters = arrayOf(InputFilters.DecimalDigitsInputFilter())

        startCollectFlows(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapOfButtonAndInputs.forEach { (button, _) ->
            outState.putBoolean(button.id.toString(), button.isSelected)
        }
    }

    private fun startCollectFlows(savedInstanceState: Bundle?) {
        viewModel.workoutExerciseSet.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach {
                setWorkoutExerciseSetData(it, savedInstanceState)
            }
            .launchIn(lifecycleScope)


        viewModel.changesSaved.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                dismiss()
            }
            .launchIn(lifecycleScope)
    }

    private fun showPickHmsDialog() {
        val localtime: LocalTime? = if (binding.etTime.text.toString().isNotBlank()) {
            LocalTime.parse(binding.etTime.text.toString(), DateAndTimeStringFormatUtils.fullTimeFormatter)
        } else {
            null
        }

        if (childFragmentManager.findFragmentByTag(PickHmsBottomSheetDialogFragment.TAG) == null) {
            PickHmsBottomSheetDialogFragment.instance(localtime).show(childFragmentManager, PickHmsBottomSheetDialogFragment.TAG)
            setPickHmsDialogResultListener()
        }
    }

    private fun setPickHmsDialogResultListener() {
        childFragmentManager.setFragmentResultListener(PickHmsBottomSheetDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val localtime = bundle.getString(PickHmsBottomSheetDialogFragment.RESULT_LOCALTIME_STRING)?.let {
                LocalTime.parse(it, DateAndTimeStringFormatUtils.fullTimeFormatter)
            }

            localtime?.let {
                binding.etTime.setText(localtime.format(DateAndTimeStringFormatUtils.fullTimeFormatter))
            }

            return@setFragmentResultListener
        }
    }

    private fun setWorkoutExerciseSetData(workoutExerciseSet: WorkoutExerciseSet, savedInstanceState: Bundle?) {
        val dataMap = mapOf(
            binding.etReps to workoutExerciseSet.reps,
            binding.etWeight to workoutExerciseSet.weightKg,
            binding.etTime to workoutExerciseSet.timeInSeconds,
            binding.etDistance to workoutExerciseSet.distanceInMeters
        )

        dataMap.forEach { (editText, value) ->
            if (editText.text.isBlank()) {
                if (editText == binding.etTime && value != null) {
                    editText.setText(LocalTime.ofSecondOfDay(value.toLong()).format(DateAndTimeStringFormatUtils.fullTimeFormatter))
                } else {
                    editText.setText(value?.toString() ?: "")
                }
            }
        }

        mapOfButtonAndInputs.forEach { (button, editText) ->
            val hasData = dataMap[editText] != null

            button.isSelected = hasData || savedInstanceState?.getBoolean(button.id.toString(), false) ?: false
            binding.etLayout.setEditTextVisibility(editText, button.isSelected)

            button.setOnClickListener {
                it.isSelected = !it.isSelected
                binding.etLayout.setEditTextVisibility(editText, it.isSelected)
            }
        }

        workoutExerciseSet.effort?.let { effort ->
            val buttonToCheck = mapOfButtonsAndEfforts.entries.first { (_, value) -> value == effort }.key
            binding.groupEffortButtons.check(buttonToCheck.id)
        }
    }

    private fun saveWorkoutExerciseSet() {
        val reps = if (binding.etReps.isVisible) binding.etReps.text.toString() else ""
        val weight = if (binding.etWeight.isVisible) binding.etWeight.text.toString() else ""
        val time = if (binding.etTime.isVisible && binding.etTime.text.toString().isNotBlank()) {
            LocalTime.parse(binding.etTime.text.toString(), DateAndTimeStringFormatUtils.fullTimeFormatter).toSecondOfDay().toString()
        } else ""
        val distance = if (binding.etDistance.isVisible) binding.etDistance.text.toString() else ""
        val effort = mapOfButtonsAndEfforts[mapOfButtonsAndEfforts.keys.firstOrNull { it.id == binding.groupEffortButtons.checkedButtonId }]

        viewModel.saveWorkoutExerciseSet(reps, weight, time, distance, effort)
    }


    companion object {
        const val TAG = "WorkoutExerciseSetEditorBottomSheetFragment"
        const val EXERCISE_SET_ID_KEY = "exerciseSetId"

        fun instance(exerciseSetId: UUID): WorkoutExerciseSetEditorFragment {
            val bundle = Bundle().apply {
                putString(EXERCISE_SET_ID_KEY, exerciseSetId.toString())
            }

            return WorkoutExerciseSetEditorFragment().apply { arguments =  bundle }
        }
    }
}