package ru.lonelywh1te.introgym.features.home.presentation

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
import ru.lonelywh1te.introgym.app.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.ItemTouchHelperCallback
import ru.lonelywh1te.introgym.databinding.FragmentHomeBinding
import ru.lonelywh1te.introgym.features.home.presentation.adapter.WorkoutLogItemAdapter
import ru.lonelywh1te.introgym.features.home.presentation.viewModel.HomeFragmentViewModel
import ru.lonelywh1te.introgym.features.workout.presentation.WorkoutsFragment

class HomeFragment : Fragment(), MenuProvider {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<HomeFragmentViewModel>()

    private lateinit var recycler: RecyclerView
    private lateinit var workoutLogItemAdapter: WorkoutLogItemAdapter

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

        workoutLogItemAdapter = WorkoutLogItemAdapter().apply {
            setOnItemClickListener { item ->
                navigateToWorkoutFragment(item.workoutId)
            }
        }

        recycler = binding.rvWorkoutLogs.apply {
            adapter = workoutLogItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        ItemTouchHelperCallback(
//            dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//            swipeDirs = ItemTouchHelper.LEFT,
//        ).apply {
//
//            setOnMoveListener { from, to ->
//                // TODO: Not yet implemented
//            }
//
//            setOnMoveFinishedListener { from, to ->
//                // TODO: Not yet implemented
//            }
//
//            setOnLeftSwipeListener { position ->
//                val item = workoutLogItemAdapter.getItem(position)
//                viewModel.deleteWorkoutLog(item.workoutId)
//            }
//
//            attachToRecyclerView(recycler)
//        }

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
                workoutLogItemAdapter.update(list)
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
        val action = HomeFragmentDirections.actionPickWorkout()
        findNavController().safeNavigate(action)
    }

    private fun navigateToWorkoutFragment(workoutId: Long) {
        val action = HomeFragmentDirections.toWorkoutFragment(workoutId)
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
        menu.clear()
        menuInflater.inflate(R.menu.home_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.add_workout_log -> navigateToPickWorkout()
        }

        return true
    }
}