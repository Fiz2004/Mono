package com.fiz.mono.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity

class ActivityContract : ActivityResultContract<Intent, Int?>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int? = when {
        resultCode != AppCompatActivity.RESULT_OK -> 0
        else -> 1
    }
}