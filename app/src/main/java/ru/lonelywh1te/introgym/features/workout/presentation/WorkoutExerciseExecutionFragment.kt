package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExerciseExecutionBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExerciseExecutionViewModel
import java.time.LocalTime

class WorkoutExerciseExecutionFragment : Fragment() {
    private var _binding: FragmentWorkoutExerciseExecutionBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutExerciseExecutionViewModel>()
    private val args by navArgs<WorkoutExerciseExecutionFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setWorkoutExerciseId(args.workoutExerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutExerciseExecutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupEffortButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    binding.btnWarmUpEffort.id -> viewModel.setEffort(Effort.WARMUP)
                    binding.btnLowEffort.id -> viewModel.setEffort(Effort.LOW)
                    binding.btnMidEffort.id -> viewModel.setEffort(Effort.MEDIUM)
                    binding.btnHardEffort.id -> viewModel.setEffort(Effort.HARD)
                    binding.btnMaxEffort.id -> viewModel.setEffort(Effort.MAX)
                }
            }
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.workoutExercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutExercise ->
                workoutExercise?.let {
                    setWorkoutExerciseData(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExercisePlan.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutExercisePlan ->
                workoutExercisePlan?.let {
                    setWorkoutExercisePlanData(workoutExercisePlan)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.exercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { exercise ->
                exercise?.let {
                    setExerciseData(it)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setWorkoutExerciseData(workoutExercise: WorkoutExercise) {
        binding.tvExerciseComment.apply {
            text = workoutExercise.comment
            isVisible = workoutExercise.comment.isNotBlank()
        }
    }

    private fun setWorkoutExercisePlanData(workoutExercisePlan: WorkoutExercisePlan) {
        binding.llWeightInput.isVisible = workoutExercisePlan.weightKg != null
        if (workoutExercisePlan.weightKg != null) {
            binding.etWeight.setHint(workoutExercisePlan.weightKg.toString())
        }

        binding.llRepsInput.isVisible = workoutExercisePlan.reps != null
        if (workoutExercisePlan.reps != null) {
            binding.etReps.setHint(workoutExercisePlan.reps.toString())
        }

        binding.llTimeInput.isVisible = workoutExercisePlan.timeInSec != null
        if (workoutExercisePlan.timeInSec != null) {
            binding.etTime.setHint(
                LocalTime.ofSecondOfDay(workoutExercisePlan.timeInSec.toLong())
                    .format(DateAndTimeStringFormatUtils.fullTimeFormatter)
            )
        }

        binding.llDistanceInput.isVisible = workoutExercisePlan.distanceInMeters != null
        if (workoutExercisePlan.distanceInMeters != null) {
            binding.etDistance.setHint(workoutExercisePlan.distanceInMeters.toString())
        }
    }

    private fun setExerciseData(exercise: Exercise) {
        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_ANIMATION, exercise.animFilename), binding.ivExerciseAnimation)

        binding.tvExerciseName.text = exercise.name
    }
}