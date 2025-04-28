package ru.lonelywh1te.introgym.features.home.presentation

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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.app.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.databinding.FragmentHomeBinding
import ru.lonelywh1te.introgym.features.home.presentation.adapter.WorkoutLogItemAdapter
import ru.lonelywh1te.introgym.features.home.presentation.viewModel.HomeFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.WorkoutsFragment
import ru.lonelywh1te.introgym.features.workout.presentation.adapter.WorkoutItemAdapter

class HomeFragment : Fragment(), MenuProvider {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<HomeFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutItemAdapter: WorkoutLogItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        binding.weeklyCalendarView.apply {
            setOnDateSelectedListener { date ->
                viewModel.setSelectedDate(date)
            }

            setOnChangeWeekListener { position ->
                val week = getWeek(position)
                viewModel.updateMarkedDays(week)
            }
        }

        workoutItemAdapter = WorkoutLogItemAdapter()

        recycler = binding.rvWorkoutLogs.apply {
            adapter = workoutItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        showToolbarAndBottomNavigationView()
        setWorkoutIdResultListener()
        startCollectFlows()
    }

    private fun showToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(true)
            setBottomNavigationViewVisibility(true)
        }
    }

    private fun startCollectFlows() {
        viewModel.workoutLogItems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { list ->
                workoutItemAdapter.update(list)
            }
            .launchIn(lifecycleScope)

        viewModel.markedDays.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { markedDaysOnWeek ->
                binding.weeklyCalendarView.setMarkedDays(markedDaysOnWeek)
            }
            .launchIn(lifecycleScope)

        viewModel.errors.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { error ->
                // TODO: Not yet implemented
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToPickWorkout() {
        val action = HomeFragmentDirections.toWorkoutsFragment(isPickMode = true)
        findNavController().safeNavigate(action)
    }

    private fun setWorkoutIdResultListener() {
        setFragmentResultListener(WorkoutsFragment.REQUEST_KEY) { _, bundle ->
            val workoutId = bundle.getLong(WorkoutsFragment.WORKOUT_ID_BUNDLE_KEY)
            val selectedDate = binding.weeklyCalendarView.selectedDate

            viewModel.addWorkoutLog(selectedDate, workoutId)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.add_workout_log -> navigateToPickWorkout()
        }

        return true
    }
}