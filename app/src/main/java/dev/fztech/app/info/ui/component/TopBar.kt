package dev.fztech.app.info.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.theme.AppInfoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    searchVisible: Boolean = false,
    hasNavigationIcon: Boolean = false,
    titleComposable: @Composable () -> Unit = { BarTitle(title) },
    actionButton: @Composable () -> Unit = { },
    onSearchClick: () -> Unit = {},
    onBack: ()-> Unit = {}
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            titleComposable.invoke()
        },
        navigationIcon = {
            if (hasNavigationIcon) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        actions = {
            actionButton.invoke()

            if (searchVisible) {
                IconButton(onClick = { onSearchClick.invoke() }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            }
        })
}

@Composable
fun BarTitle(title: String) {
    Text(
        text = title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun BarTitle2Line(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTopBar() {
    AppInfoTheme {
        TopBar("About", true)
    }
}