package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.ErrorSnackbar
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentSignInBinding
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.SignInViewModel

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModel<SignInViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.signIn(email, password)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.to_signUpFragment)
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInResult.collect { state ->
                    when (state) {
                        is UIState.Success -> {
                            navigateToHomeFragment()
                            showLoadingIndicator(false)
                        }
                        is UIState.isLoading -> {
                            showLoadingIndicator(true)
                        }
                        is UIState.Failure -> {
                            showFailureSnackbar(state.error)
                            showLoadingIndicator(false)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun navigateToHomeFragment() {
        TODO("Not yet implemented")
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureSnackbar(error: Error) {
        ErrorSnackbar(binding.root).show(getString(AuthErrorStringResProvider.get(error)))
    }
}