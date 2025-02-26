package ru.lonelywh1te.introgym.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.auth.presentation.error.asStringRes
import ru.lonelywh1te.introgym.auth.presentation.viewmodel.SignUpViewModel
import ru.lonelywh1te.introgym.core.network.NetworkError
import ru.lonelywh1te.introgym.core.network.asStringRes
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModel<SignUpViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendOtp.setOnClickListener {
            val toEmail = binding.etEmail.text.toString()
            viewModel.sendOtp(toEmail)
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpResult.collect { state ->
                    when (state) {
                        is UIState.Success -> {
                            navigateToConfirmOtpFragment()
                            showLoadingIndicator(false)
                        }
                        is UIState.isLoading -> {
                            showLoadingIndicator(true)
                        }
                        is UIState.Failure -> {
                            if (state.error == AuthError.SESSION_STILL_EXIST) findNavController().navigate(R.id.confirmOtpFragment)
                            showFailureSnackbar(state.error)
                            showLoadingIndicator(false)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun navigateToConfirmOtpFragment() {
        val email = binding.etEmail.text.toString()
        val action = SignUpFragmentDirections.toConfirmOtpFragment(email)
        findNavController().navigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSendOtp.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureSnackbar(error: Error) {
        val message = when (error) {
            is AuthError -> getString(error.asStringRes())
            is ValidationError -> getString(error.asStringRes())
            is NetworkError -> getString(error.asStringRes())
            else -> throw IllegalArgumentException("Unknown Error type: $this")
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .show()
    }
}