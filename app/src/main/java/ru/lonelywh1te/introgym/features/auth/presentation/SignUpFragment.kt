package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.ErrorSnackbar
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

class SignUpFragment : Fragment() {
    private val confirmOtpRequestKey = "CONFIRM_OTP_REQUEST"
    private val confirmOtpResultBundleKey = "CONFIRM_OTP_RESULT"

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModel<SignUpViewModel>()

    private val navController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.sendOtp(email, password, confirmPassword)
        }

        binding.passwordValidationView.apply {
            errorRequirements = mapOf(
                ValidationError.PASSWORD_TOO_SHORT to "Минимум 8 символов",
                ValidationError.PASSWORD_MISSING_UPPERCASE to "Есть заглавная буква",
                ValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL to "Есть специальный символ"
            )
        }

        startCollectFlows()
        setOnChangePasswordListener()
        setConfirmOtpFragmentResultListener()
    }

    private fun startCollectFlows() {
        viewModel.sendOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
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
                        if (state.error == AuthError.SESSION_STILL_EXIST) {
                            navigateToConfirmOtpFragment()
                        }
                        showFailureSnackbar(state.error)
                        showLoadingIndicator(false)
                    }

                    else -> {}
                }
            }
            .launchIn(lifecycleScope)


        viewModel.signUpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        navigateToHomeFragment()
                        showLoadingIndicator(false)
                    }

                    is UIState.isLoading -> {
                        showLoadingIndicator(true)
                    }

                    is UIState.Failure -> {
                        if (state.error == AuthError.EMAIL_ALREADY_REGISTERED) {
                            navigateToSignInFragment()
                        }
                        showFailureSnackbar(state.error)
                        showLoadingIndicator(false)
                    }

                    else -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setOnChangePasswordListener() {
        binding.etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(password: Editable?) {
                val validationResult = viewModel.validatePassword(password.toString())
                binding.passwordValidationView.setCurrentErrors(validationResult)
            }
        })
    }

    private fun setConfirmOtpFragmentResultListener() {
        setFragmentResultListener(confirmOtpRequestKey) { _, bundle ->
            val isConfirmed = bundle.getBoolean(confirmOtpResultBundleKey)

            if (isConfirmed) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val confirmPassword = binding.etConfirmPassword.text.toString()

                viewModel.signUp(email, password, confirmPassword)
            } else {
                showFailureSnackbar(NetworkError.UNKNOWN)
            }
        }
    }

    private fun navigateToHomeFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToSignInFragment() {
        val action = SignUpFragmentDirections.toSignInFragment()
        navController.navigate(action)
    }

    private fun navigateToConfirmOtpFragment() {
        val action = SignUpFragmentDirections.toConfirmOtpFragment(confirmOtpRequestKey, confirmOtpResultBundleKey)
        navController.navigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.etUserName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.llTextInputContainer.alpha = if (isLoading) 0.5f else 1f

        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignUp.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureSnackbar(error: Error) {
        ErrorSnackbar(binding.root).show(getString(AuthErrorStringResProvider.get(error)))
    }

    companion object {
        private const val LOG_TAG = "SignUpFragment"
    }
}