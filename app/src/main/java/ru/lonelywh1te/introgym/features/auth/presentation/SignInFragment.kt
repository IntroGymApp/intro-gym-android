package ru.lonelywh1te.introgym.features.auth.presentation

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.app.UIController
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.extensions.setClickableSpan
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsets
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.databinding.FragmentSignInBinding
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignInViewModel

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModel<SignInViewModel>()
    private val settingsPreferences: SettingsPreferences by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            hideErrorMessage()
            signIn()
        }

        hideToolbarAndBottomNavigationView()
        startCollectFlows()
        setSpannableStrings()
    }

    private fun startCollectFlows() {
        viewModel.signInResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
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
        binding.tvUserForgotPassword.setClickableSpan {
            navigateToForgotPasswordFragment()
        }

        binding.tvUserForgotPassword.movementMethod = LinkMovementMethod.getInstance()
        binding.tvUserForgotPassword.highlightColor = Color.TRANSPARENT
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        viewModel.signIn(email, password)
    }

    private fun navigateToHomeFragment() {
        val action = SignInFragmentDirections.toHomeFragment()
        findNavController().safeNavigate(action)
    }

    private fun navigateToForgotPasswordFragment() {
        val action = SignInFragmentDirections.toForgotPasswordFragment()
        findNavController().safeNavigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
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