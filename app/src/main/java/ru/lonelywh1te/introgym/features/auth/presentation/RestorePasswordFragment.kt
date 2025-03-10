package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
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
import ru.lonelywh1te.introgym.data.network.NetworkError
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { bundle -> setChangePasswordForm(bundle.getBoolean("otpConfirmed")) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("otpConfirmed", otpConfirmed)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            if (otpConfirmed) {

                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val confirmPassword = binding.etConfirmPassword.text.toString()

                viewModel.changePassword(email, password, confirmPassword)

            } else {
                navigateToConfirmOtpFragment()
            }
        }

        setConfirmOtpFragmentResultListener()
        setOnChangePasswordListener()
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
                    is UIState.isLoading -> {
                        showLoadingIndicator(true)
                    }
                    is UIState.Failure -> {
                        showFailureMessage(state.error)
                        showLoadingIndicator(false)
                    }
                    else -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToConfirmOtpFragment() {
        val action = RestorePasswordFragmentDirections.toConfirmOtpFragment(binding.etEmail.text.toString(), otpType)
        findNavController().navigate(action)
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
        setFragmentResultListener(ConfirmOtpFragment.REQUEST_KEY) { _, bundle ->
            val isConfirmed = bundle.getBoolean(ConfirmOtpFragment.RESULT_BUNDLE_KEY)

            if (isConfirmed) {
                otpConfirmed = true
                setChangePasswordForm(otpConfirmed = true)
            } else {
                val error = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(ConfirmOtpFragment.ERROR_BUNDLE_KEY, Error::class.java)
                } else {
                    bundle.getSerializable(ConfirmOtpFragment.ERROR_BUNDLE_KEY) as Error
                }

                error?.let { showFailureMessage(it) }
            }
        }
    }

    private fun setChangePasswordForm(otpConfirmed: Boolean) {
        binding.etEmail.apply {
            isEnabled = !otpConfirmed
            alpha = if (otpConfirmed) 0.5f else 1f
        }

        binding.etPassword.apply {
            isEnabled = otpConfirmed
            visibility = View.VISIBLE
        }
        binding.etConfirmPassword.apply {
            isEnabled = otpConfirmed
            visibility = View.VISIBLE
        }

        binding.btnSubmit.text = getString(
            if (!otpConfirmed) R.string.label_send_otp else R.string.label_sign_in
        )

        binding.tvRestorePasswordDescription.text = getString(
            if (!otpConfirmed) R.string.label_restore_password_description else R.string.label_create_new_password_description
        )

    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.llTextInputContainer.alpha = if (isLoading) 0.5f else 1f

        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureMessage(error: Error) {
        binding.tvErrorMessage.apply {
            text = getString(AuthErrorStringResProvider.get(error))
            visibility = View.VISIBLE
        }
    }

}