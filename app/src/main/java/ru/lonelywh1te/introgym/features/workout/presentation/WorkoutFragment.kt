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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutFragmentViewModel

class WorkoutFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<WorkoutFragmentArgs>()
    private val viewModel by viewModel<WorkoutFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadWorkout(args.workoutId)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                val item = workoutExerciseItemAdapter.getItem(position)
                viewModel.deleteWorkoutExercise(item.workoutExerciseId)
            }

            attachToRecyclerView(recycler)
        }

        binding.btnAddExercise.setOnClickListener {
            navigateToPickExerciseFragment()
        }

        startCollectFlows()
        setFragmentResultListeners()
    }

    private fun startCollectFlows() {
        viewModel.workout.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workout ->
                workout?.let {
                    binding.etWorkoutName.setText(workout.name)
                    binding.etWorkoutDescription.setText(workout.description)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.workoutExerciseItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { items ->
                workoutExerciseItemAdapter.update(items)
            }
            .launchIn(lifecycleScope)

        viewModel.workoutDeleted.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { deleted ->
                if (deleted) findNavController().navigateUp()
            }
            .launchIn(lifecycleScope)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addWorkoutExercise(pickedExerciseId)
        }

        setFragmentResultListener(WorkoutExercisePlanEditorFragment.FRAGMENT_REQUEST_KEY) { _, bundle ->
            val workoutExercisePlan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY, WorkoutExercisePlan::class.java)
            } else {
                bundle.getParcelable(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_PLAN_BUNDLE_KEY)
            }

            val workoutExerciseComment = bundle.getString(WorkoutExercisePlanEditorFragment.WORKOUT_EXERCISE_COMMENT_BUNDLE_KEY, "")

            workoutExercisePlan?.let { plan ->
                viewModel.updateWorkoutExercisePlan(plan)
                viewModel.updateWorkoutExerciseComment(plan.workoutExerciseId, workoutExerciseComment)
            }
        }
    }

    private fun navigateToPickExerciseFragment() {
        val action = WorkoutFragmentDirections.toExerciseCategoriesFragment(true, R.id.workoutFragment)
        findNavController().navigate(action)
    }

    private fun navigateToEditWorkoutExercisePlan(workoutExerciseId: Long) {
        val workoutExercise = viewModel.getWorkoutExerciseById(workoutExerciseId)

        val action = WorkoutFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExercise = workoutExercise
        )

        findNavController().navigate(action)
    }

    override fun onStop() {
        super.onStop()
        updateWorkout()
    }

    private fun updateWorkout() {
        val name = binding.etWorkoutName.text.toString()
        val description = binding.etWorkoutDescription.text.toString()

        viewModel.updateWorkout(name, description)
    }

    // TODO: добавить диалоговое окно с уточнением
    private fun deleteWorkout() {
        viewModel.deleteWorkout()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.deleteWorkout -> deleteWorkout()
        }

        return true
    }


}