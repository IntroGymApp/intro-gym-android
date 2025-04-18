package ru.lonelywh1te.introgym.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.databinding.FragmentHomeBinding
import ru.lonelywh1te.introgym.features.workout.presentation.WorkoutsFragment

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weeklyCalendarView.apply {
            setOnChangeWeekListener { position ->
                Log.d("HomeFragment", binding.weeklyCalendarView.getWeek(position).toString())
            }

            setOnDateSelectedListener { date ->
                Log.d("HomeFragment", date.toString())
            }
        }

        setWorkoutIdResultListener()
    }

    private fun navigateToPickWorkout() {
        val action = HomeFragmentDirections.toWorkoutsFragment(isPickMode = true)
        findNavController().safeNavigate(action)
    }

    private fun setWorkoutIdResultListener() {
        setFragmentResultListener(WorkoutsFragment.REQUEST_KEY) { _, bundle ->
            val workoutId = bundle.getLong(WorkoutsFragment.WORKOUT_ID_BUNDLE_KEY)
            Log.d("HomeFragment", "Picked workoutId: $workoutId")

            // TODO: запись тренировки на день (workout log)

        }
    }
}