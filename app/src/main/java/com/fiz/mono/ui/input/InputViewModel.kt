package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.fiz.mono.R
import com.fiz.mono.data.CategoryItem
import com.fiz.mono.data.CategoryStore
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.TransactionStore
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class InputViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) : ViewModel() {
    private var _selectedAdapter: MutableLiveData<Int> = MutableLiveData(InputFragment.EXPENSE)
    val selectedAdapter: LiveData<Int>
        get() = _selectedAdapter

    private var _selected: MutableLiveData<Boolean> = MutableLiveData(false)
    val selected: LiveData<Boolean>
        get() = _selected

    lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    var transaction: TransactionItem? = null

    var allCategoryExpenseForInput = categoryStore.getAllCategoryExpenseForInput()
    var allCategoryIncomeForInput = categoryStore.getAllCategoryIncomeForInput()

    private val _note: MutableLiveData<String> = MutableLiveData("")
    val note: LiveData<String>
        get() = _note

    private val _value: MutableLiveData<String> = MutableLiveData("")
    val value: LiveData<String>
        get() = _value

    private val _photoPaths: MutableLiveData<MutableList<String?>> =
        MutableLiveData(mutableListOf())
    val photoPaths: LiveData<MutableList<String?>>
        get() = _photoPaths

    fun getNewId(): Int {
        val lastItem = transactionStore.allTransactions.value?.lastOrNull()
        val id = lastItem?.id
        return id?.let { it + 1 } ?: 0
    }

    fun isClickEditPosition(position: Int, selectedAdapter: Int?): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == allCategoryExpenseForInput.value?.size?.minus(1) ?: 0
        else
            position == allCategoryIncomeForInput.value?.size?.minus(1) ?: 0
    }

    fun getCopyAllCategoryItemExpenseForInput(): List<CategoryItem> {
        return allCategoryExpenseForInput.value?.map { it.copy() } ?: listOf()
    }

    fun getCopyAllCategoryItemIncomeForInput(): List<CategoryItem> {
        return allCategoryIncomeForInput.value?.map { it.copy() } ?: listOf()
    }

    private fun addSelectItemExpenseForInput(position: Int) {
        if (!allCategoryExpenseForInput.value?.get(position)?.selected!!) {
            allCategoryExpenseForInput.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpenseForInput.value?.get(position)!!.selected =
            !allCategoryExpenseForInput.value?.get(position)!!.selected
    }

    private fun addSelectItemIncomeForInput(position: Int) {
        if (!allCategoryIncomeForInput.value?.get(position)?.selected!!) {
            allCategoryIncomeForInput.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncomeForInput.value?.get(position)?.selected =
            !allCategoryIncomeForInput.value?.get(position)?.selected!!
    }

    fun addSelectItem(position: Int, selectedAdapter: Int?) {
        if (selectedAdapter == InputFragment.EXPENSE)
            addSelectItemExpenseForInput(position)
        else
            addSelectItemIncomeForInput(position)
    }

    fun cleanSelectedForInput() {
        allCategoryExpenseForInput.value?.forEach { it.selected = false }
        allCategoryIncomeForInput.value?.forEach { it.selected = false }
    }

    fun getAllCategoryFromSelectedForInput(selectedAdapter: Int?): List<CategoryItem> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            getCopyAllCategoryItemExpenseForInput()
        else
            getCopyAllCategoryItemIncomeForInput()
    }

    fun setSelected(selectedAdapter: Int?, nameCategory: String) {
        addSelectItem(
            getAllCategoryFromSelectedForInput(selectedAdapter).indexOfFirst { it.name == nameCategory },
            selectedAdapter
        )
    }

    fun isSelectedForInput(selectedAdapter: Int?): Boolean {
        return getAllCategoryFromSelectedForInput(selectedAdapter).firstOrNull { it.selected } != null
    }

    fun getSelectedInputForInput(selectedAdapter: Int?): CategoryItem {
        return getAllCategoryFromSelectedForInput(selectedAdapter).first { it.selected }
    }

    fun findTransaction(currentTransaction: Int): TransactionItem? {
        return if (currentTransaction != -1)
            transactionStore.allTransactions.value?.find { it.id == currentTransaction }?.copy()
        else
            null
    }

    fun removeTransaction(transaction: TransactionItem?) {
        viewModelScope.launch {
            transaction?.let { transactionStore.delete(it) }
        }
    }

    fun clickUpdate(transaction: TransactionItem) {
        viewModelScope.launch {
            transactionStore.updateTransaction(transaction)
        }
    }

    fun clickSubmit(
        transaction: TransactionItem
    ) {
        viewModelScope.launch {
            transactionStore.insertNewTransaction(transaction)
        }
    }

    fun setSelectedAdapter(adapter: Int) {
        _selectedAdapter.value = adapter
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (selectedAdapter.value) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            InputFragment.INCOME -> context.getString(R.string.income)
            else -> {
                throw Error("Not adapter")
            }
        }
    }

    fun getTransactionItemForUpdate(selectedCategoryItem: CategoryItem): TransactionItem? {
        val valueTransaction = value.value?.toDouble()!! *
                if (selectedAdapter.value == InputFragment.EXPENSE)
                    -1
                else
                    1

        return transaction?.let {
            TransactionItem(
                it.id,
                it.date,
                valueTransaction,
                selectedCategoryItem.name,
                note.value ?: "",
                selectedCategoryItem.mapImgSrc,
                photoPaths.value ?: emptyList()
            )
        }
    }

    fun getTransactionItemForNew(selectedCategoryItem: CategoryItem, newId: Int, date: Calendar): TransactionItem {
        val valueTransaction = value.value?.toDouble()!! *
                if (selectedAdapter.value == InputFragment.EXPENSE)
                    -1
                else
                    1


        return TransactionItem(
            newId,
            date.time,
            valueTransaction,
            selectedCategoryItem.name,
            note.value ?: "",
            selectedCategoryItem.mapImgSrc,
            photoPaths.value ?: emptyList()
        )

    }

    fun setValue(text: String) {
        _value.value = text
    }

    fun clickSubmit() {
        _photoPaths.value = emptyList<String>().toMutableList()
        _value.value = ""
        _note.value = ""
    }

    fun getSelectedAdapter(): Int {
        return selectedAdapter.value ?: InputFragment.EXPENSE
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
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

    fun setPhotoPath(list: MutableList<String?>) {
        _photoPaths.value = if (list.get(0) == "") emptyList<String?>().toMutableList() else list
    }

    fun addPhotoPath() {
        photoPaths.value?.add(currentPhotoPath)
        _photoPaths.value = photoPaths.value
    }

    fun removePhotoPath(number: Int) {
        photoPaths.value?.removeAt(number - 1)
        _photoPaths.value = photoPaths.value
    }

    fun setNote(text: String) {
        _note.value = text
    }

    fun checkCameraHardware(context: Context): Boolean {
        if (photoPaths.value?.size == InputFragment.MAX_PHOTO)
            return false
        if (cashCheckCameraHardware == null)
            cashCheckCameraHardware =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        return cashCheckCameraHardware ?: false
    }

    fun setViewmodelTransaction(transaction: TransactionItem) {
        if (transaction.value > 0)
            setSelectedAdapter(InputFragment.INCOME)
        else
            setSelectedAdapter(InputFragment.EXPENSE)

        transaction.value = abs(transaction.value)

        setValue(transaction.value.toString())
        setNote(transaction.note)
        setPhotoPath(transaction.photo.toMutableList())
    }

    fun isCanSubmit(): Boolean {
        return value.value?.isNotBlank() == true
    }

    fun changeSelected() {
        _selected.value = !_selected.value!!
    }
}

class InputViewModelFactory(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InputViewModel(categoryStore, transactionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

