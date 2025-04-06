package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
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
import org.koin.androidx.navigation.koinNavGraphViewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutEditorBinding
import ru.lonelywh1te.introgym.features.guide.presentation.exercises.ExerciseListFragment
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutExerciseItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.error.WorkoutErrorStringMessageProvider
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutEditorFragmentViewModel

class WorkoutEditorFragment : Fragment(), MenuProvider {
    private var _binding: FragmentWorkoutEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel by koinNavGraphViewModel<WorkoutEditorFragmentViewModel>(R.id.workoutEditorGraph)
    private val args by navArgs<WorkoutEditorFragmentArgs>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutExerciseItemAdapter: WorkoutExerciseItemAdapter

    private val isCreateMode get() = args.workoutId == -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.setWorkout(args.workoutId)
        }
    }

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
        val action = WorkoutEditorFragmentDirections.toWorkoutExercisePlanEditorFragment(
            workoutExerciseId = workoutExerciseId,
        )
        findNavController().navigate(action)
    }

    private fun navigateToExercisePickFragment() {
        val action = WorkoutEditorFragmentDirections.toExerciseCategoriesFragment(true, R.id.workoutEditorFragment)
        findNavController().safeNavigate(action)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(ExerciseListFragment.PICK_REQUEST_KEY) { _, bundle ->
            val pickedExerciseId = bundle.getLong(ExerciseListFragment.PICK_RESULT_BUNDLE_KEY)
            viewModel.addPickedWorkoutExercise(pickedExerciseId)
        }
    }

    private fun startCollectFlows() {
        viewModel.workout.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workout ->

                workout?.let {
                    binding.etWorkoutName.setText(it.name)
                    binding.etWorkoutDescription.setText(it.description)
                }
            }
            .launchIn(lifecycleScope)
        viewModel.workoutExerciseItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                workoutExerciseItemAdapter.update(it)
            }
            .launchIn(lifecycleScope)

        viewModel.workoutSaveState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { saveResult ->
                when (saveResult) {
                    is Result.Success -> findNavController().navigateUp()
                    is Result.Failure -> {
                        binding.llTextInputContainer.setErrorMessage(
                            getString(WorkoutErrorStringMessageProvider.get(saveResult.error))
                        )
                    }
                    else -> { }
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_editor_menu, menu)
    }

    override fun onStop() {
        super.onStop()

        viewModel.updateWorkoutName(binding.etWorkoutName.text.toString())
        viewModel.updateWorkoutDescription(binding.etWorkoutDescription.text.toString())
    }

    private fun saveWorkout() {
        viewModel.updateWorkoutName(binding.etWorkoutName.text.toString())
        viewModel.updateWorkoutDescription(binding.etWorkoutDescription.text.toString())

        viewModel.saveWorkout()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.saveWorkout -> saveWorkout()
        }

        return true
    }

}