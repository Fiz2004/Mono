package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import com.fiz.mono.util.setDisabled
import com.fiz.mono.util.setEnabled
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CategoryInputViewModel : ViewModel() {
    private var allCategoryExpense = CategoryStore.getAllCategoryExpenseForInput()
    private var allCategoryIncome = CategoryStore.getAllCategoryIncomeForInput()

    private var selectedAdapter: Int = InputFragment.EXPENSE

    var state = ""

    lateinit var currentPhotoPath: String

    val photo: MutableList<Bitmap?> = mutableListOf()

    fun setSelectedAdapter(adapter: Int) {
        selectedAdapter = adapter
        if (adapter == InputFragment.EXPENSE)
            allCategoryIncome.forEach { it.selected = false }
        else
            allCategoryExpense.forEach { it.selected = false }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (selectedAdapter) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            InputFragment.INCOME -> context.getString(R.string.income)
            else -> {
                throw Error("Not adapter")
            }
        }
    }

    fun clickSubmit(absValue: Double, note: String) {
        val selectedCategoryItem = getAllCategoryFromSelected().first { it.selected }

        val value = if (selectedAdapter == InputFragment.EXPENSE)
            -absValue
        else
            absValue

        TransactionStore.insertNewTransaction(
            TransactionItem(
                Calendar.getInstance().time,
                value,
                selectedCategoryItem.name,
                note,
                selectedCategoryItem.imgSrc,
                photo
            )
        )
        (if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense
        else
            allCategoryIncome).first { it.selected }.selected = false

        state = "Только что отправили"
    }

    fun isClickEditPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.size - 1
    }

    fun isClickEditPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.size - 1
    }

    fun cleanSelected() {
        allCategoryExpense.forEach { it.selected = false }
        allCategoryIncome.forEach { it.selected = false }
    }

    fun setStateSubmitInputButton(submitButton: Button) {
        if (getAllCategoryFromSelected().firstOrNull { it.selected } == null)
            submitButton.setDisabled()
        else
            submitButton.setEnabled()
    }

    fun addSelectItem(position: Int) {
        if (getSelectedAdapter() == InputFragment.EXPENSE)
            addSelectItemExpense(position)
        else
            addSelectItemIncome(position)
    }


    private fun addSelectItemExpense(position: Int) {
        if (!allCategoryExpense[position].selected) {
            allCategoryExpense.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpense[position].selected = !allCategoryExpense[position].selected
    }

    private fun addSelectItemIncome(position: Int) {
        if (!allCategoryIncome[position].selected) {
            allCategoryIncome.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncome[position].selected = !allCategoryIncome[position].selected
    }

    fun getAllCategoryFromSelected(): List<CategoryItem> {
        return if (getSelectedAdapter() == InputFragment.EXPENSE)
            getAllCategoryItemExpense()
        else
            getAllCategoryItemIncome()
    }

    fun getSelectedAdapter(): Int {
        return selectedAdapter
    }

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.map { it.copy() }
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.map { it.copy() }
    }

    fun dispatchTakePictureIntent(context: Context): Intent? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    Toast.makeText(context, "I can't create a file", Toast.LENGTH_LONG).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "com.fiz.mono.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    return takePictureIntent
                }
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun setPic(targetW: Int, targetH: Int): Bitmap? {
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            return bitmap
        }
        return null
    }

}