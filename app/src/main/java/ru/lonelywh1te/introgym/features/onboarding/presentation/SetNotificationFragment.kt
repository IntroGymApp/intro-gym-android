package ru.lonelywh1te.introgym.features.onboarding.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.databinding.FragmentSetNotificationBinding

class SetNotificationFragment : Fragment() {
    private var _binding: FragmentSetNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetNotificationBinding.inflate(inflater, container, false)

        hideToolbar()
        setWindowInsets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.startFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                binding.root.paddingStart,
                systemBars.top,
                binding.root.paddingEnd,
                systemBars.bottom
            )
            insets
        }
    }

    private fun hideToolbar() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
}