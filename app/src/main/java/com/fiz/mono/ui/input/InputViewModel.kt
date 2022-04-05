package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.fiz.mono.util.BitmapUtils.setPic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

data class InputUiState(
    val isSelected: Boolean = false,
    val isReturn: Boolean = false,
    val isMoveEdit: Boolean = false,
    val isMoveCalendar: Boolean = false,
    val note: String = "",
    val value: String = "",
    val selectedAdapter: Int = InputFragment.EXPENSE,
    val transaction: TransactionItem? = null,
    val photoPaths: MutableList<String?> = mutableListOf(),
    val photoBitmap: List<Bitmap?> = photoPaths.map { path ->
        path?.let { setPic(300, 300, it) }
    }
)

class InputViewModel(
    private val categoryStore: CategoryStore,
    private val transactionStore: TransactionStore
) : ViewModel() {
    private var _inputUiState = MutableStateFlow(InputUiState())
    val inputUiState: StateFlow<InputUiState> = _inputUiState.asStateFlow()

    lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    private var _allCategoryExpense =
        categoryStore.getAllCategoryExpenseForInput() as MutableLiveData
    val allCategoryExpense: LiveData<List<CategoryItem>> = _allCategoryExpense

    private var _allCategoryIncome = categoryStore.getAllCategoryIncomeForInput() as MutableLiveData
    var allCategoryIncome: LiveData<List<CategoryItem>> = _allCategoryIncome

    private fun isClickEditPosition(position: Int, selectedAdapter: Int?): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == allCategoryExpense.value?.size?.minus(1) ?: 0
        else
            position == allCategoryIncome.value?.size?.minus(1) ?: 0
    }

    fun getAllCategoryFromSelectedForInput(selectedAdapter: Int?): List<CategoryItem> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            allCategoryExpense.value?.map { it.copy() } ?: listOf()
        else
            allCategoryIncome.value?.map { it.copy() } ?: listOf()
    }

    fun setSelected(selectedAdapter: Int?, nameCategory: String) {
        viewModelScope.launch {
            val position =
                getAllCategoryFromSelectedForInput(selectedAdapter).indexOfFirst { it.name == nameCategory }
            if (selectedAdapter
                == InputFragment.EXPENSE
            ) {
                categoryStore.selectExpense(position)
            } else {
                categoryStore.selectIncome(position)
            }
        }
    }

    fun removeTransaction() {
        viewModelScope.launch {
            inputUiState.value.transaction?.let { transactionStore.delete(it) }
            _inputUiState.update {
                it.copy(isReturn = true)
            }
        }
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
        _inputUiState.update {
            it.copy(selectedAdapter = adapter)
        }
        _allCategoryExpense.value = allCategoryExpense.value
        _allCategoryIncome.value = allCategoryIncome.value
        allCategoryExpense.value?.forEach { it.selected = false }
        allCategoryIncome.value?.forEach { it.selected = false }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (inputUiState.value.selectedAdapter) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            else -> context.getString(R.string.income)
        }
    }

    private fun getTransactionItemForUpdate(selectedCategoryItem: CategoryItem): TransactionItem? {
        val state = inputUiState.value
        val valueTransaction = state.value.toDouble() *
                if (inputUiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1
        return state.transaction?.let {
            TransactionItem(
                it.id,
                it.date,
                valueTransaction,
                selectedCategoryItem.name,
                state.note,
                selectedCategoryItem.mapImgSrc,
                state.photoPaths
            )
        }
    }

    private fun getTransactionItemForNew(
        selectedCategoryItem: CategoryItem,
        newId: Int,
        date: Calendar
    ): TransactionItem {
        val state = inputUiState.value
        val valueTransaction = state.value.toDouble() *
                if (inputUiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1


        return TransactionItem(
            newId,
            date.time,
            valueTransaction,
            selectedCategoryItem.name,
            state.note,
            selectedCategoryItem.mapImgSrc,
            state.photoPaths
        )

    }

    fun setValue(text: String) {
        _inputUiState.update {
            it.copy(value = text)
        }
    }

    private fun clickSubmit() {
        _inputUiState.update {
            it.copy(
                value = "",
                note = "",
                photoPaths = emptyList<String>().toMutableList()
            )
        }
    }

    fun getSelectedAdapter(): Int {
        return inputUiState.value.selectedAdapter
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

    private fun setPhotoPath(list: MutableList<String?>) {
        _inputUiState.update {
            it.copy(
                photoPaths = if (list.get(0) == "") emptyList<String?>().toMutableList() else list
            )
        }
    }

    fun addPhotoPath() {
        _inputUiState.update {
            it.photoPaths.add(currentPhotoPath)
            it.copy()
        }
    }

    fun removePhotoPath(number: Int) {
        inputUiState.value.photoPaths[number - 1]?.let {
            val fDelete: File = File(it)
            if (fDelete.exists()) {
                fDelete.delete()
            }
        }
        _inputUiState.update {
            it.photoPaths.removeAt(number - 1)
            it.copy()
        }
    }

    fun setNote(text: String) {
        _inputUiState.update {
            it.copy(note = text)
        }
    }

    fun checkCameraHardware(context: Context): Boolean {
        if (inputUiState.value.photoPaths.size == InputFragment.MAX_PHOTO)
            return false
        if (cashCheckCameraHardware == null)
            cashCheckCameraHardware =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        return cashCheckCameraHardware ?: false
    }

    fun setViewModelTransaction(transaction: TransactionItem) {
        if (transaction.value > 0)
            setSelectedAdapter(InputFragment.INCOME)
        else
            setSelectedAdapter(InputFragment.EXPENSE)

        transaction.value = abs(transaction.value)

        setValue(transaction.value.toString())
        setNote(transaction.note)
        setPhotoPath(transaction.photo.toMutableList())
    }

    private fun changeSelected() {
        _inputUiState.update {
            it.copy(isSelected = !it.isSelected)
        }
    }

    fun init(transaction: Int) {
        _inputUiState.update {
            it.copy(transaction = transactionStore.getTransactionByID(transaction))
        }
    }

    fun isSubmitButtonEnabled(): Boolean {
        return inputUiState.value.value.isNotBlank() && getAllCategoryFromSelectedForInput(
            inputUiState.value.selectedAdapter
        ).firstOrNull { it.selected } != null
    }

    fun isReturnRefresh() {
        _inputUiState.update {
            it.copy(isReturn = false)
        }
    }

    fun clickBackButton() {
        _inputUiState.update {
            it.copy(isReturn = true)
        }
    }

    fun clickRecyclerView(position: Int) {
        viewModelScope.launch {
            if (checkClickEditPosition(position)) return@launch
            if (inputUiState.value.selectedAdapter == InputFragment.EXPENSE) {
                categoryStore.selectExpense(position)
            } else {
                categoryStore.selectIncome(position)
            }
            changeSelected()
            _allCategoryExpense.value = allCategoryExpense.value
            _allCategoryIncome.value = allCategoryIncome.value
        }
    }

    private suspend fun checkClickEditPosition(position: Int): Boolean {
        if (isClickEditPosition(position, inputUiState.value.selectedAdapter)) {
            categoryStore.cleanSelected()
            _inputUiState.update {
                it.copy(isMoveEdit = true)
            }
            return true
        }
        return false
    }

    fun isMoveEditRefresh() {
        _inputUiState.update {
            it.copy(isMoveEdit = false)
        }
    }

    fun clickSubmitButton(date: Calendar) {
        viewModelScope.launch {
            val selectedCategoryItem =
                getAllCategoryFromSelectedForInput(inputUiState.value.selectedAdapter).first { it.selected }
            if (inputUiState.value.transaction != null) {
                val transaction =
                    getTransactionItemForUpdate(selectedCategoryItem) ?: return@launch
                clickUpdate(
                    transaction
                )
                _inputUiState.update {
                    it.copy(isReturn = true)
                }
            } else {
                val newId = transactionStore.getNewId()
                val transaction =
                    getTransactionItemForNew(
                        selectedCategoryItem,
                        newId,
                        date
                    )
                clickSubmit(transaction)
                categoryStore.cleanSelected()
                clickSubmit()
                _allCategoryExpense.value = allCategoryExpense.value
                _allCategoryIncome.value = allCategoryIncome.value
            }
        }
    }

    fun isMoveCalendarRefresh() {
        _inputUiState.update {
            it.copy(isMoveCalendar = false)
        }
    }

    fun clickDate() {
        _inputUiState.update {
            it.copy(isMoveCalendar = true)
        }
    }

    // TODO не вызывается, причина не понятна
    override fun onCleared() {
        super.onCleared()
        Log.d("AAA", "123")
        inputUiState.value.photoPaths.forEach {
            it?.let {
                val fDelete: File = File(it)
                if (fDelete.exists()) {
                    fDelete.delete()
                }
            }
        }
    }

}