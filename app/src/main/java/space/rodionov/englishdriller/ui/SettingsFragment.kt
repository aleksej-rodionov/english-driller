package space.rodionov.englishdriller.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.databinding.SettingsLayoutBinding
import space.rodionov.englishdriller.util.fetchColors
import space.rodionov.englishdriller.util.fetchTheme

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_layout) {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = SettingsLayoutBinding.bind(view)

        binding.apply {
            viewModel.readTransDir.observe(viewLifecycleOwner) {
                Log.d(TAG, "onViewCreated:  transDirPreference = $it")
                switchTranslationDirection.isChecked = it
                switchTranslationDirection.text =
                    if (it) "С русского на инглиш" else "С инглиша на русский"
            }

            viewModel.mode.observe(viewLifecycleOwner) {
                switchMode.apply {
                    isChecked = it == 1
                    text = if (it == 1) "Ночная" else "Дневная"
                }

                val theme = fetchTheme(it, resources)
                val colors = theme.fetchColors()
                root.background = colors[8].toDrawable()
                tvMode.setTextColor(colors[2])
                tvTranslationDirection.setTextColor(colors[2])
                switchMode.apply {
                    setTextColor(colors[3])
                    thumbTintList = ColorStateList.valueOf(colors[4])
                    trackTintList = ColorStateList.valueOf(colors[4])
                }
                switchTranslationDirection.apply {
                    setTextColor(colors[3])
                    thumbTintList = ColorStateList.valueOf(colors[4])
                    trackTintList = ColorStateList.valueOf(colors[4])
                }
            }

            switchTranslationDirection.setOnCheckedChangeListener { switchBtn, isChecked ->
                viewModel.saveTransDir(switchBtn.isChecked)
            }

            switchMode.setOnCheckedChangeListener { switchBtn, isChecked ->
                if (!switchBtn.isChecked) viewModel.saveMode(0) else viewModel.saveMode(1)
            }
        }
    }
}