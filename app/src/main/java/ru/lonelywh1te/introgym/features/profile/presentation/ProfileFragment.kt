package ru.lonelywh1te.introgym.features.profile.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.NavGraphDirections
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ProfileFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.groupSignedIn.isVisible = viewModel.getIsSignedIn()
        binding.groupSignedOut.isVisible = !viewModel.getIsSignedIn()

        binding.btnSignUp.setOnClickListener {
            navigateToSignUp()
        }

        binding.tvUserName.text = viewModel.getUserName()
        binding.tvUserRegisterDate.text = "Присоединился: ${viewModel.getRegisterDate()}"
        binding.tvCountOfWorkouts.text = viewModel.getCountOfWorkouts().toString()
    }

    private fun navigateToSignUp() {
        val action = ProfileFragmentDirections.actionAuth()
        findNavController().safeNavigate(action)
    }
}