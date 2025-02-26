package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.features.auth.presentation.error.asStringRes
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.CreatePasswordViewModel
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asStringRes
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentCreatePasswordBinding

class CreatePasswordFragment : Fragment() {
    private var _binding: FragmentCreatePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String

    private val viewModel by viewModel<CreatePasswordViewModel>()
    private val args by navArgs<CreatePasswordFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = args.email
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            viewModel.signUp(email, password, confirmPassword)
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpResult.collect { state ->
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
        // TODO: Вход в приложение
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureSnackbar(error: Error) {
        val message = when (error) {
            is AuthError -> getString(error.asStringRes())
            is ValidationError -> getString(error.asStringRes())
            is NetworkError -> getString(error.asStringRes())
            else -> throw IllegalArgumentException("Unknown Error type: $this")
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.red))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .show()
    }

}