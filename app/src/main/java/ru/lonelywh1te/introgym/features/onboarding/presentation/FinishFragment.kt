package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.app.activity.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsetsHelper
import ru.lonelywh1te.introgym.databinding.FragmentFinishBinding
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.FinishFragmentViewModel

class FinishFragment : Fragment() {
    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FinishFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        WindowInsetsHelper.setInsets(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            viewModel.completeOnboarding()
            navigateToHomeFragment()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        hideToolbarAndBottomNavigationView()
    }

    private fun hideToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(false)
            setBottomNavigationViewVisibility(false)
        }
    }

    private fun navigateToHomeFragment() {
        val action = FinishFragmentDirections.toHomeFragment()
        findNavController().safeNavigate(action)
    }
}