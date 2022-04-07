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
import com.fiz.mono.data.entity.Transaction
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
    val note: String = "",
    val value: String = "",
    val selectedAdapter: Int = InputFragment.EXPENSE,
    val transaction: TransactionUiState? = null,
    val photoPaths: MutableList<String?> = mutableListOf(),
    // Проверить будет ли работать как MediatorLiveData и автоматически собирать изменения
    val photoBitmap: List<Bitmap?> = getBitmapsFrom(photoPaths = photoPaths)
)

class InputViewModel(
    private val categoryDataSource: CategoryDataSource,
    private val transactionDataSource: TransactionDataSource
) : ViewModel() {
    private val _inputUiState = MutableStateFlow(InputUiState())
    val inputUiState: StateFlow<InputUiState> = _inputUiState.asStateFlow()

    lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    init {
        viewModelScope.launch {
            categoryDataSource.getAllCategoryExpenseForInput().collect { list ->
                _inputUiState.update {
                    it.copy(
                        allCategoryExpense = list
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryDataSource.getAllCategoryIncomeForInput().collect { list ->
                _inputUiState.update {
                    it.copy(
                        allCategoryIncome = list
                    )
                }
            }
        }
    }

    private fun isClickEditPosition(position: Int, selectedAdapter: Int?): Boolean {
        return if (selectedAdapter == InputFragment.EXPENSE)
            position == inputUiState.value.allCategoryExpense.size - 1
        else
            position == inputUiState.value.allCategoryIncome.size - 1
    }

    fun getAllCategoryFromSelectedForInput(selectedAdapter: Int?): List<CategoryUiState> {
        return if (selectedAdapter == InputFragment.EXPENSE)
            inputUiState.value.allCategoryExpense.map { it.copy() }
        else
            inputUiState.value.allCategoryIncome.map { it.copy() }
    }

    fun setSelected(selectedAdapter: Int?, nameCategory: String) {
        viewModelScope.launch {
            val position =
                getAllCategoryFromSelectedForInput(selectedAdapter).indexOfFirst { it.name == nameCategory }
            if (selectedAdapter
                == InputFragment.EXPENSE
            ) {
                categoryDataSource.selectExpense(position)
            } else {
                categoryDataSource.selectIncome(position)
            }
        }
    }

    fun removeTransaction() {
        viewModelScope.launch {
            inputUiState.value.transaction?.let { transactionDataSource.delete(it.toTransaction()) }
            _inputUiState.update {
                it.copy(isReturn = true)
            }
        }
    }

    private fun clickUpdate(transaction: Transaction) {
        viewModelScope.launch {
            transactionDataSource.updateTransaction(transaction)
        }
    }

    private fun clickSubmit(
        transaction: Transaction
    ) {
        viewModelScope.launch {
            transactionDataSource.insertNewTransaction(transaction)
        }
    }

    fun setSelectedAdapter(adapter: Int) {
        _inputUiState.update {
            it.copy(selectedAdapter = adapter)
        }
        inputUiState.value.allCategoryExpense.forEach { it.selectedFalse() }
        inputUiState.value.allCategoryIncome.forEach { it.selectedFalse() }
    }

    fun getTypeFromSelectedAdapter(context: Context): String {
        return when (inputUiState.value.selectedAdapter) {
            InputFragment.EXPENSE -> context.getString(R.string.expense)
            else -> context.getString(R.string.income)
        }
    }

    private suspend fun getTransactionItemForUpdate(selectedCategory: CategoryUiState): Transaction? {
        val state = inputUiState.value
        val valueTransaction = state.value.toDouble() *
                if (inputUiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1
        return state.transaction?.let {
            Transaction(
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
    ): Transaction {
        val state = inputUiState.value
        val valueTransaction = state.value.toDouble() *
                if (inputUiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1


        return Transaction(
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
            _inputUiState.update {
                it.copy(transaction = transactionDataSource.getTransactionByID(transaction))
            }
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
                selectExpense(position)
            } else {
                selectIncome(position)
            }
        }
    }

    fun selectExpense(position: Int) {
        inputUiState.value.allCategoryIncome.find { it.selected }?.let {
            it.selectedFalse()
        }

        val list = inputUiState.value.allCategoryExpense.map { it.copy() }

        if (!list[position].selected) {
            list.find { it.selected }?.let {
                it.selectedFalse()
            }
        }

        list[position].invertSelected()

        _inputUiState.update {
            it.copy(allCategoryExpense = list)
        }
    }

    fun selectIncome(position: Int) {
        inputUiState.value.allCategoryExpense.find { it.selected }?.let {
            it.selectedFalse()
        }

        val list = inputUiState.value.allCategoryIncome.map { it.copy() }

        if (!list[position].selected) {
            list.find { it.selected }?.let {
                it.selectedFalse()
            }
        }

        list[position].invertSelected()

        _inputUiState.update {
            it.copy(allCategoryIncome = list)
        }
    }

    private suspend fun checkClickEditPosition(position: Int): Boolean {
        if (isClickEditPosition(position, inputUiState.value.selectedAdapter)) {
            categoryDataSource.cleanSelected()
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
                val newId = transactionDataSource.getNewId()
                val transaction =
                    getTransactionItemForNew(
                        selectedCategoryItem,
                        newId,
                        date
                    )
                clickSubmit(transaction)
                categoryDataSource.cleanSelected()
                clickSubmit()
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