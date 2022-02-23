package space.rodionov.englishdriller.feature_driller.presentation.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import space.rodionov.englishdriller.MainActivity
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.core.fetchColors
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.databinding.FragmentSettingsBinding
import space.rodionov.englishdriller.feature_driller.utils.Constants.MODE_DARK
import space.rodionov.englishdriller.feature_driller.utils.Constants.MODE_LIGHT

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding

    private val vmSettings: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding?.apply {

            btnBack.setOnClickListener {
                (activity as MainActivity).onBackPressed()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                vmSettings.transDir.collectLatest {
                    val nativeToForeign = it ?: return@collectLatest
                    val transDirText =
                        if (nativeToForeign) resources.getString(R.string.from_ru_to_en)
                        else resources.getString(R.string.from_en_to_ru)
                    switchTransdir.text = transDirText
                    switchTransdir.setOnCheckedChangeListener(null)
                    switchTransdir.isChecked = nativeToForeign
                    switchTransdir.setOnCheckedChangeListener { _, isChecked ->
                        vmSettings.updateTransDir(isChecked)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                vmSettings.mode.collectLatest {
                    val mode = it ?: return@collectLatest
                    switchMode.setOnCheckedChangeListener(null)
                    switchMode.isChecked = mode == MODE_DARK
                    switchMode.setOnCheckedChangeListener { _, isChecked ->
                        if (!vmSettings.followSystemMode.value) vmSettings.updateMode(if (isChecked) MODE_DARK else MODE_LIGHT)
                    }

                    (root as ViewGroup).redrawViewGroup(mode)
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                vmSettings.followSystemMode.collectLatest {
                    val follow = it ?: return@collectLatest

                    if (follow) {
                        switchMode.setTextColor(resources.getColor(R.color.gray600))
                        switchMode.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.gray600))
                        switchMode.isEnabled = false
                    } else {
                        switchMode.setTextColor(fetchColors(vmSettings.mode.value, resources)[3])
                        switchMode.thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
                        switchMode.isEnabled = true
                    }

                    switchFollowSystemMode.setOnCheckedChangeListener(null)
                    switchFollowSystemMode.isChecked = follow
                    switchFollowSystemMode.setOnCheckedChangeListener { _, isChecked ->
                        vmSettings.updateFollowSystemMode(isChecked)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

