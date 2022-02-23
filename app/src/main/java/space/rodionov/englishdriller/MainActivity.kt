package space.rodionov.englishdriller

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import space.rodionov.englishdriller.databinding.ActivityMainBinding
import space.rodionov.englishdriller.feature_driller.utils.Constants.MODE_DARK
import space.rodionov.englishdriller.feature_driller.utils.Constants.MODE_LIGHT

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val vmMain: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment) { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }
        WindowCompat.setDecorFitsSystemWindows(window, true)



        this.lifecycleScope.launchWhenStarted {
            vmMain.mode.collectLatest {
                val mode = it ?: return@collectLatest

                setDefaultBarsColors(mode)
            }
        }

        this.lifecycleScope.launchWhenStarted {
            vmMain.followSystemMode.collectLatest {
                if (it) vmMain.updateMode(getSystemTheme())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (vmMain.followSystemMode.value) {
            vmMain.updateMode(getSystemTheme())
        }
    }

    //=================NIGHT MODE=====================
    private fun getSystemTheme(): Int {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> MODE_DARK
            Configuration.UI_MODE_NIGHT_NO -> MODE_LIGHT
            else -> MODE_LIGHT
        }
    }
    //=================NIGHT MODE=====================

    private fun setDefaultBarsColors(mode: Int) {
        when (mode) {
            MODE_LIGHT -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            MODE_DARK -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.gray850)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.gray850)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
            else -> {
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }
}



