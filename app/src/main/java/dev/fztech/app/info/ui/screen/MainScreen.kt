package dev.fztech.app.info.ui.screen

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.ExpandableSearchView
import dev.fztech.app.info.ui.component.ExtraSmallSpacer
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToDetail: (PackageInfo) -> Unit) {
    val packageManager = LocalContext.current.packageManager

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(Category.ALL) }

    var allApps by rememberSaveable {
        mutableStateOf(listOf<PackageInfo>())
    }
    val systemApps =  allApps.filter { it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0 }
    val userApps = allApps.filter { it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) <= 0 }

    val filteredList = when(selectedCategory) {
        Category.SYSTEM -> systemApps
        Category.USER -> userApps
        else -> allApps
    }.filter {
        it.applicationInfo.loadLabel(packageManager).toString().contains(
            query,
            true
        )
    }

    val chipState = rememberLazyListState()

    LaunchedEffect(key1 = "main") {
        launch {
            allApps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ExpandableSearchView(
                title = stringResource(id = R.string.app_name),
                searchDisplay = query,
                hint = "Search",
                onSearchDisplayChanged = {
                    query = it
                },
                onSearchDisplayClosed = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                backgroundEnabled = false
            )
        },
    ) {
        Column(Modifier.padding(it))  {
            Surface(shadowElevation = Dimens.XSmall) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.XSmall),
                    state = chipState,
                ) {
                    items(Category.values()) { item ->
                        FilterChip(
                            modifier = Modifier.padding(horizontal = Dimens.Small), // gap between items
                            selected = (item == selectedCategory),
                            onClick = {
                                selectedCategory = item
                            },
                            label = {
                                Row {
                                    Text(text = item.value)
                                    ExtraSmallSpacer()
                                    Text(
                                        text = "(${
                                            when (item) {
                                                Category.SYSTEM -> systemApps
                                                Category.USER -> userApps
                                                else -> allApps
                                            }.size
                                        })",
                                        color = MaterialTheme.colorScheme.surfaceTint
                                    )
                                }
                            }
                        )
                    }
                }
            }
            ListItem(modifier = Modifier, list = filteredList) { packageInfo ->
                onNavigateToDetail(packageInfo)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreenPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ALL) }

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
                    backgroundEnabled = false
                )
            },
        ) {
            Column(Modifier.padding(it)) {
                Surface(shadowElevation = Dimens.XSmall) {
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(Category.values()) { item ->
                            FilterChip(
                                modifier = Modifier.padding(horizontal = Dimens.Small), // gap between items
                                selected = (item == selectedCategory),
                                onClick = {
                                    selectedCategory = item
                                },
                                label = {
                                    Row {
                                        Text(text = item.value)
                                        ExtraSmallSpacer()
                                        Text(
                                            text = "(${
                                                when (item) {
                                                    Category.SYSTEM -> 219
                                                    Category.USER -> 213
                                                    else -> 6
                                                }
                                            })",
                                            color = MaterialTheme.colorScheme.surfaceTint
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(20) {
                        ItemPreview(text)
                        Divider()
                    }
                }
            }
        }
    }
}