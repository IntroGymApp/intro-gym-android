package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.app.activity.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.extensions.setColorSpan
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsets
import ru.lonelywh1te.introgym.databinding.FragmentHelloBinding
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.HelloFragmentViewModel

class HelloFragment : Fragment() {
    private var _binding: FragmentHelloBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<HelloFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHelloBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHelloIntrogym.setColorSpan(getString(R.string.app_name), R.attr.igPrimaryColor)

        binding.btnSkip.setOnClickListener {
            viewModel.completeOnboarding()
            navigateToHomeFragment()
        }

        binding.btnNext.setOnClickListener {
            navigateToFeaturesFragment()
        }

        hideToolbarAndBottomNavigationView()
    }

    private fun navigateToHomeFragment() {
        val action = HelloFragmentDirections.toHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToFeaturesFragment() {
        val action = HelloFragmentDirections.toFeaturesFragment()
        findNavController().safeNavigate(action)
    }

    private fun hideToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(false)
            setBottomNavigationViewVisibility(false)
        }
    }
}