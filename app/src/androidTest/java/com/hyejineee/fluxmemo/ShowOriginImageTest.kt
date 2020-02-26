package com.hyejineee.fluxmemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.hyejineee.fluxmemo.view.WriteMemoActivity
import com.hyejineee.fluxmemo.view.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_write_memo.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ShowOriginImageTest {

    @get:Rule
    val activityRule = ActivityTestRule(WriteMemoActivity::class.java)

    @Before
    fun setUp() {
        activityRule.runOnUiThread {
            (activityRule.activity.image_list.adapter as ImageAdapter).appendImage(
                "https://images.unsplash.com/photo-1582313731133-8017a11f193c?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2250&q=80"
            )
        }
    }

    @Test
    fun showOriginImageTest() {
        onView(withId(R.id.image_list)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<ImageAdapter.ViewHolder>(0, ViewActions.click())
        )
        Thread.sleep(500)
        onView(withId(R.id.origin_image_dialog)).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun closeDialogTest() {
        onView(withId(R.id.image_list)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<ImageAdapter.ViewHolder>(0, ViewActions.click())
        )

        Thread.sleep(500)
        onView(withId(R.id.origin_image_dialog)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withId(R.id.close_button)).perform(click())
        onView(withId(R.id.write_memo_activity)).check(matches(isDisplayed()))
    }
}
