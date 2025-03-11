package ru.lonelywh1te.introgym.features.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentConfirmOtpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.ConfirmOtpViewModel

class ConfirmOtpFragment : Fragment() {
    private var _binding: FragmentConfirmOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ConfirmOtpViewModel>()
    private val args by navArgs<ConfirmOtpFragmentArgs>()

    private val email by lazy { args.email }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setOtpType(args.otpType)
        viewModel.sendOtp(email)
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
        viewModel.sendOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Failure -> {
                        if (state.error == AuthError.SESSION_STILL_EXIST) {
                            showFailureMessage(state.error)
                        } else {
                            val bundle = Bundle().apply { putSerializable(ERROR_BUNDLE_KEY, state.error) }

                            setFragmentResult(REQUEST_KEY, bundle)
                            findNavController().navigateUp()
                        }
                    }

                    else -> {}
                }
            }
            .launchIn(lifecycleScope)

        viewModel.confirmOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        val bundle = Bundle().apply { putBoolean(ERROR_BUNDLE_KEY, true) }

                        setFragmentResult(REQUEST_KEY, bundle)
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

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnConfirmOtp.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showFailureMessage(error: Error) {
        binding.tvErrorMessage.apply {
            text = getString(AuthErrorStringResProvider.get(error))
            visibility = View.VISIBLE
        }
    }

    companion object {
        const val REQUEST_KEY = "CONFIRM_OTP_REQUEST"
        const val RESULT_BUNDLE_KEY = "CONFIRM_OTP_RESULT"
        const val ERROR_BUNDLE_KEY = "CONFIRM_OTP_RESULT"
    }
}