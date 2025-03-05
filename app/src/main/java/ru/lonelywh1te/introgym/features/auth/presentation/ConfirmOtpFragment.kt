package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.ErrorSnackbar
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentConfirmOtpBinding
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.ConfirmOtpViewModel

class ConfirmOtpFragment : Fragment() {
    private var _binding: FragmentConfirmOtpBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String

    private val viewModel by viewModel<ConfirmOtpViewModel>()
    private val args by navArgs<ConfirmOtpFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = args.email
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmOtpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmOtp.setOnClickListener {
            val otp = binding.etOtp.text.toString()
            viewModel.confirmOtp(otp)
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.confirmOtpResult.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        navigateToCreatePasswordFragment()
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
            .launchIn(lifecycleScope)
    }

    private fun navigateToCreatePasswordFragment() {
        val action = ConfirmOtpFragmentDirections.toCreatePasswordFragment(email)
        findNavController().navigate(action)
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnConfirmOtp.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureSnackbar(error: Error) {
        ErrorSnackbar(binding.root).show(getString(AuthErrorStringResProvider.get(error)))
    }
}