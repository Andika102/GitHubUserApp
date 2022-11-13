package com.dicoding.githubuser.ui

import android.view.KeyEvent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.dicoding.githubuser.R
import com.dicoding.githubuser.utils.Constants.Companion.MILLISECONDS
import com.dicoding.githubuser.utils.Constants.Companion.UI_TEST_QUERY
import com.dicoding.githubuser.utils.Constants.Companion.WAIT
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Before
    fun init() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun uiTest() {

        onView(isRoot()).perform(waitFor(3000))

        onView(withId(R.id.customSearch))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())

        onView(withId(androidx.appcompat.R.id.search_src_text))
            .check(matches(isDisplayed()))
            .perform(
                ViewActions.typeText(UI_TEST_QUERY),
                pressKey(KeyEvent.KEYCODE_ENTER)
            )

        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.searchRecyclerView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "$WAIT $delay $MILLISECONDS"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}