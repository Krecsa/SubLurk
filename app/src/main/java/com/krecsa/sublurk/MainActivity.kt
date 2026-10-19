package com.krecsa.sublurk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krecsa.sublurk.data.SettingsManager
import com.krecsa.sublurk.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager.applyThemeFromPrefs()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}