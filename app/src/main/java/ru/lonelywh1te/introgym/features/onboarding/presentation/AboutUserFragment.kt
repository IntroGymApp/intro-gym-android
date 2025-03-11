package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.core.ui.extensions.setColorSpan
import ru.lonelywh1te.introgym.databinding.FragmentAboutUserBinding


class AboutUserFragment : Fragment() {
    private var _binding: FragmentAboutUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutUserBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.setColorSpan(getString(R.string.app_name), R.attr.igPrimaryColor)
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.setNotificationFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}