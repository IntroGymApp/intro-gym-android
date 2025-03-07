package ru.lonelywh1te.introgym.app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.forEach
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.lonelywh1te.introgym.R
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
        setEdgeToEdge()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(LOG_TAG, "DESTINATION: ${destination.label}")

            val currentBackStackEntry = navController.currentBackStackEntry
            val graph = navController.graph
            val destinations = graph.nodes

            val stackLog = StringBuilder("Current Fragment Destinations:\n")
            destinations.forEach { _, destination ->
                stackLog.append("Destination ID: ${destination.id}, ")
                stackLog.append("Destination Label: ${destination.label}, ")
                stackLog.append("Destination Arguments: ${destination.arguments}\n")
            }

            Log.d(LOG_TAG, stackLog.toString())  // Логируем destination
        }
    }

    private fun setEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}