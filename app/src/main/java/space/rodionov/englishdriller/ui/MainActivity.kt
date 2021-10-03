package space.rodionov.englishdriller.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.databinding.ActivityMainBinding

@AndroidEntryPoint // part 4
// part 1-2
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        setContentView(view)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNav.setupWithNavController(navController)

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.drillerFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, binding.root).let { c ->
                        c.hide(WindowInsetsCompat.Type.systemBars())
                        c.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
//                    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//                        v.updatePadding(top = insets.systemWindowInsetTop)
//                        insets
//                    }
                }
                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.bottomNav.visibility = View.VISIBLE
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    WindowInsetsControllerCompat(
                        window,
                        binding.root
                    ).show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

const val ADD_SOMETHING_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_SOMETHING_RESULT_OK = Activity.RESULT_FIRST_USER + 1