package ru.lonelywh1te.introgym.features.auth.presentation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
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
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.ErrorSnackbar
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModel<SignUpViewModel>()
    private val navController by lazy { findNavController() }

    private val otpType = OtpType.CONFIRM_SIGNUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()

            navigateToConfirmOtpFragment(email)
        }

        binding.passwordValidationView.apply {
            errorRequirements = mapOf(
                ValidationError.PASSWORD_TOO_SHORT to getString(R.string.label_min_8_symbols),
                ValidationError.PASSWORD_MISSING_UPPERCASE to getString(R.string.label_has_upper_letter),
                ValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL to getString(R.string.label_has_special_symbol),
            )
        }


        startCollectFlows()
        setOnChangePasswordListener()
        setConfirmOtpFragmentResultListener()
        setSpannableStrings()
    }

    private fun startCollectFlows() {
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

                        showFailureMessage(state.error)
                        showLoadingIndicator(false)
                    }

                    else -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setSpannableStrings() {
        val signInSpannable = SpannableString(binding.tvUserHasAccount.text)
        val spanStartIndex = binding.tvUserHasAccount.text.indexOf("Войти")

        signInSpannable.setSpan(object: ClickableSpan(){
            override fun onClick(view: View) {
                navigateToSignInFragment()
            }
        }, spanStartIndex, spanStartIndex + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvUserHasAccount.text = signInSpannable
        binding.tvUserHasAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvUserHasAccount.highlightColor = Color.TRANSPARENT
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
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val confirmPassword = binding.etConfirmPassword.text.toString()

                viewModel.signUp(email, password, confirmPassword)
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

    private fun navigateToHomeFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToSignInFragment() {
        val action = SignUpFragmentDirections.toSignInFragment()
        navController.navigate(action)
    }

    private fun navigateToConfirmOtpFragment(email: String) {
        val action = SignUpFragmentDirections.toConfirmOtpFragment(email, otpType)
        navController.navigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.tvUserHasAccount.movementMethod = if (isLoading) null else LinkMovementMethod.getInstance()
        binding.llTextInputContainer.alpha = if (isLoading) 0.5f else 1f

        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignUp.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureMessage(error: Error) {
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.tvErrorMessage.text = getString(AuthErrorStringResProvider.get(error))
    }

    companion object {
        private const val LOG_TAG = "SignUpFragment"
    }
}