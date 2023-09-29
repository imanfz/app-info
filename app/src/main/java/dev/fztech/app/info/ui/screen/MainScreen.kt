package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.BannerAdView
import dev.fztech.app.info.ui.component.ExpandableSearchView
import dev.fztech.app.info.ui.component.ExtraSmallSpacer
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToDetail: (PackageInfo) -> Unit) {

    val context = LocalContext.current
    val viewModel = viewModel<AppInfoViewModel>()
    val chipState = rememberLazyListState()
    val list by viewModel.items.observeAsState(emptyList())
    val query by viewModel.query.observeAsState(initial = "")

    LaunchedEffect(viewModel) {
        launch {
            viewModel.apply {
                loadPackage(context)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ExpandableSearchView(
                title = stringResource(id = R.string.app_name),
                query = query,
                hint = "Search",
                onQueryChanged = {
                    viewModel.setQuery(it)
                    Log.d("TAG", "MainScreen: onSearchDisplayChanged ${viewModel.query.value}")
                },
                onSearchQuery = {
                    viewModel.setQuery(it)
                },
                onSearchDisplayClosed = {
                    viewModel.setQuery("")
                    Log.d("TAG", "MainScreen: onSearchDisplayClosed ${viewModel.query.value}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                backgroundEnabled = false
            )
        }
    ) {
        ConstraintLayout(Modifier.fillMaxSize().padding(it))  {
            val (chip, content, ads) = createRefs()

            Surface(
                modifier = Modifier.constrainAs(chip) {
                    top.linkTo(parent.top)
                    width = Dimension.matchParent
                },
                shadowElevation = Dimens.XSmall
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.XSmall),
                    state = chipState,
                ) {
                    items(Category.values()) { item ->
                        FilterChip(
                            modifier = Modifier.padding(horizontal = Dimens.Small), // gap between items
                            selected = (item == viewModel.category.value),
                            onClick = {
                                viewModel.changeCategory(item)
                            },
                            label = {
                                Row {
                                    Text(text = item.value)
                                    ExtraSmallSpacer()
                                    Text(
                                        text = "(${viewModel.getSize(item)})",
                                        color = MaterialTheme.colorScheme.surfaceTint
                                    )
                                }
                            }
                        )
                    }
                }
            }
            ListItem(modifier = Modifier.constrainAs(content) {
                top.linkTo(chip.bottom)
                bottom.linkTo(ads.top, Dimens.Small)
                height = Dimension.fillToConstraints
            }, list = list) { packageInfo ->
                onNavigateToDetail(packageInfo)
            }
            BannerAdView(Modifier.constrainAs(ads) {
                bottom.linkTo(parent.bottom)
            })
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
                    query = query,
                    onQueryChanged = {
                        query = it
                    },
                    onSearchQuery = {
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