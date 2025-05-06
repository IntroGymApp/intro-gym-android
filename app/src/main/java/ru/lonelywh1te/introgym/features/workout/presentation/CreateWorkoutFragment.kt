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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentCreateWorkoutBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.error.WorkoutErrorStringMessageProvider
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.CreateWorkoutFragmentViewModel

class CreateWorkoutFragment : Fragment(), MenuProvider {
    private var _binding: FragmentCreateWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreateWorkoutFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false)
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
                navigateToEditWorkoutExercisePlan(item.workoutExerciseId)
            }
        }

        recycler = binding.rvWorkoutExerciseItems.apply {
            adapter = workoutExerciseItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        ItemTouchHelperCallback(
            dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            swipeDirs = ItemTouchHelper.LEFT,
        ).apply {
            setOnMoveListener { from, to ->
                viewModel.moveWorkoutExerciseItem(from, to)
            }

            setOnMoveFinishedListener { from, to ->
                viewModel.moveWorkoutExercise(from, to)
            }

            setOnLeftSwipeListener { position ->
                viewModel.deleteWorkoutExercise(position)
            }

            attachToRecyclerView(recycler)
        }

        setFragmentResultListeners()
        startCollectFlows()
    }

    private fun navigateToEditWorkoutExercisePlan(workoutExerciseId: Long) {
        val workoutExercise = viewModel.getWorkoutExerciseById(workoutExerciseId)
        val workoutExercisePlan = viewModel.getWorkoutExercisePlanById(workoutExerciseId)

        val action = CreateWorkoutFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExercise = workoutExercise,
            workoutExercisePlan = workoutExercisePlan,
        )

        findNavController().navigate(action)
    }

    private fun navigateToExercisePickFragment() {
        val action = CreateWorkoutFragmentDirections.actionPickExercise(R.id.createWorkoutFragment)
        findNavController().safeNavigate(action)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addPickedExercise(pickedExerciseId)
        }

        setFragmentResultListener(WorkoutExercisePlanEditorFragment.FRAGMENT_REQUEST_KEY) { _, bundle ->
            val workoutExercisePlan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY, WorkoutExercisePlan::class.java)
            } else {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY)
            }

            val workoutExerciseComment = bundle.getString(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_COMMENT_BUNDLE_KEY)

            workoutExercisePlan?.let { plan ->
                viewModel.updateWorkoutExercisePlan(plan)
                viewModel.updateWorkoutExerciseComment(plan.workoutExerciseId, workoutExerciseComment ?: "")
            }
        }
    }

    private fun startCollectFlows() {
        viewModel.workoutExerciseItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                workoutExerciseItemAdapter.update(it)
                binding.tvEmptyListDescription.isVisible = it.isEmpty()
            }
            .launchIn(lifecycleScope)

        viewModel.createWorkoutResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { result ->
                result
                    .onSuccess { findNavController().navigateUp() }
                    .onFailure { showErrorMessage(it) }
            }
            .launchIn(lifecycleScope)
    }

    private fun showErrorMessage(error: Error) {
        binding.llTextInputContainer.setErrorMessage(getString(WorkoutErrorStringMessageProvider.get(error)))
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_editor_menu, menu)
    }

    private fun createWorkout() {
        viewModel.updateWorkoutName(binding.etWorkoutName.text.toString())
        viewModel.updateWorkoutDescription(binding.etWorkoutDescription.text.toString())

        viewModel.createWorkout()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.createWorkout -> createWorkout()
        }

        return true
    }

}