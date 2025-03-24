package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExercisePlanEditorBinding
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExercisePlanEditorFragmentViewModel


class WorkoutExercisePlanEditorFragment : Fragment() {
    private var _binding: FragmentWorkoutExercisePlanEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutExercisePlanEditorFragmentViewModel>()
    private val args by navArgs<WorkoutExercisePlanEditorFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getExerciseData(args.exerciseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutExercisePlanEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.exerciseName.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.tvExerciseName.text = it
            }
            .launchIn(lifecycleScope)

        viewModel.exerciseAnimFilename.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                ImageLoader(requireContext()).load(
                    uri = AssetPath.get(AssetType.EXERCISE_ANIMATION, it),
                    imageView = binding.ivExerciseAnimation
                )
            }
            .launchIn(lifecycleScope)
    }

    companion object {
        const val REQUEST_KEY = "workout_exercise_plan_editor_request"

        const val COMMENT_BUNDLE_KEY = "workout_exercise_plan_comment"
        const val SETS_BUNDLE_KEY = "workout_exercise_plan_sets"
        const val WEIGHT_BUNDLE_KEY = "workout_exercise_plan_weight"
        const val REPS_BUNDLE_KEY = "workout_exercise_plan_reps"
        const val TIME_BUNDLE_KEY = "workout_exercise_plan_time"
        const val DISTANCE_BUNDLE_KEY = "workout_exercise_plan_distance"
    }
}