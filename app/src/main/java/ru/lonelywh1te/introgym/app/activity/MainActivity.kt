package ru.lonelywh1te.introgym.app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.core.ui.utils.WindowInsets
import ru.lonelywh1te.introgym.databinding.ActivityMainBinding
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.presentation.WorkoutTrackingService
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), UIController {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel by viewModel<MainActivityViewModel>()

    private var showingError: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        setStartDestination()
        setupBottomNavigationView()
        setupToolbar()
        startCollectFlows()
        setContentView(binding.root)

        enableEdgeToEdge()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateInsets(destination)
        }

        restoreWorkoutTrackerService()
    }

    private fun setTheme() {
        val mode = if (viewModel.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.workoutsFragment,
                R.id.guideFragment,
                R.id.statsFragment,
                R.id.profileFragment
            )
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.setupWithNavController(navController)
    }

    // TODO: передать полностью контроль над инсетами
    private fun updateInsets(destination: NavDestination) {
        val shouldUpdate = destination.parent?.id !in listOf(
            R.id.onboarding,
        )

        if (shouldUpdate) WindowInsets.setInsets(binding.root)
    }

    private fun setStartDestination() {
        val onboardingCompleted = viewModel.onboardingCompleted

        Log.d("MainActivity", onboardingCompleted.toString())

        val startDestination = when {
            !onboardingCompleted -> R.id.onboarding
            else -> R.id.homeFragment
        }

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.setGraph(navGraph, null)
    }

    private fun restoreWorkoutTrackerService() {
        lifecycleScope.launch {
            val workoutLogRepository: WorkoutLogRepository by inject()

            workoutLogRepository.getWorkoutLogWithStartDateTime()
                .onSuccess {
                    if (it != null) startWorkoutTrackingService(it.startDateTime)
                }
        }
    }


    private fun startWorkoutTrackingService(startDateTime: LocalDateTime? = null) {
        startService(
            Intent(this, WorkoutTrackingService::class.java).apply {
                action = WorkoutTrackingService.ACTION_START
                startDateTime?.let { startDateTime ->
                    putExtra(WorkoutTrackingService.START_LOCAL_DATE_TIME_EXTRA, startDateTime.toString())
                }
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