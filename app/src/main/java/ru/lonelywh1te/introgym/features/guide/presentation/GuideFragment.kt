package ru.lonelywh1te.introgym.features.guide.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.databinding.FragmentGuideBinding
import ru.lonelywh1te.introgym.databinding.FragmentProfileBinding

class GuideFragment : Fragment() {
    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardExerciseSection.setOnClickListener {
            navigateToExerciseSection()
        }
    }

    private fun navigateToExerciseSection() {
        val action = GuideFragmentDirections.toExerciseCategoriesFragment()
        findNavController().navigate(action)
    }
}