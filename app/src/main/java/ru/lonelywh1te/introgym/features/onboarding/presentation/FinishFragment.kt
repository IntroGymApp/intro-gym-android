package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.databinding.FragmentFinishBinding
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.FinishViewModel

class FinishFragment : Fragment() {
    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FinishViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            viewModel.completeOnboarding()
            navigateToSignUpFragment()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun navigateToSignUpFragment() {
        val action = FinishFragmentDirections.toSignUpFragment()
        findNavController().navigate(action)
    }
}