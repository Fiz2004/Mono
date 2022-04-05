package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
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
    val selectedAdapter: LiveData<Int> = _selectedAdapter

    private var _selected: MutableLiveData<Boolean> = MutableLiveData(false)
    val selected: LiveData<Boolean> = _selected

    private val _isReturn = MutableLiveData(false)
    val isReturn: LiveData<Boolean> = _isReturn

    private val _isMoveEdit = MutableLiveData(false)
    val isMoveEdit: LiveData<Boolean> = _isMoveEdit

    private val _isMoveCalendar = MutableLiveData(false)
    val isMoveCalendar: LiveData<Boolean> = _isMoveCalendar

    lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    private val _transaction: MutableLiveData<TransactionItem?> = MutableLiveData(null)
    val transaction: LiveData<TransactionItem?> = _transaction

    private var _allCategoryExpense =
        categoryStore.getAllCategoryExpenseForInput() as MutableLiveData
    val allCategoryExpense: LiveData<List<CategoryItem>> = _allCategoryExpense

    private var _allCategoryIncome = categoryStore.getAllCategoryIncomeForInput() as MutableLiveData
    var allCategoryIncome: LiveData<List<CategoryItem>> = _allCategoryIncome

    private val _note: MutableLiveData<String> = MutableLiveData("")
    val note: LiveData<String> = _note

    private val _value: MutableLiveData<String> = MutableLiveData("")
    val value: LiveData<String> = _value

    private val _photoPaths: MutableLiveData<MutableList<String?>> =
        MutableLiveData(mutableListOf())
    val photoPaths: LiveData<MutableList<String?>> = _photoPaths

    private val _photoBitmap: LiveData<List<Bitmap?>> = Transformations.map(photoPaths) { listPaths ->
        listPaths.map { path ->
            path?.let { setPic(300, 300, it) }
        }
    }
    val photoBitmap: LiveData<List<Bitmap?>> = _photoBitmap

    private fun getNewId(): Int {
        val lastItem = transactionStore.allTransactions.value?.lastOrNull()
        val id = lastItem?.id
        return id?.let { it + 1 } ?: 0
    }

    private fun isClickEditPosition(position: Int, selectedAdapter: Int?): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == allCategoryExpense.value?.size?.minus(1) ?: 0
        else
            position == allCategoryIncome.value?.size?.minus(1) ?: 0
    }

    private fun getCopyAllCategoryItemExpenseForInput(): List<CategoryItem> {
        return allCategoryExpense.value?.map { it.copy() } ?: listOf()
    }

    private fun getCopyAllCategoryItemIncomeForInput(): List<CategoryItem> {
        return allCategoryIncome.value?.map { it.copy() } ?: listOf()
    }

    private fun addSelectItemExpenseForInput(position: Int) {
        if (!allCategoryExpense.value?.get(position)?.selected!!) {
            allCategoryExpense.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryExpense.value?.get(position)!!.selected =
            !allCategoryExpense.value?.get(position)!!.selected
    }

    private fun addSelectItemIncomeForInput(position: Int) {
        if (!allCategoryIncome.value?.get(position)?.selected!!) {
            allCategoryIncome.value?.find { it.selected }?.let {
                it.selected = false
            }
        }

        allCategoryIncome.value?.get(position)?.selected =
            !allCategoryIncome.value?.get(position)?.selected!!
    }

    private fun addSelectItem(position: Int, selectedAdapter: Int?) {
        if (selectedAdapter == InputFragment.EXPENSE)
            addSelectItemExpenseForInput(position)
        else
            addSelectItemIncomeForInput(position)
    }

    fun cleanSelectedForInput() {
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
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

    private fun isSelectedForInput(selectedAdapter: Int?): Boolean {
        return getAllCategoryFromSelectedForInput(selectedAdapter).firstOrNull { it.selected } != null
    }

    private fun getSelectedInputForInput(selectedAdapter: Int?): CategoryItem {
        return getAllCategoryFromSelectedForInput(selectedAdapter).first { it.selected }
    }

    fun removeTransaction() {
        viewModelScope.launch {
            transaction.value?.let { transactionStore.delete(it) }
        }
        _isReturn.value = true
    }

    private fun clickUpdate(transaction: TransactionItem) {
        viewModelScope.launch {
            transactionStore.updateTransaction(transaction)
        }
    }

    private fun clickSubmit(
        transaction: TransactionItem
    ) {
        viewModelScope.launch {
            transactionStore.insertNewTransaction(transaction)
        }
    }

    fun setSelectedAdapter(adapter: Int) {
        _selectedAdapter.value = adapter
        _allCategoryExpense.value = allCategoryExpense.value
        _allCategoryIncome.value = allCategoryIncome.value
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (selectedAdapter.value) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            else -> context.getString(R.string.income)
        }
    }

    fun getTransactionItemForUpdate(selectedCategoryItem: CategoryItem): TransactionItem? {
        val valueTransaction = value.value?.toDouble()!! *
                if (selectedAdapter.value == InputFragment.EXPENSE)
                    -1
                else
                    1

        return transaction.value?.let {
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

    fun getTransactionItemForNew(
        selectedCategoryItem: CategoryItem,
        newId: Int,
        date: Calendar
    ): TransactionItem {
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

    private fun clickSubmit() {
        _photoPaths.value = emptyList<String>().toMutableList()
        _value.value = ""
        _note.value = ""
    }

    fun getSelectedAdapter(): Int {
        return selectedAdapter.value ?: InputFragment.EXPENSE
    }

    fun dispatchTakePictureIntent(context: Context): Intent? {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
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
        return null
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
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
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

        val photoW: Int = bmOptions.outWidth
        val photoH: Int = bmOptions.outHeight

        val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        BitmapFactory.decodeFile(path, bmOptions)?.also { bitmap ->
            return if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                val matrix = Matrix()
                matrix.postRotate(90f)
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            } else
                bitmap
        }
        return null
    }

    fun getExifRotation(imageFile: File?): Int {
        return if (imageFile == null) 0 else try {
            val exif = ExifInterface(imageFile.absolutePath)
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> ExifInterface.ORIENTATION_UNDEFINED
            }
        } catch (e: IOException) {
            //  Log.e("Error getting Exif data", e);
            0
        }
    }

    private fun setPhotoPath(list: MutableList<String?>) {
        _photoPaths.value = if (list.get(0) == "") emptyList<String?>().toMutableList() else list
    }

    fun addPhotoPath() {
        photoPaths.value?.add(currentPhotoPath)
        _photoPaths.value = photoPaths.value
    }

    fun removePhotoPath(number: Int) {
        photoPaths.value?.get(number - 1)?.let {
            val fDelete: File = File(it)
            if (fDelete.exists()) {
                fDelete.delete()
            }
        }
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

    private fun isCanSubmit(): Boolean {
        return value.value?.isNotBlank() == true
    }

    private fun changeSelected() {
        _selected.value = !_selected.value!!
    }

    fun init(transaction: Int) {
        _transaction.value = transactionStore.getTransactionByID(transaction)
    }

    fun isSubmitButtonEnabled(): Boolean {
        return isCanSubmit() && isSelectedForInput(selectedAdapter.value)
    }

    fun isReturnRefresh() {
        _isReturn.value = false
    }

    fun clickBackButton() {
        _isReturn.value = true
    }

    fun clickRecyclerView(position: Int) {
        if (checkClickEditPosition(position)) return
        addSelectItem(position, selectedAdapter.value)
        changeSelected()
        _allCategoryExpense.value = allCategoryExpense.value
        _allCategoryIncome.value = allCategoryIncome.value
    }

    private fun checkClickEditPosition(position: Int): Boolean {
        if (isClickEditPosition(position, selectedAdapter.value)) {
            cleanSelectedForInput()
            _isMoveEdit.value = true
            return true
        }
        return false
    }

    fun isMoveEditRefresh() {
        _isMoveEdit.value = false
    }

    fun clickSubmitButton(date: Calendar) {
        val selectedCategoryItem =
            getSelectedInputForInput(selectedAdapter.value)
        if (transaction.value != null) {
            val transaction =
                getTransactionItemForUpdate(selectedCategoryItem) ?: return
            clickUpdate(
                transaction
            )
            _isReturn.value = true
        } else {
            val newId = getNewId()
            val transaction =
                getTransactionItemForNew(
                    selectedCategoryItem,
                    newId,
                    date
                )
            clickSubmit(transaction)
            cleanSelectedForInput()
            clickSubmit()
            _allCategoryExpense.value = allCategoryExpense.value
            _allCategoryIncome.value = allCategoryIncome.value
        }
    }

    fun isMoveCalendarRefresh() {
        _isMoveCalendar.value = false
    }

    fun clickDate() {
        _isMoveCalendar.value = true
    }

    // TODO не вызывается, причина не понятна
    override fun onCleared() {
        super.onCleared()
        photoPaths.value?.forEach {
            it?.let {
                val fDelete: File = File(it)
                if (fDelete.exists()) {
                    fDelete.delete()
                }
            }
        }
    }
}