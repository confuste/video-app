package com.alexfuster.videoapp

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexfuster.videoapp.ui.main.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITest {



    @Test
    fun onRecordingButtonAndViewGalleryButtonPerformAction() {

        // 1
        ActivityScenario.launch(MainActivity::class.java)
        // 2
        onView(withId(R.id.video_capture_button))
            .perform(click())

        SystemClock.sleep(3000)

        //3
        onView(withId(R.id.video_capture_button))
            .perform(click())

        // 4
        onView(withId(R.id.button_open_gallery))
            .perform(click())

        //5
        onView(withId(R.id.rv_video_list))
            .check(matches(isDisplayed()))
    }

}