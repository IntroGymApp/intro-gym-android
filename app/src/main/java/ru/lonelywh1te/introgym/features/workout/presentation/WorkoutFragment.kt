package ru.lonelywh1te.introgym.features.workout.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.databinding.FragmentWorkoutBinding
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
                // TODO: Not yet implemented
            }
        }

        recycler = binding.rvWorkoutExerciseItems.apply {
            adapter = workoutExerciseItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.workout.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { workout ->
                workout?.let {
                    binding.tvWorkoutName.text = workout.name
                    binding.tvWorkoutDescription.text = workout.description
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

    // TODO: добавить диалоговое окно с уточнением
    private fun deleteWorkout() {
        viewModel.deleteWorkout()
    }

    private fun navigateToWorkoutEditorFragment() {
        val action = WorkoutFragmentDirections.toWorkoutEditorGraph(args.workoutId)
        findNavController().safeNavigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.workout_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.editWorkout -> navigateToWorkoutEditorFragment()
            R.id.deleteWorkout -> deleteWorkout()
        }

        return true
    }


}