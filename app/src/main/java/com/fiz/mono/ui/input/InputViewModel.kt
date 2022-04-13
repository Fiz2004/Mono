package com.fiz.mono.ui.input

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.data.data_source.TransactionDataSource
import com.fiz.mono.data.entity.TransactionEntity
import com.fiz.mono.data.repositories.CategoryRepository
import com.fiz.mono.ui.models.CategoryUiState
import com.fiz.mono.ui.models.TransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class InputViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionDataSource: TransactionDataSource
) : ViewModel() {
    var uiState = MutableStateFlow(InputUiState())
        private set

    var navigationUiState = MutableStateFlow(InputNavigationState())
        private set

    private lateinit var currentPhotoPath: String

    private var cashCheckCameraHardware: Boolean? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryExpenseForInput().collect { allCategoryExpense ->
                uiState.update {
                    it.copy(
                        allCategoryExpense = allCategoryExpense
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            categoryRepository.getAllCategoryIncomeForInput().collect { allCategoryIncome ->
                uiState.update {
                    it.copy(
                        allCategoryIncome = allCategoryIncome
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            transactionDataSource.allTransactions.collect { allTransactions ->
                uiState.update {
                    it.copy(
                        allTransactions = allTransactions,
                        isAllTransactionsLoaded = true
                    )
                }
            }
        }
    }

    fun init(transactionId: Int) {
        uiState.update {
            it.copy(transactionId = transactionId)
        }
    }

    fun setSelected(nameCategory: String) {
        val position =
            uiState.value.currentCategory
                .indexOfFirst { it.name == nameCategory }

        if (uiState.value.selectedAdapter == InputFragment.EXPENSE) {
            selectExpense(position)
        } else {
                selectIncome(position)
            }
    }

    fun removeTransaction() {
        viewModelScope.launch(Dispatchers.Default) {
            uiState.value.transaction?.let { transactionDataSource.delete(it.toTransaction()) }
            navigationUiState.update {
                it.copy(isReturn = true)
            }
        }
    }

    fun setSelectedAdapter(adapter: Int) {
        if (uiState.value.selectedAdapter != adapter)
            uiState.update {
                it.copy(
                    allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                    allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                    selectedAdapter = adapter
                )
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
                it.localDate,
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
        date: LocalDate
    ): TransactionEntity {
        val state = uiState.value
        val valueTransaction = state.value.toDouble() *
                if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
                    -1
                else
                    1


        return TransactionEntity(
            newId,
            date,
            valueTransaction,
            selectedCategory.name,
            state.note,
            selectedCategory.toCategory().mapImgSrc,
            state.photoPaths
        )

    }

    fun setValue(text: String) {
        uiState.update {
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
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDate.now())
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
        uiState.update {
            it.copy(
                photoPaths = if (list[0] == "") emptyList<String?>().toMutableList() else list
            )
        }
    }

    fun addPhotoPath() {
        uiState.update {
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
            val fDelete = File(it)
            if (fDelete.exists()) {
                fDelete.delete()
            }
        }
        uiState.update {
            val photoPaths = it.photoPaths
            photoPaths.removeAt(number - 1)
            it.copy(
                photoPaths = photoPaths,
                isPhotoPathsChange = true
            )
        }
    }

    fun setNote(text: String) {
        uiState.update {
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

    fun isReturnRefresh() {
        navigationUiState.update {
            it.copy(isReturn = false)
        }
    }

    fun clickBackButton() {
        navigationUiState.update {
            it.copy(isReturn = true)
        }
    }

    fun clickRecyclerView(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            if (uiState.value.isClickEditPosition(position)) {
                uiState.update {
                    it.copy(
                        allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                    )
                }
                navigationUiState.update {
                    it.copy(
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
        uiState.update {
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
        uiState.update {
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
        navigationUiState.update {
            it.copy(isMoveEdit = false)
        }
    }

    fun clickSubmitButton(date: LocalDate) {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedCategoryItem =
                uiState.value.currentCategory
                    .first { it.selected }

            if (uiState.value.transaction != null) {
                val transaction =
                    getTransactionItemForUpdate(selectedCategoryItem) ?: return@launch

                transactionDataSource.updateTransaction(transaction)

                navigationUiState.update {
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

                uiState.update {
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
        navigationUiState.update {
            it.copy(isMoveCalendar = false)
        }
    }

    fun clickDate() {
        navigationUiState.update {
            it.copy(isMoveCalendar = true)
        }
    }

    // TODO не вызывается, причина не понятна
    override fun onCleared() {
        super.onCleared()
        Log.d("AAA", "123")
        uiState.value.photoPaths.forEach {
            it?.let {
                val fDelete = File(it)
                if (fDelete.exists()) {
                    fDelete.delete()
                }
            }
        }
    }

    fun onPhotoPathsChange() {
        uiState.update {
            it.copy(isPhotoPathsChange = false)
        }
    }

    fun onTrasactionLoaded() {
        uiState.update {
            it.copy(isAllTransactionsLoaded = false)
        }
    }

}