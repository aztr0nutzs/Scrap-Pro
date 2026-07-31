package com.example.ui.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Compatibility wrapper. Banner artwork contains its own title; no overlay is rendered. */
@Composable
fun HeaderBanner(
    @DrawableRes imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    ScreenHeaderBanner(
        imageResId = imageRes,
        contentDescription = title,
        modifier = modifier
    )
}
