package com.fiz.mono.core.ui.settings

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.fiz.mono.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class SettingsFragmentTest/* : BaseTest() */ {
//    @Before
//    fun setup() {
//        launchActivity<MainActivity>()
//    }

//    private val context = mock(Context::class.java)


//
//    /**
//     * Протестируйте компоненты представления состояния выбора
//     */
//    @Test
//    fun `test_initial_state`() {
//        testState(R.string.category, R.drawable.category)
//    }

    /**
     * Протестируйте запуск фрагмента настройки
     */
    @Test
    fun `test_settings_fragment`() {
        launchFragmentInContainer<SettingsFragment>(themeResId = R.style.Theme_Mono)

        onView(withId(R.id.titleTextView))
            .check(matches(withText(R.string.settings)))

        onView(withId(R.id.categoryTextView))
            .check(matches(withText(R.string.category)))

        onView(withId(R.id.currencyTextView))
            .check(matches(withText(R.string.currency)))

//        onView(withId(R.id.categoryIconImageView)).perform(click())
//
//        onView(withId(R.id.titleTextView))
//            .check(matches(withText(R.string.category_edit)))
    }
}
//
//    /**
//     * Проверьте, что функция выбора лимона переводит нас в "состояние сжатия".
//     */
//    @Test
//    fun `test_picking_lemon_proceeds_to_squeeze_state`() {
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        testState(R.string.lemon_squeeze, R.drawable.lemon_squeeze)
//    }
//
//    /**
//     * Проверьте, что функция сжатия переводит нас в "состояние напитка".
//     */
//    @Test
//    fun `test_squeezing_lemon_proceeds_to_drink_state`() {
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        juiceLemon()
//        testState(R.string.lemon_drink, R.drawable.lemon_drink)
//    }
//
//    /**
//     * * Закусочная с тестовым количеством сжатий
//     */
//    @Test
//    fun `test_squeeze_count_snackbar_is_displayed`() {
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(longClick())
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("Squeeze count: 1, keep squeezing!")))
//    }
//
//    /**
//     * Проверьте, что функциональность напитка переводит нас в "состояние перезапуска".
//     */
//    @Test
//    fun `test_drinking_juice_proceeds_to_restart_state`() {
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        juiceLemon()
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        // Click image to progress state
//        testState(R.string.lemon_empty_glass, R.drawable.lemon_restart)
//    }
//
//    /**
//     * Проверьте, что функция перезапуска возвращает нас в состояние "выбрать лимон".
//     */
//    @Test
//    fun `test_restarting_proceeds_to_pick_lemon_state`() {
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        juiceLemon()
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        // Click image to progress state
//        onView(withId(R.id.image_lemon_state)).perform(click())
//        testState(R.string.lemon_select, R.drawable.lemon_tree)
//    }
//}
//
///**
// * Приложение lemonade фактически является государственной машиной.
// * Чтобы избежать тестов с отслеживанием состояния (тестов, которые основаны на предыдущих тестах),
// * эти служебные методы перемещают состояния и уменьшают дублирование кода.
// * Этот класс предназначен для наследования методами @Test для использования этих методов.
// */
//open class BaseTest {
//
//    /**
//     * Проверьте, чтобы убедиться, что приложение находится в правильном состоянии.
//     * @* @param textActionResource Целое число для ожидаемого текстового ресурса.
//     * @param drawableResource Целое число для ожидаемого извлекаемого ресурса.
//     */
//    fun testState(textActionResource: Int, drawableResource: Int) {
//        onView(withId(R.id.categoryTextView))
//            .check(matches(ViewMatchers.withText(textActionResource)))
//        onView(withId(R.id.categoryIconImageView)).check(
//            matches(withDrawable(drawableResource))
//        )
//    }
//
//    /**
//     * Щелкает по изображению лимонного дерева.
//     */
//    fun pickLemon() {
//        onView(withDrawable(R.drawable.lemon_tree))
//            .perform(click())
//    }
//
//    /**
//     * Сжимайте лимон до тех пор, пока изображение лимона не исчезнет.
//     * Количество требуемых кликов определяется случайным числом, которое тест не
//     * осознаем, поэтому мы зацикливаемся и щелкаем до тех пор, пока изображение не изменится.
//     */
//    fun juiceLemon() {
//        while (onView(withDrawable(R.drawable.lemon_squeeze)).isPresent()) {
//            onView(withId(R.id.image_lemon_state)).perform(click())
//        }
//    }
//
//    /**
//     * Щелкните по изображению лимонада.
//     */
//    fun drinkJuice() {
//        onView(withDrawable(R.drawable.lemon_drink))
//            .perform(click())
//    }
//
//    /**
//     * Щелкните по изображению пустого стекла, чтобы перезапустить.
//     */
//    fun restart() {
//        onView(withDrawable(R.drawable.lemon_restart))
//            .perform(click())
//    }
//
//    /**
//     * Функция расширения для определения наличия элемента.
//     * Это используется для щелчка по изображению лимона до тех пор, пока оно не изменится, поскольку количество щелчков
//     * требуется определяется случайным числом, о котором тест не знает.
//     */
//    private fun ViewInteraction.isPresent(): Boolean {
//        return try {
//            check(matches(isDisplayed()))
//            true
//        } catch (e: NoMatchingViewException) {
//            false
//        }
//    }
//}
//
///**
// * Пользовательский сопоставитель для поиска чертежей.
// */
//object DrawableMatcher {
//
//    fun withDrawable(@DrawableRes resourceId: Int): Matcher<View> {
//        return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
//            override fun describeTo(description: Description?) {
//                description!!.appendText("has drawable resource $resourceId")
//            }
//
//            override fun matchesSafely(imageView: ImageView): Boolean {
//                return isSameBitmap(imageView, imageView.drawable, resourceId)
//            }
//        }
//    }
//
//    private fun isSameBitmap(item: View, drawable: Drawable?, expectedResId: Int): Boolean {
//        val image = item as ImageView
//        if (expectedResId < 0) {
//            return image.drawable == null
//        }
//        val expectedDrawable: Drawable? = ContextCompat.getDrawable(item.context, expectedResId)
//        if (drawable == null || expectedDrawable == null) {
//            return false
//        }
//        // Make tint consistent just in case they differ
//        val bitmap = getBitmap(drawable)
//        val expectedBitmap = getBitmap(expectedDrawable)
//        return bitmap.sameAs(expectedBitmap)
//    }
//
//    /**
//     * Преобразование векторного изображения в растровое изображение
//     * @* @параметр, доступный для рисования вектор, доступный для рисования
//     */
//    private fun getBitmap(drawable: Drawable): Bitmap {
//        val bitmap = Bitmap.createBitmap(
//            drawable.intrinsicWidth,
//            drawable.intrinsicHeight,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmap)
//        drawable.setBounds(0, 0, canvas.width, canvas.height)
//        drawable.draw(canvas)
//        return bitmap
//    }
//}