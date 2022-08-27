package com.fiz.mono.base.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(themeAttrId))
    val color = typedArray.getColor(0, Color.MAGENTA)
    typedArray.recycle()
    return color
}

@ColorInt
@Suppress("DEPRECATION")
fun Context.getColorCompat(
    @ColorRes colorRes: Int
): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.resources.getColor(colorRes, this.theme)
    } else {
        this.resources.getColor(colorRes)
    }
}

fun Context.showKeyboard(view: View) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
        view,
        InputMethodManager.SHOW_IMPLICIT
    )
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(@StringRes messageId: Int, vararg params: String) {
    Toast.makeText(this, getString(messageId, *params), Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String) {
    context?.showToast(message)
}

fun Fragment.showToast(@StringRes messageId: Int, vararg params: String) {
    context?.showToast(messageId, *params)
}