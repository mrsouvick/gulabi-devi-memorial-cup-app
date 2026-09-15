package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Fixture
import com.example.ui.components.MatchCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        val testFixture = Fixture(
            id = "f1",
            home = "BBIT Kolkata",
            away = "Brainware University",
            homeScore = 2,
            awayScore = 1,
            stage = "Grand Final",
            status = "live",
            minute = "78'",
            venue = "BBIT Main Stadium",
            date = "28 Sep",
            time = "16:00"
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                MatchCard(
                    fixture = testFixture,
                    onClick = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
