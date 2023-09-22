package dev.fztech.app.info.ui.screen

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.ExpandableSearchView
import dev.fztech.app.info.ui.component.app_update.InAppUpdateView
import dev.fztech.app.info.ui.theme.AppInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var query by remember { mutableStateOf("") }
            val list = LocalContext.current.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

            AppInfoTheme {
                InAppUpdateView {
                    // A surface container using the 'background' color from the theme
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            ExpandableSearchView(
                                title = stringResource(id = R.string.app_name),
                                searchDisplay = query,
                                onSearchDisplayChanged = {
                                    query = it
                                },
                                onSearchDisplayClosed = {
                                    query = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary),
                                backgroundEnabled = true
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        ListItem(modifier = Modifier.padding(it), list = list)
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    var query by remember { mutableStateOf("") }
    val list = LocalContext.current.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    AppInfoTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ExpandableSearchView(
                    title = stringResource(id = R.string.app_name),
                    searchDisplay = query,
                    onSearchDisplayChanged = {
                        query = it
                    },
                    onSearchDisplayClosed = {
                        query = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    backgroundEnabled = true
                )
            }
        ) {
           ListItem(modifier = Modifier.padding(it), list = list)
        }
    }
}