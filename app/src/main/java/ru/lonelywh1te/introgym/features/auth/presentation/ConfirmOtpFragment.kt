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
import ru.lonelywh1te.introgym.core.ui.WindowInsets
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

        WindowInsets.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

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
                    is UIState.Success -> {
                        isLoadingFragment(false)
                    }
                    is UIState.Loading -> {
                        isLoadingFragment(true)
                    }
                    is UIState.Failure -> {
                        if (state.error == AuthError.SESSION_STILL_EXIST) {
                            showFailureMessage(state.error)
                        } else {
                            val bundle = Bundle().apply { putSerializable(ERROR_BUNDLE_KEY, state.error) }

                            setFragmentResult(REQUEST_KEY, bundle)
                            findNavController().navigateUp()
                        }
                        isLoadingFragment(false)
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.confirmOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        val bundle = Bundle().apply { putBoolean(RESULT_BUNDLE_KEY, true) }

                        setFragmentResult(REQUEST_KEY, bundle)
                        findNavController().navigateUp()
                        showLoadingIndicator(false)
                    }
                    is UIState.Loading -> {
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

    private fun isLoadingFragment(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE

        binding.tvConfirmOtpTitle.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.tvConfirmOtpDescription.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.tvErrorMessage.visibility = if (!isLoading) View.GONE else View.VISIBLE
        binding.etOtp.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.flSignUpButtonContainer.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.btnSendOtp.visibility = if (!isLoading) View.VISIBLE else View.GONE

    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbBtnLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
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