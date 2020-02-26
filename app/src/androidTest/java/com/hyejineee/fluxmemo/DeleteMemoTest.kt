package com.hyejineee.fluxmemo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.hyejineee.fluxmemo.model.Memo
import com.hyejineee.fluxmemo.room.MemoAppDatabase
import com.hyejineee.fluxmemo.view.MainActivity
import com.hyejineee.fluxmemo.view.adapter.MemoAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeleteMemoTest : KoinTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)
    private val db: MemoAppDatabase by inject()

    @Before
    fun setup() {
        db.memoDao().insert(
            Memo(
                title = "Memo title",
                content = "Memo content"
            )
        )
    }

    @Test
    fun deleteMemoTest() {
        onView(withText("Memo title")).check(matches(isDisplayed()))
        onView(withId(R.id.memo_list)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<MemoAdapter.ViewHolder>(0, click())
        )

        onView(withId(R.id.delete_button)).perform(click())
        onView(withText("주의")).check(matches(isDisplayed()))
        onView(withText("예")).inRoot(isDialog()).perform(click())

        onView(withId(R.id.main_activity)).check(matches(isDisplayed()))
        onView(withText("Memo title")).check(doesNotExist())
    }
}
