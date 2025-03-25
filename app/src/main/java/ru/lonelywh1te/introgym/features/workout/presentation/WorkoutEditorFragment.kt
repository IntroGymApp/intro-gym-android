package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutEditorBinding
import ru.lonelywh1te.introgym.features.guide.presentation.GuideFragmentDirections
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel

class WorkoutEditorFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutEditorFragmentViewModel>()
    private val args by navArgs<WorkoutEditorFragmentArgs>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    private val isCreateMode get() = args.workoutId == -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutEditorBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddExercise.setOnClickListener {
            navigateToExercisePickFragment()
        }

        workoutExerciseItemAdapter = WorkoutExerciseItemAdapter().apply {
            setOnItemClickListener { item ->
                val exerciseId = viewModel.workoutExercisesWithPlans.value.keys.find { it.id == item.workoutExerciseId }!!.exerciseId
                navigateToEditWorkoutExercisePlan(item.workoutExerciseId, exerciseId)
            }
        }

        recycler = binding.rvWorkoutExerciseItems.apply {
            adapter = workoutExerciseItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.etWorkoutName.addTextChangedListener {
            viewModel.updateWorkoutName(it.toString())
        }

        binding.etWorkoutDescription.addTextChangedListener {
            viewModel.updateWorkoutDescription(it.toString())
        }

        setFragmentResultListeners()
        startCollectFlows()
    }

    private fun navigateToEditWorkoutExercisePlan(workoutExerciseId: Long, exerciseId: Long) {
        val action = WorkoutEditorFragmentDirections.toWorkoutExercisePlanEditorFragment(
            exerciseId = exerciseId,
            workoutExerciseId = workoutExerciseId,
            isCreateMode = isCreateMode,
        )
        findNavController().navigate(action)
    }

    private fun navigateToExercisePickFragment() {
        val action = WorkoutEditorFragmentDirections.toGuideExersices(true, R.id.workoutEditorFragment)
        findNavController().safeNavigate(action)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addWorkoutExercise(pickedExerciseId)
        }

        setFragmentResultListener(WorkoutExercisePlanEditorFragment.REQUEST_KEY) { _, bundle ->
            val workoutExercisePlan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE, WorkoutExercisePlan::class.java)
            } else {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE)
            }

            workoutExercisePlan?.let {
                viewModel.updateWorkoutExercisePlan(it)
            }
        }
    }

    private fun startCollectFlows() {
        viewModel.workoutExerciseItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                workoutExerciseItemAdapter.update(it)
            }
            .launchIn(lifecycleScope)

        viewModel.workoutSaved.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutSaved ->
                if (workoutSaved) findNavController().navigateUp()
            }
            .launchIn(lifecycleScope)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_editor_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.saveWorkout -> viewModel.saveWorkout()
        }

        return true
    }

}