package ru.lonelywh1te.introgym.features.auth.presentation

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.databinding.FragmentSignInBinding
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignInViewModel

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModel<SignInViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.signIn(email, password)
        }

        startCollectFlows()
        setSpannableStrings()
    }

    private fun startCollectFlows() {
        viewModel.signInResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
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
                        showFailureMessage(state.error)
                        showLoadingIndicator(false)
                    }
                    else -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setSpannableStrings() {
        val forgotPasswordSpannable = SpannableString(binding.tvUserForgotPassword.text)

        forgotPasswordSpannable.setSpan(object: ClickableSpan(){
            override fun onClick(view: View) {
                navigateToForgotPasswordFragment()
            }
        }, 0, forgotPasswordSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvUserForgotPassword.text = forgotPasswordSpannable
        binding.tvUserForgotPassword.movementMethod = LinkMovementMethod.getInstance()
        binding.tvUserForgotPassword.highlightColor = Color.TRANSPARENT
    }

    private fun navigateToHomeFragment() {
        // TODO: Not yet implemented
    }

    private fun navigateToForgotPasswordFragment() {
        val action = SignInFragmentDirections.toForgotPasswordFragment()
        findNavController().navigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureMessage(error: Error) {
        binding.tvErrorMessage.apply {
            text = getString(AuthErrorStringResProvider.get(error))
            visibility = View.VISIBLE
        }
    }
}