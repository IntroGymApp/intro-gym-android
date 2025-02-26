package ru.lonelywh1te.introgym.auth.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.auth.domain.error.AuthError
import ru.lonelywh1te.introgym.auth.domain.error.ValidationError
import ru.lonelywh1te.introgym.auth.presentation.error.asStringRes
import ru.lonelywh1te.introgym.auth.presentation.viewmodel.ConfirmOtpViewModel
import ru.lonelywh1te.introgym.core.network.NetworkError
import ru.lonelywh1te.introgym.core.network.asStringRes
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.ui.UIState
import ru.lonelywh1te.introgym.databinding.FragmentConfirmOtpBinding

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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.confirmOtpResult.collect { state ->
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
            }
        }
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