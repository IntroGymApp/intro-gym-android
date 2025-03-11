package ru.lonelywh1te.introgym.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.WindowInsets
import ru.lonelywh1te.introgym.databinding.ActivityMainBinding

private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        enableEdgeToEdge()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbarVisibilityAndInsets(destination)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun updateToolbarVisibilityAndInsets(destination: NavDestination) {
        val shouldShowToolbar = destination.parent?.id !in listOf(
            R.id.onboarding,
            R.id.auth,
        )

        binding.toolbar.isVisible = shouldShowToolbar
        if (shouldShowToolbar) WindowInsets.setInsets(binding.root)
    }
}