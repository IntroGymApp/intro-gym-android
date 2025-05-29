package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
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
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.utils.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutsBinding
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel

class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutsFragmentViewModel>()
    private val args by navArgs<WorkoutsFragmentArgs>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutItemAdapter: WorkoutItemAdapter

    private val isPickMode by lazy { args.isPickMode }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutItemAdapter = WorkoutItemAdapter().apply {
            setOnItemClickListener { workoutItem ->
                if (isPickMode) {
                    setFragmentResultAndNavigateUp(workoutItem.workoutId)
                } else {
                    navigateToWorkoutFragment(workoutItem.workoutId)
                }
            }
        }

        recycler = binding.rvWorkouts.apply {
            adapter = workoutItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        ItemTouchHelperCallback(
            dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            swipeDirs = ItemTouchHelper.LEFT,
        ).apply {

            setOnMoveListener { from, to ->
                workoutItemAdapter.move(from, to)
            }

            setOnMoveFinishedListener { from, to ->
                viewModel.moveWorkout(from, to)
            }

            setOnLeftSwipeListener { position ->
                val item = workoutItemAdapter.getItem(position)
                viewModel.deleteWorkout(item.workoutId)

                Toast.makeText(requireContext(), "Тренировка удалена!", Toast.LENGTH_LONG).show()
            }

            attachToRecyclerView(recycler)
        }

        binding.rvWorkoutsTitle.text = getString(if (isPickMode) R.string.label_choose_workout else R.string.label_your_workouts)

        binding.btnCreateWorkout.apply {
            setOnClickListener {
                navigateToCreateWorkout()
            }

            visibility = if (isPickMode) View.GONE else View.VISIBLE
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.workoutList.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutList ->
                workoutItemAdapter.update(workoutList)
                binding.groupEmptyList.isVisible = workoutList.isEmpty()
            }
            .launchIn(lifecycleScope)

    }

    private fun setFragmentResultAndNavigateUp(workoutId: Long) {
        val bundle = Bundle().apply {
            putLong(WORKOUT_ID_BUNDLE_KEY, workoutId)
        }

        setFragmentResult(REQUEST_KEY, bundle)
        findNavController().navigateUp()
    }

    private fun navigateToWorkoutFragment(workoutId: Long) {
        val action = WorkoutsFragmentDirections.toWorkoutFragment(workoutId)
        findNavController().safeNavigate(action)
    }

    private fun navigateToCreateWorkout() {
        val action = WorkoutsFragmentDirections.toWorkoutEditorFragment()
        findNavController().safeNavigate(action)
    }

    companion object {
        const val REQUEST_KEY = "workouts_fragment_request_key"
        const val WORKOUT_ID_BUNDLE_KEY = "workout_id"
    }
}
