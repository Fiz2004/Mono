package com.fiz.mono.input.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.database.entity.TransactionEntity
import com.fiz.mono.database.mapper.toCategoryEntity
import com.fiz.mono.database.mapper.toTransaction
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import com.fiz.mono.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class InputViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var uiState = MutableStateFlow(InputUiState()); private set

    var navigationUiState = MutableStateFlow(InputNavigationState()); private set

    init {

        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(false)
        }

        settingsRepository.currency.load()
            .onEach { currency ->
                uiState.value = uiState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

        settingsRepository.firstTime.load()
            .onEach { firstTime ->
                navigationUiState.value = navigationUiState.value
                    .copy(isMoveOnBoarding = firstTime)
            }.launchIn(viewModelScope)

        settingsRepository.needConfirmPin.load()
            .onEach { needConfirmPin ->
                val need = needConfirmPin
            }.launchIn(viewModelScope)

        settingsRepository.needConfirmPin.load()
            .zip(settingsRepository.currentConfirmPin.load()) { needConfirmPin, currentConfirmPin ->
                needConfirmPin && !currentConfirmPin
            }
            .onEach { isMovePinPassword ->
                if (isMovePinPassword)
                    navigationUiState.value = navigationUiState.value
                        .copy(isMovePinPassword = true)
            }.launchIn(viewModelScope)
    }


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
                        )
                uiState.value.transaction?.let {
                    setViewModelTransaction(it)
                    setSelected(it.nameCategory)
                }
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

            is InputUiEvent.ClickData -> {
                clickDate()
            }

            is InputUiEvent.ClickLeftData -> dateDayMinusOne()
            is InputUiEvent.ClickRightData -> dateDayPlusOne()

            is InputUiEvent.ClickSubmit -> {
                clickSubmitButton()
            }

            is InputUiEvent.NoteChange -> {
                setNote(event.newNote)
            }

            is InputUiEvent.ValueChange -> {
                setValue(event.newValue)
            }
            InputUiEvent.ClickBackButton -> clickBackButton()
            InputUiEvent.ClickRemoveTransaction -> removeTransaction()
            is InputUiEvent.ClickRemovePhoto -> removePhotoPath(event.numberPhoto)
            is InputUiEvent.ClickCategory -> clickRecyclerView(event.position)
            InputUiEvent.AddPhotoPath -> addPhotoPath()
            is InputUiEvent.Init -> start(event.transactionId)
            is InputUiEvent.UpdateCurrentPhotoPath -> updateCurrentPhotoPath(event.absolutePath)
            InputUiEvent.MovedCalendar -> movedCalendar()
            InputUiEvent.MovedEdit -> movedEdit()
            InputUiEvent.MovedOnBoarding -> movedOnBoarding()
            InputUiEvent.MovedPinPassword -> movedPinPassword()
            InputUiEvent.Returned -> returned()
        }
    }

    private fun dateDayPlusOne() {
        val newDate = uiState.value.date.plusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsRepository.date.save(newDate)
        }
    }

    private fun dateDayMinusOne() {
        val newDate = uiState.value.date.minusDays(1)

        uiState.value = uiState.value
            .copy(date = newDate)

        viewModelScope.launch {
            settingsRepository.date.save(newDate)
        }
    }

    private fun start(transactionId: Int) {
        uiState.update {
            it.copy(transactionId = transactionId)
        }
    }

    private fun setSelected(nameCategory: String) {
        val position =
            uiState.value.currentCategory
                .indexOfFirst { it.name == nameCategory }

        if (uiState.value.selectedAdapter == InputFragment.EXPENSE)
            selectExpense(position)
        else
            selectIncome(position)
    }

    private fun removeTransaction() {
        viewModelScope.launch(Dispatchers.Default) {
            uiState.value.transaction?.let { transactionRepository.delete(it) }
            navigationUiState.update {
                it.copy(isMoveReturn = true)
            }
        }
    }

    private fun setSelectedAdapter(adapter: Int) {
        if (uiState.value.selectedAdapter != adapter)
            uiState.update { inputUiState ->
                inputUiState.copy(
                    allCategoryExpense = inputUiState.allCategoryExpense.map { it.copy(selected = false) },
                    allCategoryIncome = inputUiState.allCategoryIncome.map { it.copy(selected = false) },
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

    private fun setValue(text: String) {
        uiState.update {
            it.copy(value = text)
        }
    }

    private fun setPhotoPath(list: MutableList<String?>) {
        uiState.update {
            it.copy(
                photoPaths = if (list[0] == "") emptyList<String?>().toMutableList() else list
            )
        }
    }

    private fun addPhotoPath() {
        uiState.update {
            val photoPaths = it.photoPaths.toMutableList()
            photoPaths.add(it.currentPhotoPath)
            it.copy(
                photoPaths = photoPaths,
            )
        }
    }

    private fun removePhotoPath(number: Int) {
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
        if (uiState.value.cashCheckCameraHardware == null)
            uiState.value = uiState.value
                .copy(
                    cashCheckCameraHardware =
                    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                )
        return uiState.value.cashCheckCameraHardware ?: false
    }

    private fun setViewModelTransaction(transaction: Transaction) {
        if (transaction.value > 0)
            setSelectedAdapter(InputFragment.INCOME)
        else
            setSelectedAdapter(InputFragment.EXPENSE)

        transaction.value = abs(transaction.value)

        setValue(transaction.value.toString())
        setNote(transaction.note)
        setPhotoPath(transaction.photo.toMutableList())
    }

    private fun clickRecyclerView(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            if (uiState.value.isClickEditPosition(position)) {
                uiState.update { inputUiState ->
                    inputUiState.copy(
                        allCategoryExpense = inputUiState.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = inputUiState.allCategoryIncome.map { it.copy(selected = false) },
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
        uiState.update { inputUiState ->
            val allCategoryIncome = inputUiState.allCategoryIncome.map {
                var selected = it.selected
                if (it.selected)
                    selected = false
                it.copy(selected = selected)
            }

            val allCategoryExpense =
                inputUiState.allCategoryExpense.mapIndexed { index, categoryUiState ->
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

            inputUiState.copy(
                allCategoryExpense = allCategoryExpense,
                allCategoryIncome = allCategoryIncome
            )
        }
    }

    private fun selectIncome(position: Int) {
        uiState.update { inputUiState ->
            val allCategoryExpense = inputUiState.allCategoryExpense.map {
                var selected = it.selected
                if (it.selected)
                    selected = false
                it.copy(selected = selected)
            }

            val allCategoryIncome =
                inputUiState.allCategoryIncome.mapIndexed { index, categoryUiState ->
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

            inputUiState.copy(
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

                uiState.update { inputUiState ->
                    inputUiState.copy(
                        allCategoryExpense = inputUiState.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = inputUiState.allCategoryIncome.map { it.copy(selected = false) },
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

    private fun clickBackButton() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveReturn = true)
    }

    private fun clickDate() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveCalendar = true)
    }

    private fun movedCalendar() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveCalendar = false)
    }

    private fun movedOnBoarding() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveOnBoarding = false)
    }

    private fun movedPinPassword() {
        navigationUiState.value = navigationUiState.value
            .copy(isMovePinPassword = false)
    }


    private fun movedEdit() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveEdit = false)
    }


    private fun returned() {
        navigationUiState.value = navigationUiState.value
            .copy(isMoveReturn = false)

    }

    private fun updateCurrentPhotoPath(absolutePath: String) {
        uiState.update {
            it.copy(
                currentPhotoPath = absolutePath
            )
        }
    }
}