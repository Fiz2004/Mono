package com.fiz.mono.input.ui

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
import com.fiz.mono.database.entity.TransactionEntity
import com.fiz.mono.database.mapper.toCategoryEntity
import com.fiz.mono.database.mapper.toTransaction
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.SettingsLocalDataSource
import com.fiz.mono.domain.repositories.TransactionRepository
import com.fiz.mono.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class InputViewModel @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var uiState = MutableStateFlow(InputUiState()); private set

    var navigationUiState = MutableStateFlow(InputNavigationState()); private set

    fun getFormatDate(pattern: String): String {
        return DateTimeFormatter.ofPattern(pattern).format(uiState.value.date)
    }

    fun setMonth(month: Int) {
        val newDate = uiState.value.date.withMonth(month)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun setDate(day: Int) {
        if (day == 0) return

        val newDate = uiState.value.date.withDayOfMonth(day)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateDayPlusOne() {
        val newDate = uiState.value.date.plusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateDayMinusOne() {
        val newDate = uiState.value.date.minusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateMonthPlusOne() {
        val newDate = uiState.value.date.plusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    fun dateMonthMinusOne() {
        val newDate = uiState.value.date.minusMonths(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsLocalDataSource.saveDate(newDate)
        }
    }

    init {
        viewModelScope.launch {
            settingsLocalDataSource.stateFlow.collect {
                navigationUiState.value = navigationUiState.value
                    .copy(isMoveOnBoarding = it.firstTime)

                uiState.value = uiState.value
                    .copy(date = it.date)

                if (it.isNeedConfirmPIN && !it.isCurrentConfirmPIN)
                    navigationUiState.value = navigationUiState.value
                        .copy(isMovePinPassword = true)

                uiState.value = uiState.value
                    .copy(currency = it.currency)
            }
        }
    }

    private var cashCheckCameraHardware: Boolean? = null

    init {

        categoryRepository.getAllCategoryExpenseForInput()
            .onEach { allCategoryExpense ->

                val isLoading = when (allCategoryExpense) {
                    is Resource.Loading -> true
                    is Resource.Error -> false
                    is Resource.Success -> false
                }

                val allCategoryExpenseData = when (allCategoryExpense) {
                    is Resource.Loading -> listOf()
                    is Resource.Error -> listOf()
                    is Resource.Success -> allCategoryExpense.data ?: listOf()
                }

                uiState.value = uiState.value
                    .copy(
                        isLoading = isLoading,
                        allCategoryExpense = allCategoryExpenseData
                    )
            }.launchIn(viewModelScope)

        categoryRepository.getAllCategoryIncomeForInput()
            .onEach { allCategoryIncome ->
                uiState.value = uiState.value
                    .copy(allCategoryIncome = allCategoryIncome)
            }.launchIn(viewModelScope)

        transactionRepository.allTransactions
            .onEach { allTransactions ->
                uiState.value =
                    uiState.value
                        .copy(
                            allTransactions = allTransactions,
                            isAllTransactionsLoaded = true
                        )
            }.launchIn(viewModelScope)

    }

    fun onEvent(event: InputUiEvent) {
        when (event) {
            is InputUiEvent.ChangeTypeTransactions -> {
                event.typeTransactions?.let {
                    when (it) {
                        0 -> setSelectedAdapter(InputFragment.EXPENSE)
                        1 -> setSelectedAdapter(InputFragment.INCOME)
                    }
                }
            }

            is InputUiEvent.ClickCategory -> TODO()

            is InputUiEvent.ClickData -> {
                clickDate()
            }

            is InputUiEvent.ClickLeftData -> TODO()
            is InputUiEvent.ClickRightData -> TODO()

            is InputUiEvent.ClickSubmit -> {
                clickSubmitButton()
            }

            is InputUiEvent.NoteChange -> {
                setNote(event.newNote)
            }

            is InputUiEvent.ValueChange -> {
                setValue(event.newValue)
            }
        }
    }

    fun start(transactionId: Int) {
        uiState.update {
            it.copy(transactionId = transactionId)
        }
    }

    fun setSelected(nameCategory: String) {
        val position =
            uiState.value.currentCategory
                .indexOfFirst { it.name == nameCategory }

        if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
            selectExpense(position)
        else
            selectIncome(position)
    }

    fun removeTransaction() {
        viewModelScope.launch(Dispatchers.Default) {
            uiState.value.transaction?.let { transactionRepository.delete(it) }
            navigationUiState.update {
                it.copy(isMoveReturn = true)
            }
        }
    }

    private fun setSelectedAdapter(adapter: Int) {
        if (uiState.value.selectedAdapter != adapter)
            uiState.update {
                it.copy(
                    allCategoryExpense = it.allCategoryExpense.map { it.copy(selected = false) },
                    allCategoryIncome = it.allCategoryIncome.map { it.copy(selected = false) },
                    selectedAdapter = adapter
                )
            }
    }

    private suspend fun getTransactionItemForUpdate(selectedCategory: Category): TransactionEntity? {
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
                selectedCategory.toCategoryEntity().mapImgSrc,
                state.photoPaths
            )
        }
    }

    private suspend fun getTransactionItemForNew(
        selectedCategory: Category,
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
            selectedCategory.toCategoryEntity().mapImgSrc,
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
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneOffset.UTC).format(Instant.now())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            uiState.update {
                it.copy(
                    currentPhotoPath = absolutePath
                )
            }
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
            val photoPaths = it.photoPaths.toMutableList()
            photoPaths.add(it.currentPhotoPath)
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
            val photoPaths = it.photoPaths.toMutableList()
            photoPaths.removeAt(number - 1)
            it.copy(
                photoPaths = photoPaths,
                isPhotoPathsChange = true
            )
        }
    }

    private fun setNote(newNote: String) {
        uiState.update {
            it.copy(note = newNote)
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

    fun setViewModelTransaction(transaction: Transaction) {
        if (transaction.value > 0)
            setSelectedAdapter(InputFragment.INCOME)
        else
            setSelectedAdapter(InputFragment.EXPENSE)

        transaction.value = abs(transaction.value)

        setValue(transaction.value.toString())
        setNote(transaction.note)
        setPhotoPath(transaction.photo.toMutableList())
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


    private fun clickSubmitButton() {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedCategoryItem =
                uiState.value.currentCategory
                    .first { it.selected }

            if (uiState.value.transaction != null) {
                val transaction =
                    getTransactionItemForUpdate(selectedCategoryItem) ?: return@launch

                transactionRepository.updateTransaction(transaction.toTransaction())

                navigationUiState.update {
                    it.copy(isMoveReturn = true)
                }
            } else {
                val lastItem = uiState.value.allTransactions.lastOrNull()
                val id = lastItem?.id
                val newId = id?.let { it + 1 } ?: 0
                val transaction =
                    getTransactionItemForNew(
                        selectedCategoryItem,
                        newId,
                        uiState.value.date
                    )
                transactionRepository.insertNewTransaction(transaction.toTransaction())

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

    // TODO не вызывается, причина не понятна
    override fun onCleared() {
        super.onCleared()
        if (uiState.value.isInput) {
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

    fun clickBackButton() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveReturn = true)
    }

    private fun clickDate() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveCalendar = true)
    }

    fun movedCalendar() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveCalendar = false)
    }

    fun movedOnBoarding() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveOnBoarding = false)
    }

    fun movedPinPassword() {
        navigationUiState.value = navigationUiState.value
            .copy(isMovePinPassword = false)
    }


    fun movedEdit() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveEdit = false)
    }


    fun returned() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveReturn = false)

    }

}