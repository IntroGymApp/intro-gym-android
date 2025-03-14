package ru.lonelywh1te.introgym.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.koin.android.ext.android.inject
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.databinding.ActivityMainBinding

private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val settingsPreferences: SettingsPreferences by inject<SettingsPreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        setStartDestination()
        setupBottomNavigationView()
        setupToolbar()
        setContentView(binding.root)

        enableEdgeToEdge()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbarBottomBarVisibilityAndInsets(destination)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.workoutsFragment,
            R.id.guideFragment,
            R.id.statsFragment,
            R.id.profileFragment)
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun updateToolbarBottomBarVisibilityAndInsets(destination: NavDestination) {
        val shouldShow = destination.parent?.id !in listOf(
            R.id.onboarding,
            R.id.auth,
        )

        binding.toolbar.isVisible = shouldShow
        binding.bottomNavigation.isVisible = shouldShow
        if (shouldShow) WindowInsets.setInsets(binding.root)
    }

    private fun setStartDestination() {
        val isFirstLaunch = settingsPreferences.isFirstLaunch
        val onboardingCompleted = settingsPreferences.onboardingCompleted

        val startDestination = when {
            !onboardingCompleted && isFirstLaunch -> R.id.onboarding
            onboardingCompleted && isFirstLaunch -> R.id.auth
            else -> R.id.homeFragment
        }

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.setGraph(navGraph, null)
    }
}