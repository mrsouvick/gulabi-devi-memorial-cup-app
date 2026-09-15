package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Fixture
import com.example.data.model.Standing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Gulabi Devi Cup", appName)
    }

    @Test
    fun `fixture status checks`() {
        val liveFixture = Fixture(id = "1", home = "BBIT", away = "Brainware", status = "live")
        val completedFixture = Fixture(id = "2", home = "JIS", away = "Heritage", status = "completed")
        val scheduledFixture = Fixture(id = "3", home = "HIT", away = "Techno", status = "scheduled")

        assertTrue(liveFixture.isLive)
        assertTrue(completedFixture.isCompleted)
        assertTrue(scheduledFixture.isScheduled)
    }

    @Test
    fun `standing points calculation`() {
        val standing = Standing(
            team = "BBIT",
            played = 3,
            wins = 2,
            draws = 1,
            losses = 0,
            gf = 5,
            ga = 1
        )
        assertEquals(7, standing.totalPoints)
        assertEquals(4, standing.goalDifference)
    }
}
