package com.example.pharmacy.ui.activities


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.pharmacy.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LaunchScreenActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LaunchScreenActivity::class.java)

    @Test
    fun launchScreenActivityTest() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.et_email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        Thread.sleep(5000);
        appCompatEditText.perform(replaceText("brown@gmail.com"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.et_password),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_password),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("Test1234"), closeSoftKeyboard())
        Thread.sleep(2000);
        val materialButton = onView(
            allOf(
                withId(R.id.btn_login), withText("Войти"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())
        Thread.sleep(5000);
        val recyclerView = onView(
            allOf(
                withId(R.id.rv_dashboard_items),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        Thread.sleep(2000);
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))
        Thread.sleep(2000);
        val materialButton2 = onView(
            allOf(
                withId(R.id.btn_go_to_cart), withText("К корзине"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        materialButton2.perform(scrollTo(), click())
        Thread.sleep(2000);
        val materialButton3 = onView(
            allOf(
                withId(R.id.btn_checkout), withText("Подтвердить"),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_checkout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())
        Thread.sleep(2000);
        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.et_address),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_address),
                        0
                    ),
                    0
                )
            )
        )

        appCompatEditText3.perform(scrollTo(), replaceText("Kazan"), closeSoftKeyboard())
        Thread.sleep(2000);
        val materialButton4 = onView(
            allOf(
                withId(R.id.btn_place_order), withText("Заказать"),
                childAtPosition(
                    allOf(
                        withId(R.id.ll_checkout_place_order),
                        childAtPosition(
                            withClassName(`is`("android.widget.RelativeLayout")),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())
        Thread.sleep(2000);
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_orders), withContentDescription("Заказы"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
