package com.fiz.mono.base.android.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.TextView

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

fun View.setVisible(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

var EditText.textString: String
    get() {
        return this.text.toString()
    }
    set(value) {
        if (this.text.toString() != value)
            this.setText(value)
    }