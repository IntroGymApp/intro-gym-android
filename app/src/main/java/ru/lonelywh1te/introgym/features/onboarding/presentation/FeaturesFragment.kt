package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.lonelywh1te.introgym.app.activity.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsets
import ru.lonelywh1te.introgym.databinding.FragmentFeaturesBinding

class FeaturesFragment: Fragment() {
    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnNext.setOnClickListener {
            navigateToSetNotificationsFragment()
        }

        hideToolbarAndBottomNavigationView()
    }

    private fun hideToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(false)
            setBottomNavigationViewVisibility(false)
        }
    }

    private fun navigateToSetNotificationsFragment() {
        val action = FeaturesFragmentDirections.toSetNotificationFragment()
        findNavController().safeNavigate(action)
    }
}