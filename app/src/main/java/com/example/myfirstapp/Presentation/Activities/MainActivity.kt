package com.example.myfirstapp.Presentation.Activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale


class MainActivity : AppCompatActivity(), StringProvider {
    private val loginViewModel: LoginViewModel by viewModel()
    private val guestViewModel: GuestViewModel by viewModel()

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applySavedLanguage()

        navController = (supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment).navController
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)

        setupBottomNavigation()
        setupFab()
        setupDestinationListener()

        if (savedInstanceState == null) {
            checkTokenAndNavigate()
        }
    }

    private fun applySavedLanguage() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val language = prefs.getString("selected_language", "ru") ?: "ru"
        val newLocale = Locale(language)
        Locale.setDefault(newLocale)
        val config = resources.configuration
        config.setLocale(newLocale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun checkTokenAndNavigate() {
        val token = loginViewModel.getTokenFromPreferences()
        lifecycleScope.launch {
            if (token != null) {
                loginViewModel.isTokenValid(token).observe(this@MainActivity) { isValid ->
                    if (isValid) {
                        val user = loginViewModel.getUserFromPreferences()
                        if (user != null) {
                            navController.navigate(R.id.homeFragment)
                            guestViewModel.setUser(user)
                        } else {
                            navController.navigate(R.id.entryFragment)
                        }
                    } else {
                        navController.navigate(R.id.entryFragment)
                    }
                }
            } else {
                navController.navigate(R.id.registerFragment)
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            navController.navigate(menuItem.itemId)
            true
        }
    }

    private fun setupFab() {
        fab.setOnClickListener {
            navController.navigate(R.id.bookingTableFragment)
        }
    }

    private fun setupDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomBar = when (destination.id) {
                R.id.bookingTableFragment,
                R.id.bookSuccessFragment,
                R.id.tableReservationFragment,
                R.id.orderHistoryFragment,
                R.id.paymentFragment,
                R.id.addCardFragment,
                R.id.bookingHistoryFragment,
                R.id.profileFragment,
                R.id.detailFragment,
                R.id.entryFragment,
                R.id.registerFragment -> true
                R.id.paymentDetailsFragment -> true
                else -> false
            }
            coordinatorLayout.visibility = if (hideBottomBar) View.GONE else View.VISIBLE
        }
    }

    override fun getStringResource(resId: Int): String = getString(resId)

    fun getNavController(): NavController = navController
}
