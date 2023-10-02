package dev.fztech.app.info.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.theme.AppInfoTheme

@Composable
fun ExpandableSearchView(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchQuery: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    title: String = "",
    hint: String = "",
    backgroundEnabled: Boolean = false,
    expandedInitially: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val (expanded, onExpandedChanged) = rememberSaveable {
        mutableStateOf(expandedInitially)
    }

    if (backgroundEnabled) {
        Crossfade(targetState = expanded, label = "SearchBar") { isSearchFieldVisible ->
            when (isSearchFieldVisible) {
                true -> ExpandedSearchView(
                    query = query,
                    onQueryChanged = onQueryChanged,
                    onSearchQuery = onSearchQuery,
                    onSearchDisplayClosed = onSearchDisplayClosed,
                    onExpandedChanged = onExpandedChanged,
                    backgroundEnabled = true,
                    hint = hint,
                    keyboardType
                )

                false -> CollapsedSearchView(
                    title = title,
                    onExpandedChanged = onExpandedChanged
                )
            }
        }
    } else {
        when (expanded) {
            true -> ExpandedSearchView(
                query = query,
                onQueryChanged = onQueryChanged,
                onSearchQuery = onSearchQuery,
                onSearchDisplayClosed = onSearchDisplayClosed,
                onExpandedChanged = onExpandedChanged,
                backgroundEnabled = false,
                hint = hint,
                keyboardType = keyboardType
            )

            false -> CollapsedSearchView(
                title = title,
                onExpandedChanged = onExpandedChanged
            )
        }
    }

}

@Composable
fun CollapsedSearchView(
    title: String,
    onExpandedChanged: (Boolean) -> Unit,
) {

    TopBar(
        title = title,
        searchVisible = true,
        onSearchClick = {
            onExpandedChanged(true)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ExpandedSearchView(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchQuery: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    backgroundEnabled: Boolean,
    hint: String,
    keyboardType: KeyboardType,
    interactionSource: MutableInteractionSource =  remember { MutableInteractionSource() }
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    SideEffect {
        focusRequester.requestFocus()
    }

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
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .clip(MaterialTheme.shapes.small)
                ,
                cursorBrush = SolidColor(
                    if (backgroundEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onPrimary
                ),
                textStyle = TextStyle(color =
                if (backgroundEnabled)  MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchQuery(query)
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    // places text field with placeholder and appropriate bottom padding
                    if (backgroundEnabled) {
                        TextFieldDefaults.DecorationBox(
                            value = query,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = stringResource(id = R.string.search)
                                )
                            },
                            trailingIcon = {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChanged("") }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = stringResource(id = R.string.clear)
                                        )
                                    }
                                }
                            },
                            placeholder = {
                                if (query.isEmpty()) {
                                    Text(text = hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.secondary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.tertiary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            contentPadding = PaddingValues(bottom = 3.dp),
                        )
                    } else {
                        TextFieldDefaults.DecorationBox(
                            value = query,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            placeholder = {
                                if (query.isEmpty()) {
                                    Text(text = hint, color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                focusedContainerColor = MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.tertiary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            contentPadding = PaddingValues(bottom = 3.dp),
                        )
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onExpandedChanged(false)
                onSearchDisplayClosed()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            if (query.isNotEmpty() && !backgroundEnabled) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.clear)
                    )
                }
            }
        })
}

@Preview
@Composable
fun CollapsedSearchViewPreview() {
    AppInfoTheme {
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            ExpandableSearchView(
                title = stringResource(id = R.string.app_name),
                query = "",
                onQueryChanged = {},
                onSearchQuery = {},
                onSearchDisplayClosed = {}
            )
        }
    }
}

@Preview
@Composable
fun ExpandedSearchViewPreview() {
    AppInfoTheme {
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            ExpandableSearchView(
                query = "",
                onQueryChanged = {},
                onSearchQuery = {},
                expandedInitially = true,
                onSearchDisplayClosed = {},
                hint = "Search"
            )
        }
    }
}


@Preview
@Composable
fun ExpandedWithBGPreview() {
    AppInfoTheme {
        Surface(
            color = MaterialTheme.colorScheme.primary
        ) {
            ExpandableSearchView(
                query = "",
                onQueryChanged = {},
                onSearchQuery = {},
                expandedInitially = true,
                onSearchDisplayClosed = {},
                backgroundEnabled = true,
                hint = "Search"
            )
        }
    }
}