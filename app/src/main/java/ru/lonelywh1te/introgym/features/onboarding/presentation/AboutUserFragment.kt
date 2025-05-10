package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.app.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.core.ui.extensions.setColorSpan
import ru.lonelywh1te.introgym.data.prefs.user.Gender
import ru.lonelywh1te.introgym.databinding.FragmentAboutUserBinding
import ru.lonelywh1te.introgym.features.onboarding.presentation.viewModel.AboutUserViewModel
import java.time.LocalDate
import java.time.ZoneOffset

class AboutUserFragment : Fragment() {
    private var _binding: FragmentAboutUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AboutUserViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutUserBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.setColorSpan(getString(R.string.app_name), R.attr.igPrimaryColor)

        binding.btnNext.setOnClickListener {
            saveUserPreferences()
            navigateToSetNotificationFragment()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.dpBirthday.apply {
            maxDate = LocalDate.now().atStartOfDay().minusYears(14).toInstant(ZoneOffset.UTC).toEpochMilli()
        }

        binding.btnMale.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) binding.btnFemale.isSelected = false
        }

        binding.btnFemale.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) binding.btnMale.isSelected = false
        }

        hideToolbarAndBottomNavigationView()
    }

    private fun saveUserPreferences() {
        val name = binding.etUsername.text.toString()
        val gender = getSelectedGender()
        val birthday = getSelectedBirthday()

        viewModel.saveUserPreferences(name, gender, birthday)
    }

    private fun navigateToSetNotificationFragment() {
        val action = AboutUserFragmentDirections.toSetNotificationFragment()
        findNavController().safeNavigate(action)
    }

    private fun getSelectedGender(): Gender? {
        return when {
            binding.btnMale.isSelected -> Gender.MALE
            binding.btnFemale.isSelected -> Gender.FEMALE
            else -> null
        }
    }

    private fun hideToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(false)
            setBottomNavigationViewVisibility(false)
        }
    }

    private fun getSelectedBirthday(): LocalDate {
        return LocalDate.of(binding.dpBirthday.year, binding.dpBirthday.month, binding.dpBirthday.dayOfMonth)
    }
}