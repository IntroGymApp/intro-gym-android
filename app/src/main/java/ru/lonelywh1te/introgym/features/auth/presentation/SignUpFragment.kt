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
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthValidationError
import ru.lonelywh1te.introgym.features.auth.domain.model.OtpType
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignUpViewModel

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModel<SignUpViewModel>()
    private val settingsPreferences: SettingsPreferences by inject()

    private val navController by lazy { findNavController() }

    private val otpType = OtpType.CONFIRM_SIGNUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            binding.llTextInputContainer.setErrorMessage(null)

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            val validateResult = viewModel.validate(email, password, confirmPassword)

            when (validateResult) {
                is Result.Success -> navigateToConfirmOtpFragment(email)
                is Result.Failure -> showFailureMessage(validateResult.error)
                else -> return@setOnClickListener
            }
        }

        binding.passwordValidationView.apply {
            errorRequirements = mapOf(
                AuthValidationError.PASSWORD_TOO_SHORT to getString(R.string.label_min_8_symbols),
                AuthValidationError.PASSWORD_MISSING_UPPERCASE to getString(R.string.label_has_upper_letter),
                AuthValidationError.PASSWORD_MISSING_SPECIAL_SYMBOL to getString(R.string.label_has_special_symbol),
            )
        }

        binding.btnSkipSignUp.setOnClickListener {
            navigateToHomeFragment()
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

                    is UIState.Loading -> {
                        showLoadingIndicator(true)
                    }

                    is UIState.Failure -> {
                        if (state.error == AuthError.EMAIL_ALREADY_REGISTERED) {
                            navigateToSignInFragment()
                        }

                        showFailureMessage(state.error)
                        showLoadingIndicator(false)
                    }
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
                val validationResult = viewModel.getPasswordState(password.toString())
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
        val action = SignInFragmentDirections.toHomeFragment()
        findNavController().safeNavigate(action)

        settingsPreferences.isFirstLaunch = false
    }

    private fun navigateToSignInFragment() {
        val action = SignUpFragmentDirections.toSignInFragment()
        navController.safeNavigate(action)
    }

    private fun navigateToConfirmOtpFragment(email: String) {
        val action = SignUpFragmentDirections.toConfirmOtpFragment(email, otpType)
        navController.safeNavigate(action)
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
        binding.llTextInputContainer.setErrorMessage(getString(AuthErrorStringResProvider.get(error)))
    }

    companion object {
        private const val LOG_TAG = "SignUpFragment"
    }
}