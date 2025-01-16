package com.syndicate.ptkscheduleapp

import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.syndicate.ptkscheduleapp.di.initKoin
import com.syndicate.ptkscheduleapp.navigation.featureSearchModule

fun MainViewController() = ComposeUIViewController(
    configure = {
        ScreenRegistry { featureSearchModule() }
        initKoin()
    }
) { App() }