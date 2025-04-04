package ru.lonelywh1te.introgym.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.lonelywh1te.introgym.databinding.FragmentHomeBinding

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
    }
}