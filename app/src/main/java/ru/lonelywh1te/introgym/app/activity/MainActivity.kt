package ru.lonelywh1te.introgym.app.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.NavGraphDirections
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.navigation.safeNavigate
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsetsHelper
import ru.lonelywh1te.introgym.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), UIController {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel by viewModel<MainActivityViewModel>()

    private var showingError: Job? = null

    private val bottomNavigationDestinations: Set<Int> = setOf(
        R.id.homeFragment,
        R.id.workoutsFragment,
        R.id.guideFragment,
        R.id.statsFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController
        setStartDestination()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in bottomNavigationDestinations) {
                binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
            }
        }

        setupBottomNavigationView()
        setupToolbar()
        startCollectFlows()
    }

    private fun setTheme() {
        val mode = if (viewModel.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, AppBarConfiguration(bottomNavigationDestinations))

        WindowInsetsHelper.setInsets(binding.toolbar, bottom = 0)
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.apply {
            setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                true
            }

            setOnItemReselectedListener {
                navController.popBackStack(it.itemId, false)
            }
        }
    }

    private fun setStartDestination() {
        val onboardingCompleted = viewModel.onboardingCompleted

        navController.safeNavigate(
            when {
                !onboardingCompleted -> NavGraphDirections.actionStartOnboarding()
                else -> return
            }
        )
    }

    private fun startCollectFlows() {
        viewModel.errors.flowWithLifecycle(lifecycle)
            .onEach { message ->
                showError(message)
            }
            .launchIn(lifecycleScope)
    }

    private fun showError(message: String) {
        showingError?.cancel()

        showingError = lifecycleScope.launch {
            binding.tvError.text = message
            binding.tvError.isVisible = true
            binding.tvError.scaleY = 0f

            binding.tvError.animate()
                .scaleY(1f)
                .setDuration(100)
                .start()

            delay(5000)

            binding.tvError.animate()
                .scaleY(0f)
                .setDuration(100)
                .withEndAction {
                    binding.tvError.isVisible = false
                }
                .start()
        }
    }

    override fun setToolbarVisibility(visible: Boolean) {
        if (binding.toolbar.isVisible == visible) return
        binding.toolbar.isVisible = visible
        binding.dividerTop.isVisible = visible
    }

    override fun setBottomNavigationViewVisibility(visible: Boolean) {
        if (binding.bottomNavigation.isVisible == visible) return
        binding.bottomNavigation.isVisible = visible
        binding.dividerBottom.isVisible = visible
    }
}