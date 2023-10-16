package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.BannerAdView
import dev.fztech.app.info.ui.component.ExpandableSearchView
import dev.fztech.app.info.ui.component.ExtraSmallSpacer
import dev.fztech.app.info.ui.component.ShimmerListItem
import dev.fztech.app.info.ui.component.decoration.ShimmerBrush
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppInfoViewModel, onNavigateToDetail: (PackageInfo) -> Unit) {

    val context = LocalContext.current
    val list by viewModel.items.observeAsState(emptyList())
    val loading by viewModel.loading
    val query by viewModel.query.observeAsState(initial = "")

    LaunchedEffect(key1 = viewModel) {
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
                hint = stringResource(id = R.string.search),
                onQueryChanged = {
                    viewModel.setQuery(it)
                },
                onSearchQuery = {
                    viewModel.setQuery(it)
                },
                onSearchDisplayClosed = {
                    viewModel.setQuery("")
                },
                backgroundEnabled = false
            )
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        )  {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = Dimens.XSmall
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(Dimens.XSmall),
                    ) {
                        items(Category.values()) { item ->
                            if (loading) {
                                Spacer(modifier = Modifier.width(Dimens.Small))
                                Spacer(modifier = Modifier
                                    .padding(bottom = Dimens.Small)
                                    .width(70.dp)
                                    .height(Dimens.XXLarge)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(ShimmerBrush())
                                )
                                Spacer(modifier = Modifier.width(Dimens.Small))
                            } else {
                                FilterChip(
                                    modifier = Modifier
                                        .padding(horizontal = Dimens.Small), // gap between items
                                    selected = (item == viewModel.category.value),
                                    onClick = {
                                        viewModel.changeCategory(item)
                                    },
                                    label = {
                                        Text(text = item.value)
                                    },
                                    trailingIcon = {
                                        Text(text = "(${viewModel.getSize(item)})")
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        labelColor = MaterialTheme.colorScheme.onPrimary,
                                        iconColor = Color.Yellow.copy(0.7f),
                                        selectedLabelColor = Color.Yellow,
                                        selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                                        selectedTrailingIconColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = Color.LightGray
                                    )
                                )
                            }
                        }
                    }
                }

                if (loading) {
                    Column {
                        repeat(15) {
                            ShimmerListItem(brush = ShimmerBrush())
                        }
                    }
                } else {
                    ListItem(modifier = Modifier.fillMaxWidth(), list = list) { packageInfo ->
                        onNavigateToDetail(packageInfo)
                    }
                }
            }

            BannerAdView(Modifier.align(Alignment.BottomCenter), adID = stringResource(id = R.string.banner_ad_id))
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