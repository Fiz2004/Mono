package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CategoryInputViewModel : ViewModel() {
    var edit: Boolean = false
    var note: String = ""
    var value: Double = 0.0
    private var _allCategoryExpense: MutableLiveData<MutableList<CategoryItem>> =
        MutableLiveData(mutableListOf())
    var allCategoryExpense: LiveData<MutableList<CategoryItem>> = _allCategoryExpense

    private var _allCategoryIncome: MutableLiveData<MutableList<CategoryItem>> =
        MutableLiveData(mutableListOf())
    var allCategoryIncome: LiveData<MutableList<CategoryItem>> = _allCategoryIncome

    init {
        viewModelScope.launch {
            CategoryStore.init {
                _allCategoryExpense.value = CategoryStore.getAllCategoryExpenseForInput()
                _allCategoryIncome.value = CategoryStore.getAllCategoryIncomeForInput()
            }
        }
    }

    private var selectedAdapter: Int = InputFragment.EXPENSE

    lateinit var currentPhotoPath: String

    val photoPath: MutableList<String?> = mutableListOf()

    fun setSelectedAdapter(adapter: Int) {
        selectedAdapter = adapter
        if (adapter == InputFragment.EXPENSE)
            allCategoryIncome.value?.forEach { it.selected = false }
        else
            allCategoryExpense.value?.forEach { it.selected = false }
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

    fun setValue(text: CharSequence?) {
        val value1 = text.toString()
        value = if (value1.isNotBlank())
            value1.toDouble()
        else
            0.0
    }

    fun clickSubmit() {
        val selectedCategoryItem = getAllCategoryFromSelected().first { it.selected }

        val value1 = if (selectedAdapter == InputFragment.EXPENSE)
            -value
        else
            value


        CoroutineScope(Dispatchers.IO).launch {
            TransactionStore.init {

                val lastItem = TransactionStore.getAllTransactions().lastOrNull()
                val id = lastItem?.id
                val newId = id?.let { it + 1 } ?: 0

                TransactionStore.insertNewTransaction(
                    TransactionItem(
                        newId,
                        Calendar.getInstance().time,
                        value1,
                        selectedCategoryItem.name,
                        note,
                        selectedCategoryItem.imgSrc,
                        photoPath
                    )
                )

                value = 0.0
                note = ""

            }
        }

        (if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense
        else
            allCategoryIncome).value?.first { it.selected }?.selected = false
    }

    fun isClickEditPositionExpense(position: Int): Boolean {
        return position == allCategoryExpense.value?.size?.minus(1) ?: throw Error("No allCategoryExpense")
    }

    fun isClickEditPositionIncome(position: Int): Boolean {
        return position == allCategoryIncome.value?.size?.minus(1) ?: throw Error("No allCategoryIncome")
    }

    fun cleanSelected() {
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
    }

    fun addSelectItem(position: Int) {
        if (getSelectedAdapter() == InputFragment.EXPENSE)
            addSelectItemExpense(position)
        else
            addSelectItemIncome(position)
    }


    private fun addSelectItemExpense(position: Int) {
        if (!allCategoryExpense.value?.get(position)?.selected!!) {
            allCategoryExpense.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpense.value?.get(position)!!.selected =
            !allCategoryExpense.value?.get(position)!!.selected
    }

    private fun addSelectItemIncome(position: Int) {
        if (!allCategoryIncome.value?.get(position)?.selected!!) {
            allCategoryIncome.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncome.value?.get(position)?.selected =
            !allCategoryIncome.value?.get(position)?.selected!!
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

    fun isSelected(): Boolean {
        return getAllCategoryFromSelected().firstOrNull { it.selected } != null
    }

    fun getAllCategoryItemExpense(): List<CategoryItem> {
        return allCategoryExpense.value?.map { it.copy() } ?: mutableListOf()
    }

    fun getAllCategoryItemIncome(): List<CategoryItem> {
        return allCategoryIncome.value?.map { it.copy() } ?: mutableListOf()
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

    fun setPic(targetW: Int, targetH: Int, path: String): Bitmap? {
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, bmOptions)

        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        BitmapFactory.decodeFile(path, bmOptions)?.also { bitmap ->
            return bitmap
        }
        return null
    }

    fun initLoad() {
        _allCategoryExpense.value = CategoryStore.getAllCategoryExpenseForInput()
        _allCategoryIncome.value = CategoryStore.getAllCategoryIncomeForInput()
    }
}
