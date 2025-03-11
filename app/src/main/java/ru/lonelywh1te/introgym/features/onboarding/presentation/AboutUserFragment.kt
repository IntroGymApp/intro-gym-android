package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
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
            val name = binding.etUsername.text.toString()
            val gender = getSelectedGender()
            val birthday = getSelectedBirthday()

            viewModel.saveUserPreferences(name, gender, birthday)

            findNavController().navigate(R.id.setNotificationFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.materialButtonToggleGroup.isSingleSelection = true
        binding.dpBirthday.apply {
            maxDate = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        }
    }

    private fun getSelectedGender(): Gender? {
        return when (binding.materialButtonToggleGroup.checkedButtonId) {
            binding.btnMale.id -> Gender.MALE
            binding.btnFemale.id -> Gender.FEMALE
            else -> null
        }
    }

    private fun getSelectedBirthday(): LocalDate {
        return LocalDate.of(binding.dpBirthday.year, binding.dpBirthday.month, binding.dpBirthday.dayOfMonth)
    }
}