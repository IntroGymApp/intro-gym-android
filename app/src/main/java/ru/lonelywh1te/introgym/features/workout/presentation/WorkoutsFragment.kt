package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutsBinding
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutItemAdapter
import ru.lonelywh1te.introgym.features.workout.presentation.viewModel.WorkoutsFragmentViewModel

class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<WorkoutsFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutItemAdapter: WorkoutItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutItemAdapter = WorkoutItemAdapter().apply {
            setOnItemClickListener { workoutItem ->
                navigateToWorkoutFragment(workoutItem.workoutId)
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

                // TODO: заменить на snackbar с отменой

                Toast.makeText(requireContext(), "Тренировка удалена!", Toast.LENGTH_LONG).show()
            }

            attachToRecyclerView(recycler)
        }

        binding.btnCreateWorkout.setOnClickListener {
            navigateToCreateWorkout()
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.workoutList.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workoutList ->
                workoutItemAdapter.update(workoutList)

                Log.d("WorkoutsFragment", "$workoutList")
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToWorkoutFragment(workoutId: Long) {
        val action = WorkoutsFragmentDirections.toWorkoutFragment(workoutId)
        findNavController().safeNavigate(action)
    }

    private fun navigateToCreateWorkout() {
        val action = WorkoutsFragmentDirections.toWorkoutEditorFragment()
        findNavController().safeNavigate(action)
    }
}
