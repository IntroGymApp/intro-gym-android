package ru.lonelywh1te.introgym.features.settings.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModel<SettingsFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.switchNotification.isChecked = viewModel.isNotificationsEnabled

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNotificationsEnabled(isChecked)
        }

        return binding.root
    }
}