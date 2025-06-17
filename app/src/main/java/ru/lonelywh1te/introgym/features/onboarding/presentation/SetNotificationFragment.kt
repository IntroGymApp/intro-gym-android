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
import ru.lonelywh1te.introgym.databinding.FragmentSetNotificationBinding
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.SetNotificationViewModel

class SetNotificationFragment : Fragment() {
    private var _binding: FragmentSetNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SetNotificationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetNotificationBinding.inflate(inflater, container, false)

        WindowInsetsHelper.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            viewModel.setNotification(binding.materialSwitch.isEnabled)
            navigateToStartFragment()
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

    private fun navigateToStartFragment() {
        val action = SetNotificationFragmentDirections.toStartFragment()
        findNavController().safeNavigate(action)
    }
}