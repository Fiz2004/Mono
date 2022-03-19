package com.fiz.mono.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity

class ActivityContract : ActivityResultContract<Intent, Intent?>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = when {
        resultCode != AppCompatActivity.RESULT_OK -> null
        else -> intent
    }
}