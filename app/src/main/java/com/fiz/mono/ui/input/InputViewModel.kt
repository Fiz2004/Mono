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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.R
import com.fiz.mono.data.data_source.CategoryDataSource
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.entity.TransactionEntity
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import com.fiz.mono.util.BitmapUtils.getBitmapsFrom
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
    val isReturn: Boolean = false,
    val isMoveEdit: Boolean = false,
    val isMoveCalendar: Boolean = false,
    val allCategoryExpense: List<CategoryUiState> = listOf(),
    val allCategoryIncome: List<CategoryUiState> = listOf(),
    val allTransactions: List<TransactionUiState> = listOf(),
    val note: String = "",
    val value: String = "",
    val selectedAdapter: Int = InputFragment.EXPENSE,
    val transaction: TransactionUiState? = null,
    val photoPaths: MutableList<String?> = mutableListOf(),
    val isPhotoPathsChange: Boolean = false
) {
    val photoBitmap: List<Bitmap?>
        get() = getBitmapsFrom(photoPaths = photoPaths)
}

class InputViewModel(
    private val categoryDataSource: CategoryDataSource,
    private val transactionDataSource: TransactionDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    init {
        viewModelScope.launch {
            categoryDataSource.getAllCategoryExpenseForInput().collect { allCategoryExpense ->
                _uiState.update {
                    it.copy(
                        allCategoryExpense = allCategoryExpense
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryDataSource.getAllCategoryIncomeForInput().collect { allCategoryIncome ->
                _uiState.update {
                    it.copy(
                        allCategoryIncome = allCategoryIncome
                    )
                }
            }
        }
        viewModelScope.launch {
            transactionDataSource.allTransactions.collect { allTransactions ->
                _uiState.update {
                    it.copy(
                        allTransactions = allTransactions
                    )
                }
            }
        }
    }

    private fun isClickEditPosition(position: Int): Boolean {
        return if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
            position == uiState.value.allCategoryExpense.size - 1
        else
            position == uiState.value.allCategoryIncome.size - 1
    }

    private fun getAllCategoryFromSelectedForInput(selectedAdapter: Int?): List<CategoryUiState> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            uiState.value.allCategoryExpense.map { it.copy() }
        else
            uiState.value.allCategoryIncome.map { it.copy() }
    }

    fun setSelected(nameCategory: String) {
        viewModelScope.launch {
            val position =
                getAllCategoryFromSelectedForInput(uiState.value.selectedAdapter)
                    .indexOfFirst { it.name == nameCategory }

            if (uiState.value.selectedAdapter == InputFragment.EXPENSE) {
                selectExpense(position)
            } else {
                selectIncome(position)
            }
        }
    }

    fun removeTransaction() {
        viewModelScope.launch {
            uiState.value.transaction?.let { transactionDataSource.delete(it.toTransaction()) }
            _uiState.update {
                it.copy(isReturn = true)
            }
        }
    }

    fun setSelectedAdapter(adapter: Int) {
        _uiState.update {
            it.copy(
                allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                selectedAdapter = adapter
            )
        }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (uiState.value.selectedAdapter) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            else -> context.getString(R.string.income)
        }
    }

    private suspend fun getTransactionItemForUpdate(selectedCategory: CategoryUiState): TransactionEntity? {
        val state = uiState.value
        val valueTransaction = state.value.toDouble() *
                if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1
        return state.transaction?.let {
            TransactionEntity(
                it.id,
                it.date,
                valueTransaction,
                selectedCategory.name,
                state.note,
                selectedCategory.toCategory().mapImgSrc,
                state.photoPaths
            )
        }
    }

    private suspend fun getTransactionItemForNew(
        selectedCategory: CategoryUiState,
        newId: Int,
        date: Calendar
    ): TransactionEntity {
        val state = uiState.value
        val valueTransaction = state.value.toDouble() *
                if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1


        return TransactionEntity(
            newId,
            date.time,
            valueTransaction,
            selectedCategory.name,
            state.note,
            selectedCategory.toCategory().mapImgSrc,
            state.photoPaths
        )

    }

    fun setValue(text: String) {
        _uiState.update {
            it.copy(value = text)
        }
    }

    fun getSelectedAdapter(): Int {
        return uiState.value.selectedAdapter
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
        _uiState.update {
            it.copy(
                photoPaths = if (list.get(0) == "") emptyList<String?>().toMutableList() else list
            )
        }
    }

    fun addPhotoPath() {
        _uiState.update {
            val photoPaths = it.photoPaths
            photoPaths.add(currentPhotoPath)
            it.copy(
                photoPaths = photoPaths,
                isPhotoPathsChange = true
            )
        }
    }

    fun removePhotoPath(number: Int) {
        uiState.value.photoPaths[number - 1]?.let {
            val fDelete: File = File(it)
            if (fDelete.exists()) {
                fDelete.delete()
            }
        }
        _uiState.update {
            val photoPaths = it.photoPaths
            photoPaths.removeAt(number - 1)
            it.copy(
                photoPaths = photoPaths,
                isPhotoPathsChange = true
            )
        }
    }

    fun setNote(text: String) {
        _uiState.update {
            it.copy(note = text)
        }
    }

    fun checkCameraHardware(context: Context): Boolean {
        if (uiState.value.photoPaths.size == InputFragment.MAX_PHOTO)
            return false
        if (cashCheckCameraHardware == null)
            cashCheckCameraHardware =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        return cashCheckCameraHardware ?: false
    }

    fun setViewModelTransaction(transaction: TransactionUiState) {
        if (transaction.value > 0)
            setSelectedAdapter(InputFragment.INCOME)
        else
            setSelectedAdapter(InputFragment.EXPENSE)

        transaction.value = abs(transaction.value)

        setValue(transaction.value.toString())
        setNote(transaction.note)
        setPhotoPath(transaction.photo.toMutableList())
    }

    fun init(transaction: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(transaction = transactionDataSource.getTransactionByID(transaction))
            }
        }
    }

    fun isSubmitButtonEnabled(): Boolean {
        return uiState.value.value.isNotBlank() && getAllCategoryFromSelectedForInput(
            uiState.value.selectedAdapter
        ).firstOrNull { it.selected } != null
    }

    fun isReturnRefresh() {
        _uiState.update {
            it.copy(isReturn = false)
        }
    }

    fun clickBackButton() {
        _uiState.update {
            it.copy(isReturn = true)
        }
    }

    fun clickRecyclerView(position: Int) {
        viewModelScope.launch {
            if (isClickEditPosition(position)) {
                _uiState.update {
                    it.copy(
                        allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                        isMoveEdit = true
                    )
                }
                return@launch
            }
            if (uiState.value.selectedAdapter == InputFragment.EXPENSE) {
                selectExpense(position)
            } else {
                selectIncome(position)
            }
        }
    }

    private fun selectExpense(position: Int) {
        _uiState.update {
            val allCategoryIncome = it.allCategoryIncome.map {
                var selected = it.selected
                if (it.selected)
                    selected = false
                it.copy(selected = selected)
            }

            val allCategoryExpense = it.allCategoryExpense.mapIndexed { index, categoryUiState ->
                var selected = categoryUiState.selected
                if (index != position && categoryUiState.selected)
                    selected = false
                if (index == position)
                    selected = !selected
                categoryUiState.copy(
                    selected =
                    selected
                )
            }

            it.copy(
                allCategoryExpense = allCategoryExpense,
                allCategoryIncome = allCategoryIncome
            )
        }
    }

    private fun selectIncome(position: Int) {
        _uiState.update {
            val allCategoryExpense = it.allCategoryExpense.map {
                var selected = it.selected
                if (it.selected)
                    selected = false
                it.copy(selected = selected)
            }

            val allCategoryIncome = it.allCategoryIncome.mapIndexed { index, categoryUiState ->
                var selected = categoryUiState.selected
                if (index != position && categoryUiState.selected)
                    selected = false
                if (index == position)
                    selected = !selected
                categoryUiState.copy(
                    selected =
                    selected
                )
            }

            it.copy(
                allCategoryExpense = allCategoryExpense,
                allCategoryIncome = allCategoryIncome
            )
        }
    }

    fun isMoveEditRefresh() {
        _uiState.update {
            it.copy(isMoveEdit = false)
        }
    }

    fun clickSubmitButton(date: Calendar) {
        viewModelScope.launch {
            val selectedCategoryItem =
                getAllCategoryFromSelectedForInput(uiState.value.selectedAdapter)
                    .first { it.selected }

            if (uiState.value.transaction != null) {
                val transaction =
                    getTransactionItemForUpdate(selectedCategoryItem) ?: return@launch

                transactionDataSource.updateTransaction(transaction)

                _uiState.update {
                    it.copy(isReturn = true)
                }
            } else {
                val lastItem = uiState.value.allTransactions.lastOrNull()
                val id = lastItem?.id
                val newId = id?.let { it + 1 } ?: 0
                val transaction =
                    getTransactionItemForNew(
                        selectedCategoryItem,
                        newId,
                        date
                    )
                transactionDataSource.insertNewTransaction(transaction)

                _uiState.update {
                    it.copy(
                        allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                        value = "",
                        note = "",
                        photoPaths = emptyList<String>().toMutableList()
                    )
                }
            }
        }
    }

    fun isMoveCalendarRefresh() {
        _uiState.update {
            it.copy(isMoveCalendar = false)
        }
    }

    fun clickDate() {
        _uiState.update {
            it.copy(isMoveCalendar = true)
        }
    }

    // TODO не вызывается, причина не понятна
    override fun onCleared() {
        super.onCleared()
        Log.d("AAA", "123")
        uiState.value.photoPaths.forEach {
            it?.let {
                val fDelete: File = File(it)
                if (fDelete.exists()) {
                    fDelete.delete()
                }
            }
        }
    }

    fun onPhotoPathsChange() {
        _uiState.update {
            it.copy(isPhotoPathsChange = false)
        }
    }

}