package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsetsHelper
import ru.lonelywh1te.introgym.databinding.FragmentForgotPasswordBinding
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.RestorePasswordViewModel

class RestorePasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<RestorePasswordViewModel>()

    private val otpType = OtpType.CONFIRM_CHANGE_PASSWORD
    private var otpConfirmed: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("otpConfirmed", otpConfirmed)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        savedInstanceState?.let { bundle ->
            otpConfirmed = bundle.getBoolean("otpConfirmed")
        }

        setChangePasswordFormState(otpConfirmed)

        WindowInsetsHelper.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            hideErrorMessage()

            if (otpConfirmed) {
                changePassword()
            } else {
                navigateToConfirmOtpFragment()
            }
        }

        binding.llTextInputContainer.apply {
            setEditTextVisibility(binding.etConfirmPassword, otpConfirmed)
            setEditTextVisibility(binding.etPassword, otpConfirmed)
        }

        binding.etPassword.addTextChangedListener { password ->
            val validationResult = viewModel.validatePassword(password.toString())
            binding.passwordValidationView.setCurrentErrors(validationResult)
        }

        setConfirmOtpFragmentResultListener()
        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.changePasswordResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        findNavController().navigateUp()
                        showLoadingIndicator(false)
                    }
                    is UIState.Loading -> {
                        showLoadingIndicator(true)
                    }
                    is UIState.Failure -> {
                        showErrorMessage(state.error)
                        showLoadingIndicator(false)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToConfirmOtpFragment() {
        val action = RestorePasswordFragmentDirections.toConfirmOtpFragment(binding.etEmail.text.toString(), otpType)
        findNavController().safeNavigate(action)
    }

    private fun changePassword() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        viewModel.changePassword(email, password, confirmPassword)
    }

    private fun setConfirmOtpFragmentResultListener() {
        setFragmentResultListener(ConfirmOtpFragment.REQUEST_KEY) { _, bundle ->
            val isConfirmed = bundle.getBoolean(ConfirmOtpFragment.RESULT_BUNDLE_KEY)

            if (isConfirmed) {
                otpConfirmed = true
                setChangePasswordFormState(otpConfirmed)
            }
        }
    }

    private fun setChangePasswordFormState(otpConfirmed: Boolean) {
        showPasswordAndConfirmPasswordInputs(otpConfirmed)
        setSubmitButtonText(otpConfirmed)
        setRestorePasswordDescriptionText(otpConfirmed)
        enableEmailInput(!otpConfirmed)
    }

    private fun enableEmailInput(isEnabled: Boolean) {
        binding.etEmail.apply {
            this.isEnabled = isEnabled
            alpha = if (isEnabled) 1f else 0.5f
        }
    }

    private fun setSubmitButtonText(otpConfirmed: Boolean) {
        binding.btnSubmit.text = getString(
            if (!otpConfirmed) R.string.label_send_otp else R.string.label_sign_in
        )
    }

    private fun setRestorePasswordDescriptionText(otpConfirmed: Boolean) {
        binding.tvRestorePasswordDescription.text = getString(
            if (!otpConfirmed) R.string.label_restore_password_description else R.string.label_create_new_password_description
        )
    }

    private fun showPasswordAndConfirmPasswordInputs(show: Boolean) {
        binding.etPassword.isEnabled = show
        binding.etConfirmPassword.isEnabled = show

        binding.llTextInputContainer.apply {
            setEditTextVisibility(binding.etConfirmPassword, show)
            setEditTextVisibility(binding.etPassword, show)
        }

        binding.passwordValidationView.isVisible = show
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.llTextInputContainer.alpha = if (isLoading) 0.5f else 1f

        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showErrorMessage(error: BaseError) {
        binding.llTextInputContainer.setErrorMessage(getString(AuthErrorStringResProvider.get(error)))
    }

    private fun hideErrorMessage() {
        binding.llTextInputContainer.setErrorMessage(null)
    }
}