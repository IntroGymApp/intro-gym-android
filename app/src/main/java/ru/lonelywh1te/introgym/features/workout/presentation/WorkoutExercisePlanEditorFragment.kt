package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutExercisePlanEditorBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutExercisePlanEditorFragmentViewModel

class WorkoutExercisePlanEditorFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutExercisePlanEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutExercisePlanEditorFragmentViewModel>()
    private val workoutEditorViewModel by koinNavGraphViewModel<WorkoutEditorFragmentViewModel>(R.id.workoutEditorGraph)

    private val args by navArgs<WorkoutExercisePlanEditorFragmentArgs>()

    private var exerciseId: Long? = null

    private lateinit var mapOfButtonAndInputs: Map<Button, EditText>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapOfButtonAndInputs.forEach { (button, _) ->
            outState.putBoolean(button.id.toString(), button.isSelected)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutExercisePlanEditorBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapOfButtonAndInputs = mapOf(
            (binding.btnSelectWeight to binding.etPlanWeight),
            (binding.btnSelectReps to binding.etPlanReps),
            (binding.btnSelectTime to binding.etPlanTime),
            (binding.btnSelectDistance to binding.etPlanDistance),
        )

        binding.etExerciseComment.setText(
            workoutEditorViewModel.getWorkoutExerciseComment(args.workoutExerciseId)
        )

        startCollectFlows(savedInstanceState)
    }

    private fun startCollectFlows(savedInstanceState: Bundle?) {
        viewModel.exerciseName.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                if (it.isBlank()) return@onEach

                binding.tvExerciseName.text = it
            }
            .launchIn(lifecycleScope)

        viewModel.exerciseAnimFilename.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                if (it.isBlank()) return@onEach

                ImageLoader(requireContext()).load(
                    uri = AssetPath.get(AssetType.EXERCISE_ANIMATION, it),
                    imageView = binding.ivExerciseAnimation
                )
            }
            .launchIn(lifecycleScope)

        viewModel.workoutPlanExercise.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                setWorkoutExercisePlanData(it, savedInstanceState)
            }
            .launchIn(lifecycleScope)

        workoutEditorViewModel.workoutExercises.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { exercises ->
                val workoutExercise = exercises.find { it.id == args.workoutExerciseId }

                workoutExercise?.let {
                    exerciseId = it.exerciseId
                    viewModel.getExerciseData(it.exerciseId)
                }
            }
            .launchIn(lifecycleScope)

        workoutEditorViewModel.workoutExercisePlans.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { plans ->
                val plan = plans.find { it.workoutExerciseId == args.workoutExerciseId }

                plan?.let { viewModel.setWorkoutExercisePlan(plan) }
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToExerciseInfoFragment() {
        exerciseId?.let { id ->
            val action = WorkoutExercisePlanEditorFragmentDirections.toExerciseInfoFragment(id)
            findNavController().safeNavigate(action)
        }
    }

    private fun saveWorkoutExercisePlan() {
        val sets = if (binding.etPlanSets.isVisible) binding.etPlanSets.text.toString() else ""
        val reps = if (binding.etPlanReps.isVisible) binding.etPlanReps.text.toString() else ""
        val weight = if (binding.etPlanWeight.isVisible) binding.etPlanWeight.text.toString() else ""
        val time = if (binding.etPlanTime.isVisible) binding.etPlanTime.text.toString() else ""
        val distance = if (binding.etPlanDistance.isVisible) binding.etPlanDistance.text.toString() else ""

        viewModel.updateWorkoutPlanExercise(sets, reps, weight, time, distance)

        val workoutExercisePlan = viewModel.workoutPlanExercise.value

        workoutEditorViewModel.updateWorkoutExercisePlan(workoutExercisePlan)
        workoutEditorViewModel.updateWorkoutExerciseComment(workoutExercisePlan.workoutExerciseId, binding.etExerciseComment.text.toString())

        findNavController().navigateUp()
    }

    private fun setWorkoutExercisePlanData(workoutExercisePlan: WorkoutExercisePlan, savedInstanceState: Bundle?) {
        val dataMap = mapOf(
            binding.etPlanSets to workoutExercisePlan.sets,
            binding.etPlanReps to workoutExercisePlan.reps,
            binding.etPlanWeight to workoutExercisePlan.weightKg,
            binding.etPlanTime to workoutExercisePlan.timeInSec,
            binding.etPlanDistance to workoutExercisePlan.distanceInMeters
        )

        dataMap.forEach { (editText, value) ->
            if (editText.text.isBlank()) editText.setText(value?.toString() ?: "")
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
}