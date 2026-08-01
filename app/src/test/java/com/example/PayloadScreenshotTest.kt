package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.calculators.PayloadSafetyScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class) @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(sdk=[36])
class PayloadScreenshotTest { @get:Rule val rule=createComposeRule(); @Test fun payloadScreenRenders(){rule.setContent{MyApplicationTheme{PayloadSafetyScreen()}};rule.onRoot().captureRoboImage(filePath="src/test/screenshots/payload.png")} }
