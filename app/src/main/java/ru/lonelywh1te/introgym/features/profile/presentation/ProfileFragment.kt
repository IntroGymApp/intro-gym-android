package ru.lonelywh1te.introgym.features.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.dialogs.SubmitDialogFragment
import ru.lonelywh1te.introgym.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ProfileFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUserInfo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateProfileUi(viewModel.getIsSignedIn())

        binding.btnSignUp.setOnClickListener {
            navigateToSignUp()
        }

        binding.btnSignOut.setOnClickListener {
            showSubmitSignOutDialog()
        }

        startCollectFlows()
    }

    private fun startCollectFlows() {
        viewModel.userInfo.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach { userInfo ->
                binding.tvUserName.text = userInfo.name
                binding.tvUserRegisterDate.text = getString(R.string.label_joined_at, userInfo.registerDate)
                binding.tvCountOfWorkouts.text = userInfo.countOfWorkouts.toString()
            }
            .launchIn(lifecycleScope)
    }

    private fun showSubmitSignOutDialog() {
        SubmitDialogFragment.Builder()
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile))
            .setIconTint(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
            .setTitle(getString(R.string.label_sign_out_title))
            .setMessage(getString(R.string.label_sign_out_description))
            .setPositiveButton(getString(R.string.label_cancel)) {
                it.dismiss()
            }
            .setNegativeButton(getString(R.string.label_exit)) {
                viewModel.signOut()
                updateProfileUi(viewModel.getIsSignedIn())

                it.dismiss()
            }
            .build()
            .show(childFragmentManager, SubmitDialogFragment.TAG)
    }

    private fun updateProfileUi(isSignedIn: Boolean) {
        binding.groupSignedIn.isVisible = isSignedIn
        binding.groupSignedOut.isVisible = !isSignedIn
    }

    private fun navigateToSignUp() {
        val action = ProfileFragmentDirections.actionAuth()
        findNavController().safeNavigate(action)
    }
}