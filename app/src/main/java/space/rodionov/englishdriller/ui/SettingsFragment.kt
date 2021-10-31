package space.rodionov.englishdriller.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.databinding.SettingsLayoutBinding

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
                switchTranslationDirection.text = if (it) "С русского на инглиш" else "С инглиша на русский"

                switchTranslationDirection.setOnCheckedChangeListener { switchBtn, isChecked ->
                    viewModel.saveTransDir(switchBtn.isChecked)
                }
                switchMode.setOnCheckedChangeListener { switchBtn, isChecked ->
                    if (!isChecked) viewModel.saveMode(0) else viewModel.saveMode(1)
                }
            }
        }
    }
}