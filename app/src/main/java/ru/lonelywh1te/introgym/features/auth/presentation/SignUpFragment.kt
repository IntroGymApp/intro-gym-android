package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.ErrorSnackbar
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

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
        viewModel.signUpResult.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        navigateToConfirmOtpFragment()
                        showLoadingIndicator(false)
                    }

                    is UIState.isLoading -> {
                        showLoadingIndicator(true)
                    }

                    is UIState.Failure -> {
                        if (state.error == AuthError.SESSION_STILL_EXIST) findNavController().navigate(
                            R.id.confirmOtpFragment
                        )
                        showFailureSnackbar(state.error)
                        showLoadingIndicator(false)
                    }

                    else -> {}
                }
            }
            .launchIn(lifecycleScope)
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
        ErrorSnackbar(binding.root).show(getString(AuthErrorStringResProvider.get(error)))
    }
}