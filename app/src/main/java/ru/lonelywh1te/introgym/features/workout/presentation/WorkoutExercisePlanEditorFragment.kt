package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExercisePlanEditorBinding
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExercisePlanEditorFragmentViewModel

class WorkoutExercisePlanEditorFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutExercisePlanEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutExercisePlanEditorFragmentViewModel>()
    private val args by navArgs<WorkoutExercisePlanEditorFragmentArgs>()

    private val isCreateMode: Boolean get() =  args.isCreateMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getExerciseData(args.exerciseId)

        if (!isCreateMode) {
            viewModel.getWorkoutExercisePlan(args.workoutExerciseId)
        } else {
            viewModel.setWorkoutExerciseId(args.workoutExerciseId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutExercisePlanEditorBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs()
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

    private fun navigateToExerciseInfoFragment() {

    }

    private fun saveWorkoutExercisePlan() {
        viewModel.updateSets(binding.etPlanSets.text.toString())
        viewModel.updateReps(binding.etPlanReps.text.toString())
        viewModel.updateWeight(binding.etPlanWeight.text.toString())
        viewModel.updateTimeInSec(binding.etPlanTime.text.toString())
        viewModel.updateDistanceInMeters(binding.etPlanDistance.text.toString())

        val workoutExercisePlan = viewModel.workoutPlanExercise.value

        val bundle = Bundle().apply {
            putParcelable(WORKOUT_EXERCISE_PLAN_BUNDLE, workoutExercisePlan)
        }

        setFragmentResult(REQUEST_KEY, bundle)
        findNavController().navigateUp()
    }

    // TODO: заменить на custom input layout

    private fun setupInputs() {
        val inputsMap: Map<Button, EditText> = mapOf(
            (binding.btnSelectWeight to binding.etPlanWeight),
            (binding.btnSelectReps to binding.etPlanReps),
            (binding.btnSelectTime to binding.etPlanTime),
            (binding.btnSelectDistance to binding.etPlanDistance),
        )

        inputsMap.forEach { (button, editText) ->
            button.isSelected = false
            editText.visibility = View.GONE

            button.setOnClickListener {
                it.isSelected = !it.isSelected
                editText.visibility = if (button.isSelected) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_exercise_plan_editor_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.exerciseInfo -> navigateToExerciseInfoFragment()
            R.id.saveWorkoutExercisePlan -> saveWorkoutExercisePlan()
        }

        return true
    }

    companion object {
        const val REQUEST_KEY = "workout_exercise_plan_editor_request"
        const val WORKOUT_EXERCISE_PLAN_BUNDLE = "workout_exercise_plan"
    }
}