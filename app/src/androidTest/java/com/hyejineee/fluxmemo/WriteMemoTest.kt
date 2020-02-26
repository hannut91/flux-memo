package com.hyejineee.fluxmemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.hyejineee.fluxmemo.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class WriteMemoTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createMemoTest() {
        onView(withId(R.id.fab))
            .perform(click())

        val title = "Memo title"
        val content = "Memo content"

        onView(withId(R.id.memo_title_edit_text)).perform(typeText(title))
        onView(withId(R.id.memo_content_edit_text)).perform(typeText(content))

        onView(withId(R.id.back_button)).perform(click())

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()))
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(content)).check(matches(isDisplayed()))
    }

    @Test
    fun addImageTest() {
        onView(withId(R.id.fab))
            .perform(click())

        val title = "Memo title"
        val content = "Memo content"

        onView(withId(R.id.memo_title_edit_text)).perform(typeText(title))
        onView(withId(R.id.memo_content_edit_text)).perform(typeText(content))

        onView(withId(R.id.add_image_button)).perform(click())
        onView(withText("이미지 선택")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun addImageFromUrlTest() {
        onView(withId(R.id.fab))
            .perform(click())

        val title = "Memo title"
        val content = "Memo content"

        onView(withId(R.id.memo_title_edit_text)).perform(typeText(title))
        onView(withId(R.id.memo_content_edit_text)).perform(typeText(content))

        onView(withId(R.id.add_image_button)).perform(click())
        onView(withText("이미지 선택")).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText(activityRule.activity.getString(R.string.link))).perform(click())
        onView(withId(R.id.enter_url_dialog)).check(matches(isDisplayed()))

        onView(withId(R.id.url_edit_text)).perform(typeText("https://images.unsplash.com/photo-1582148453014-b7d4117d3300?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2250&q=80"))
    }
}
