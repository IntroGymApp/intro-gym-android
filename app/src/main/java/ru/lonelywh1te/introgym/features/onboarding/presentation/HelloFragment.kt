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
import ru.lonelywh1te.introgym.databinding.FragmentHelloBinding

class HelloFragment : Fragment() {
    private var _binding: FragmentHelloBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHelloBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvHelloIntrogym.setColorSpan(getString(R.string.app_name), R.attr.igPrimaryColor)

        binding.btnSkip.setOnClickListener {
            navigateToSignInFragment()
        }

        binding.btnNext.setOnClickListener {
            navigateToFeaturesFragment()
        }
    }

    private fun navigateToSignInFragment() {
        val action = HelloFragmentDirections.toSignInFragment()
        findNavController().navigate(action)
    }

    private fun navigateToFeaturesFragment() {
        val action = HelloFragmentDirections.toFeaturesFragment()
        findNavController().navigate(action)
    }
}