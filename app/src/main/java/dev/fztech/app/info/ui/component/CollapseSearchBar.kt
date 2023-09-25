package dev.fztech.app.info.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.ui.theme.Shapes

@Composable
fun ExpandableSearchView(
    modifier: Modifier = Modifier,
    searchDisplay: String,
    onSearchDisplayChanged: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    title: String = "",
    hint: String = "",
    backgroundEnabled: Boolean = false,
    expandedInitially: Boolean = false,
    hintTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconTint: Color = MaterialTheme.colorScheme.outline,
    buttonTint: Color = MaterialTheme.colorScheme.onPrimary,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val (expanded, onExpandedChanged) = remember {
        mutableStateOf(expandedInitially)
    }

    if (backgroundEnabled) {
        Crossfade(targetState = expanded, label = "SearchBar") { isSearchFieldVisible ->
            when (isSearchFieldVisible) {
                true -> ExpandedSearchView(
                    searchDisplay = searchDisplay,
                    onSearchDisplayChanged = onSearchDisplayChanged,
                    onSearchDisplayClosed = onSearchDisplayClosed,
                    onExpandedChanged = onExpandedChanged,
                    modifier = modifier,
                    backgroundEnabled = true,
                    hint = hint,
                    hintTextColor = hintTextColor,
                    iconTint = iconTint,
                    buttonTint = buttonTint,
                    keyboardType
                )

                false -> CollapsedSearchView(
                    title = title,
                    onExpandedChanged = onExpandedChanged,
                    modifier = modifier,
                    buttonTint = buttonTint
                )
            }
        }
    } else {
        when (expanded) {
            true -> ExpandedSearchView(
                searchDisplay = searchDisplay,
                onSearchDisplayChanged = onSearchDisplayChanged,
                onSearchDisplayClosed = onSearchDisplayClosed,
                onExpandedChanged = onExpandedChanged,
                modifier = modifier,
                backgroundEnabled = false,
                hint = hint,
                hintTextColor = hintTextColor,
                iconTint = iconTint,
                buttonTint = buttonTint,
                keyboardType = keyboardType
            )

            false -> CollapsedSearchView(
                title = title,
                onExpandedChanged = onExpandedChanged,
                modifier = modifier,
                buttonTint = buttonTint
            )
        }
    }

}

@Composable
fun SearchIcon(iconTint: Color) {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "search icon",
        tint = iconTint
    )
}

@Composable
fun CloseIcon(iconTint: Color) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "close icon",
        tint = iconTint
    )
}

@Composable
fun BackIcon(iconTint: Color) {
    Icon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = "close icon",
        tint = iconTint
    )
}

@Composable
fun CollapsedSearchView(
    title: String,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    buttonTint: Color
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp),
            color = buttonTint
        )
        IconButton(onClick = { onExpandedChanged(true) }) {
            SearchIcon(iconTint = buttonTint)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSearchView(
    searchDisplay: String,
    onSearchDisplayChanged: (String) -> Unit,
    onSearchDisplayClosed: () -> Unit,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundEnabled: Boolean,
    hint: String,
    hintTextColor: Color,
    iconTint: Color,
    buttonTint: Color,
    keyboardType: KeyboardType
) {
    val focusManager = LocalFocusManager.current

    val interactionSource = remember { MutableInteractionSource() }

    val textFieldFocusRequester = remember { FocusRequester() }

    SideEffect {
        textFieldFocusRequester.requestFocus()
    }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(searchDisplay, TextRange(searchDisplay.length)))
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides LocalTextSelectionColors.current
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                textFieldValue = TextFieldValue("")
                onExpandedChanged(false)
                onSearchDisplayClosed()
            }) {
                BackIcon(iconTint = buttonTint)
            }
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onSearchDisplayChanged(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = Dimens.Default)
                    .heightIn(min = 32.dp, max = 48.dp)
                    .focusRequester(textFieldFocusRequester)
                    .clip(Shapes.small)
                ,
                cursorBrush = SolidColor(
                    if (backgroundEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onPrimary
                ),
                textStyle = TextStyle.Default.copy(color =
                    if (backgroundEnabled)  MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    // places text field with placeholder and appropriate bottom padding
                    if (backgroundEnabled) {
                        TextFieldDefaults.DecorationBox(
                            value = searchDisplay,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            isError = false,
                            leadingIcon = {
                                SearchIcon(iconTint = iconTint)
                            },
                            trailingIcon = {
                                if (textFieldValue.text.isNotEmpty()) {
                                    IconButton(onClick = {
                                        textFieldValue = TextFieldValue()
                                        onSearchDisplayChanged("")
                                    }) {
                                        CloseIcon(iconTint = iconTint)
                                    }
                                }
                            },
                            placeholder = {
                                if (textFieldValue.text.isEmpty()) {
                                    Text(text = hint, color = hintTextColor)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                            ),
                            contentPadding = PaddingValues(bottom = 3.dp),
                        )
                    } else {
                        TextFieldDefaults.DecorationBox(
                            value = searchDisplay,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            isError = false,
                            trailingIcon = {
                                if (textFieldValue.text.isNotEmpty()) {
                                    IconButton(onClick = {
                                        textFieldValue = TextFieldValue()
                                        onSearchDisplayChanged("")
                                    }) {
                                        CloseIcon(iconTint = buttonTint)
                                    }
                                }
                            },
                            placeholder = {
                                if (textFieldValue.text.isEmpty()) {
                                    Text(text = hint, color = hintTextColor)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Unspecified,
                                unfocusedContainerColor = Color.Unspecified,
                                disabledContainerColor = Color.Unspecified,
                            ),
                            contentPadding = PaddingValues(bottom = 3.dp),
                        )
                    }
                }
            )
        }
    }
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
                searchDisplay = "",
                onSearchDisplayChanged = {},
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
                searchDisplay = "",
                onSearchDisplayChanged = {},
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
                searchDisplay = "",
                onSearchDisplayChanged = {},
                expandedInitially = true,
                onSearchDisplayClosed = {},
                backgroundEnabled = true,
                hint = "Search"
            )
        }
    }
}