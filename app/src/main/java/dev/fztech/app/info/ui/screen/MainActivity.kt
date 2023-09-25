package dev.fztech.app.info.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import dev.fztech.app.info.ui.component.app_update.InAppUpdateView
import dev.fztech.app.info.ui.theme.AppInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppInfoTheme {
                InAppUpdateView {
                    MainScreen()
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