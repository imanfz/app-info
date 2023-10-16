package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.fztech.app.info.ui.component.app_update.InAppUpdateView
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.utils.INFO_KEY
import dev.fztech.app.info.utils.extenstions.navigate
import dev.fztech.app.info.utils.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val viewModel = viewModel<AppInfoViewModel>()

            AppInfoTheme {
                InAppUpdateView {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Home.route,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                animationSpec = tween(700)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                                animationSpec = tween(700)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                                animationSpec = tween(700)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                                animationSpec = tween(700)
                            )
                        }
                    ) {
                        composable(Routes.Home.route) {
                            MainScreen(viewModel) {
                                navController.navigate(
                                    Routes.Detail.route,
                                    bundleOf(INFO_KEY to it)
                                )
                            }
                        }
                        composable(Routes.Detail.route) {
                            @Suppress("DEPRECATION")
                            it.arguments?.apply {
                                val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                    getParcelable(INFO_KEY, PackageInfo::class.java)
                                else getParcelable(INFO_KEY)

                                info?.let { packageInfo ->
                                    DetailScreen(
                                        data = packageInfo
                                    ) {
                                        navController.navigateUp()
                                    }
                                } ?: run {
                                    navController.navigateUp()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(device = "id:pixel_7", showSystemUi = true)
@Composable
fun AppPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    AppInfoTheme {
        MainScreenPreview(text)
    }
}