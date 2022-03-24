package com.fiz.mono.util

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.fiz.mono.R

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

fun Button.setDisabled() {
    this.backgroundTintList =
        context?.themeColor(R.attr.colorGray)?.let {
            ColorStateList.valueOf(
                it
            )
        }
    context?.themeColor(com.google.android.material.R.attr.colorSecondary)
        ?.let { this.setTextColor(it) }
}

fun Button.setEnabled() {
    this.backgroundTintList =
        context?.getColorCompat(R.color.blue)?.let {
            ColorStateList.valueOf(
                it
            )
        }
    context?.themeColor(R.attr.colorMain)
        ?.let {
            this.setTextColor(it)
        }
}

fun Button.setRemove() {
    this.backgroundTintList =
        context?.getColorCompat(R.color.red)?.let {
            ColorStateList.valueOf(
                it
            )
        }
    context?.themeColor(R.attr.colorMain)
        ?.let {
            this.setTextColor(it)
        }
}

fun View.setVisible(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}