package com.krecsa.sublurk.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.krecsa.sublurk.R
import com.krecsa.sublurk.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    private var isUpdating = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.theme.collect { theme ->
                isUpdating = true
                when (theme) {
                    "light" -> binding.themeLight.isChecked = true
                    "dark" -> binding.themeDark.isChecked = true
                    else -> binding.themeSystem.isChecked = true
                }
                isUpdating = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.language.collect { lang ->
                isUpdating = true
                when (lang) {
                    "en" -> binding.langEn.isChecked = true
                    "ru" -> binding.langRu.isChecked = true
                    else -> binding.langSystem.isChecked = true
                }
                isUpdating = false
            }
        }

        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isUpdating) return@setOnCheckedChangeListener
            val theme = when (checkedId) {
                R.id.themeLight -> "light"
                R.id.themeDark -> "dark"
                else -> "system"
            }
            viewModel.setTheme(theme)
        }

        binding.languageGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isUpdating) return@setOnCheckedChangeListener
            val lang = when (checkedId) {
                R.id.langEn -> "en"
                R.id.langRu -> "ru"
                else -> "system"
            }
            viewModel.setLanguage(lang)
        }

        binding.clearCacheButton.setOnClickListener {
            Snackbar.make(binding.root, "Кеш очищен (заглушка)", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}