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
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.app.activity.UIController
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.ErrorDispatcher
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsetsHelper
import ru.lonelywh1te.introgym.databinding.FragmentConfirmOtpBinding
import ru.lonelywh1te.introgym.features.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.features.auth.presentation.error.AuthErrorStringResProvider
import ru.lonelywh1te.introgym.features.auth.presentation.viewModel.ConfirmOtpViewModel

class ConfirmOtpFragment : Fragment() {
    private var _binding: FragmentConfirmOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ConfirmOtpViewModel>()
    private val args by navArgs<ConfirmOtpFragmentArgs>()
    private val errorDispatcher by inject<ErrorDispatcher>()

    private val email by lazy { args.email }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setOtpType(args.otpType)
        viewModel.sendOtp(email)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentConfirmOtpBinding.inflate(layoutInflater, container, false)

        WindowInsetsHelper.setInsets(binding.root, left = binding.root.paddingStart, end = binding.root.paddingEnd)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirmOtp.setOnClickListener {
            hideErrorMessage()
            confirmOtp()
        }

        hideToolbarAndBottomNavigationView()
        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.sendOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        isLoadingState(false)
                    }
                    is UIState.Loading -> {
                        isLoadingState(true)
                    }
                    is UIState.Failure -> {
                        if (state.error is AuthError.SessionStillExist) {
                            showErrorMessage(state.error)
                        } else {
                            navigateUpWithError(state.error)
                        }

                        isLoadingState(false)
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.confirmOtpResult.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is UIState.Success -> {
                        navigateUpWithResult()
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

    private fun confirmOtp() {
        val otp = binding.etOtp.text.toString()
        viewModel.confirmOtp(otp)
    }

    private fun isLoadingState(isLoading: Boolean) {
        binding.pbLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE

        binding.tvConfirmOtpTitle.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.tvConfirmOtpDescription.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.etOtp.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.flSignUpButtonContainer.visibility = if (!isLoading) View.VISIBLE else View.GONE
        binding.btnSendOtp.visibility = if (!isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateUpWithResult() {
        val bundle = Bundle().apply { putBoolean(RESULT_BUNDLE_KEY, true) }

        setFragmentResult(REQUEST_KEY, bundle)
        findNavController().navigateUp()
    }

    private fun navigateUpWithError(error: BaseError) {
        errorDispatcher.dispatch(error, error.throwable?.message)
        findNavController().navigateUp()
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.pbBtnLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnConfirmOtp.visibility = if (isLoading) View.GONE else View.VISIBLE
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

    companion object {
        const val REQUEST_KEY = "CONFIRM_OTP_REQUEST"
        const val RESULT_BUNDLE_KEY = "CONFIRM_OTP_RESULT"
    }
}