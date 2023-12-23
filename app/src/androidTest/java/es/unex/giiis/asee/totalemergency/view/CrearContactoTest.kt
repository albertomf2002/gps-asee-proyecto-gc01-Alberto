package es.unex.giiis.asee.totalemergency.view


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import es.unex.giiis.asee.totalemergency.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CrearContactoTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.CALL_PHONE"
        )

    @Test
    fun crearContactoTest() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.et_username),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Carlos"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.et_password),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("1234"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.bt_login), withText("login"),
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

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.contactsFragment), withContentDescription("Contactos"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottom_navigation),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.insertarTelefono),
                childAtPosition(
                    allOf(
                        withId(R.id.bodyLayout),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("123321"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.nombreContacto),
                childAtPosition(
                    allOf(
                        withId(R.id.bodyLayout),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("Miguel"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.botonInsertar), withText("añadir"),
                childAtPosition(
                    allOf(
                        withId(R.id.bodyLayout),
                        childAtPosition(
                            withId(R.id.nav_host_fragment),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.labelNumber), withText("Miguel"),
                withParent(
                    allOf(
                        withId(R.id.contactoGroup),
                        withParent(withId(R.id.Cuerpo))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Miguel")))

        val textView2 = onView(
            allOf(
                withId(R.id.telefonoShow), withText("123321"),
                withParent(
                    allOf(
                        withId(R.id.contactoGroup),
                        withParent(withId(R.id.Cuerpo))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("123321")))

        val textView3 = onView(
            allOf(
                withId(R.id.telefonoShow), withText("123321"),
                withParent(
                    allOf(
                        withId(R.id.contactoGroup),
                        withParent(withId(R.id.Cuerpo))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("123321")))
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
