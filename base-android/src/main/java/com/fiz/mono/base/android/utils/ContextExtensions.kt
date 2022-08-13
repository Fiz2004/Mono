package com.fiz.mono.base.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

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