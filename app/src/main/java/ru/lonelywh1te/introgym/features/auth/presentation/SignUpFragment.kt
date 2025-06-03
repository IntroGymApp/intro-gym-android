package ru.lonelywh1te.introgym.features.auth.presentation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
import ru.lonelywh1te.introgym.app.activity.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.extensions.setClickableSpan
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsets
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.databinding.FragmentSignUpBinding
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
        hideToolbarAndBottomNavigationView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            hideErrorMessage()

            validateEmailAndPassword()
                .onSuccess { email -> navigateToConfirmOtpFragment(email) }
                .onFailure { error -> showErrorMessage(error) }
        }

        binding.passwordValidationView.apply {
            errorRequirements = mapOf(
                AuthValidationError.PasswordTooShort() to getString(R.string.label_min_8_symbols),
                AuthValidationError.PasswordMissingUppercase() to getString(R.string.label_has_upper_letter),
                AuthValidationError.PasswordMissingSpecialSymbol() to getString(R.string.label_has_special_symbol),
            )
        }

        binding.btnSkipSignUp.setOnClickListener {
            settingsPreferences.isFirstLaunch = false
            navigateToHomeFragment()
        }

        binding.etPassword.addTextChangedListener { password ->
            val validationResult = viewModel.validatePassword(password.toString())
            binding.passwordValidationView.setCurrentErrors(validationResult)
        }

        startCollectFlows()
        setConfirmOtpFragmentResultListener()
        setSpannableStrings()
    }

    private fun startCollectFlows() {
        viewModel.signUpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        settingsPreferences.isFirstLaunch = false

                        navigateToHomeFragment()
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

    private fun setSpannableStrings() {
        binding.tvUserHasAccount.setClickableSpan(substring = getString(R.string.label_sign_in)) {
            navigateToSignInFragment()
        }

        binding.tvUserHasAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvUserHasAccount.highlightColor = Color.TRANSPARENT
    }

    private fun validateEmailAndPassword(): Result<String> {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        viewModel.validate(email, password, confirmPassword)
            .onFailure { return Result.Failure(it) }

        return Result.Success(email)
    }

    private fun setConfirmOtpFragmentResultListener() {
        setFragmentResultListener(ConfirmOtpFragment.REQUEST_KEY) { _, bundle ->
            val isConfirmed = bundle.getBoolean(ConfirmOtpFragment.RESULT_BUNDLE_KEY)

            val error = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(ConfirmOtpFragment.ERROR_BUNDLE_KEY, BaseError::class.java)
            } else {
                bundle.getSerializable(ConfirmOtpFragment.ERROR_BUNDLE_KEY) as BaseError
            }

            if (isConfirmed) {
                signUp()
            } else {
                error?.let { showErrorMessage(it) }
            }
        }
    }

    private fun signUp() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        viewModel.signUp(email, password, confirmPassword)
    }

    private fun navigateToHomeFragment() {
        val action = SignInFragmentDirections.toHomeFragment()
        findNavController().safeNavigate(action)
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

    private fun showErrorMessage(error: BaseError) {
        binding.llTextInputContainer.setErrorMessage(getString(AuthErrorStringResProvider.get(error)))
    }

    private fun hideErrorMessage() {
        binding.llTextInputContainer.setErrorMessage(null)
    }

    private fun hideToolbarAndBottomNavigationView() {
        (requireActivity() as UIController).apply {
            setToolbarVisibility(false)
            setBottomNavigationViewVisibility(false)
        }
    }
}