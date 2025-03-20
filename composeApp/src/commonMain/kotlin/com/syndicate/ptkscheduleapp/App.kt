package com.syndicate.ptkscheduleapp

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.FadeTransition
import com.syndicate.ptkscheduleapp.core.presentation.AppViewModel
import com.syndicate.ptkscheduleapp.core.presentation.theme.AppTheme
import com.syndicate.ptkscheduleapp.core.presentation.theme.colorPalette
import com.syndicate.ptkscheduleapp.feature.splash.presentation.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalVoyagerApi::class)
@Composable
@Preview
fun App() {

    ProvideNavigatorLifecycleKMPSupport {

        val appViewModel = koinViewModel<AppViewModel>()
        val state by appViewModel.state.collectAsState()

        AppTheme(themeMode = state.themeMode) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorPalette.backgroundColor)
            ) {

                Navigator(
                    screen = SplashScreen(),
                    disposeBehavior = NavigatorDisposeBehavior(disposeSteps = false)
                ) { navigator ->

                    FadeTransition(
                        navigator = navigator,
                        disposeScreenAfterTransitionEnd = true,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = Ease,
                            delayMillis = 100
                        )
                    )
                }
            }
        }
    }
}