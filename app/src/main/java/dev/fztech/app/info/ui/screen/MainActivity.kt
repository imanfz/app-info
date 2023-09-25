package dev.fztech.app.info.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.fztech.app.info.ui.component.app_update.InAppUpdateView
import dev.fztech.app.info.ui.navigation.Screen
import dev.fztech.app.info.ui.theme.AppInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()

            AppInfoTheme {
                InAppUpdateView {
                    NavHost(navController = navController, startDestination = Screen.Home.route ) {
                        composable(Screen.Home.route) {
                            MainScreen(navController)
                        }
//                        composable(
//                            Screen.Detail.route,
//                            arguments = listOf(navArgument("data") { type = PackageInfo::class.java})
//                        ) {
//                            DetailScreen(navController, data = it.arguments?.getParcelable("data", PackageInfo::class.java))
//                        }
                    }

                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AppPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    AppInfoTheme {
        MainScreenPreview(text)
    }
}