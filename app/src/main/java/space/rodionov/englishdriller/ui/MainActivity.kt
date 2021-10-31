package space.rodionov.englishdriller.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.databinding.ActivityMainBinding
import space.rodionov.englishdriller.util.BottomNavManager
import space.rodionov.englishdriller.util.fetchColors
import space.rodionov.englishdriller.util.fetchTheme

private const val TAG = "MainActivity LOGS"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val bottomNavManager = BottomNavManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//        view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        setContentView(view)

        // supernavigation
        bottomNavManager.start(binding.bottomNav.menu.getItem(3))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        val bottomNavView = binding.bottomNav
        bottomNavView.setupWithNavController(navController)

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.drillerFragment,
                R.id.categoriesFragment,
                R.id.vocabularyFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // supernavigation
        binding.bottomNav.setOnItemSelectedListener {
            bottomNavManager.changeNav(it)
            NavigationUI.onNavDestinationSelected(it, navController)
        }

//==============================CHANGE THEME================================================

        viewModel.mode.observe(this, {
            binding.apply {
                bottomNav.itemIconTintList = null
                bottomNav.itemTextColor = null

                val theme = fetchTheme(it, resources)
                val colors = theme.fetchColors()
//                changeTheme(theme, colors)

                toolbar.setBackgroundColor(colors[0])
                toolbar.setTitleTextColor(colors[3])

                val bottomNavBarStateList = arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                )
                val bottomNavColorList = intArrayOf(
                    colors[5],
                    colors[6]
                )
                val colorStateList = ColorStateList(bottomNavBarStateList, bottomNavColorList)
                bottomNav.itemTextColor = colorStateList
                bottomNav.itemIconTintList = colorStateList
                bottomNav.setBackgroundColor(colors[8])
            }
        })


//===================================INSETS AND SYSTEM BARS================================

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        /* navController.addOnDestinationChangedListener { controller, destination, arguments ->
             when (destination.id) {
                 R.id.drillerFragment -> {
                     requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                     binding.toolbar.visibility = View.GONE
                     binding.bottomNav.visibility = View.GONE
                     WindowCompat.setDecorFitsSystemWindows(window, true)
                     WindowInsetsControllerCompat(window, binding.root).let { c ->
                         c.hide(WindowInsetsCompat.Type.systemBars())
                         c.systemBarsBehavior =
                             WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                     }
                 }
                 else -> {
                     requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                     binding.toolbar.visibility = View.VISIBLE
                     binding.bottomNav.visibility = View.VISIBLE
                     WindowCompat.setDecorFitsSystemWindows(window, true)
                     WindowInsetsControllerCompat(
                         window,
                         binding.root
                     ).show(WindowInsetsCompat.Type.systemBars())
                 }
             }
         }*/
    }

    /*fun fullscreenAndFreeOrientation(on: Boolean) {
        when (on) {
            true -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                binding.toolbar.visibility = View.GONE
                binding.bottomNav.visibility = View.GONE
                WindowCompat.setDecorFitsSystemWindows(window, true)
                WindowInsetsControllerCompat(window, binding.root).let { c ->
                    c.hide(WindowInsetsCompat.Type.systemBars())
                    c.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            false -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                binding.toolbar.visibility = View.VISIBLE
                binding.bottomNav.visibility = View.VISIBLE
                WindowCompat.setDecorFitsSystemWindows(window, true)
                WindowInsetsControllerCompat(
                    window,
                    binding.root
                ).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }*/

//==========================STYLE CHANGE METHODS=========================

/*    private fun changeTheme(theme: Resources.Theme, colors: Array<Int>) {
        //=====================================FOREGROUND====================================
        val bottomIcon0 =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_driller_selector, theme)
        val bottomIcon1 =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_categories_selector, theme)
        val bottomIcon2 = ResourcesCompat.getDrawable(resources, R.drawable.ic_list_selector, theme)
        val bottomIcon3 =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_selector, theme)
        binding.apply {
            bottomNav.menu.getItem(0).setIcon(bottomIcon0)
            bottomNav.menu.getItem(1).setIcon(bottomIcon1)
            bottomNav.menu.getItem(2).setIcon(bottomIcon2)
            bottomNav.menu.getItem(3).setIcon(bottomIcon3)

            val bottomNavBarStateList = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )

            val typedValueOn = TypedValue()
            val typedValueOff = TypedValue()
            theme.resolveAttribute(R.attr.iconSelected, typedValueOn, true)
            theme.resolveAttribute(R.attr.iconUnselected, typedValueOff, true)

            val colorList = intArrayOf(
                typedValueOn.data,
                typedValueOff.data
            )
            val colorStateList = ColorStateList(bottomNavBarStateList, colorList)
//            bottomNav.itemIconTintList = colorStateList
            bottomNav.itemTextColor = colorStateList

            //=================================BACKGROUND===============================
            val typedValueBottomNavBG = TypedValue()
            theme.resolveAttribute(R.attr.bottomNavBG, typedValueBottomNavBG, true)
            bottomNav.setBackgroundColor(colors[8])
        }
    }*/

//=====================OTHERS========================================

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)/* || super.onSupportNavigateUp()*/
    }

    // supernavigation
    override fun onBackPressed() {
        if (setOf(
                R.id.drillerFragment,
                R.id.categoriesFragment,
                R.id.vocabularyFragment,
                R.id.settingsFragment
            ).contains(navController.currentDestination?.id)
        ) {
            bottomNavManager.backItemId()?.let {
                NavigationUI.onNavDestinationSelected(it, navController)
            } ?: super.onBackPressed()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

const val ADD_SOMETHING_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_SOMETHING_RESULT_OK = Activity.RESULT_FIRST_USER + 1