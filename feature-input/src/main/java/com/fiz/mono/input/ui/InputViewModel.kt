package com.fiz.mono.input.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.data.entity.TransactionEntity
import com.fiz.mono.data.mapper.toCategoryEntity
import com.fiz.mono.data.mapper.toTransaction
import com.fiz.mono.domain.models.Category
import com.fiz.mono.domain.repositories.CategoryRepository
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class InputViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsRepository: SettingsRepository,
    categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var viewState = MutableStateFlow(InputViewState())
        private set

    var viewEffects = MutableSharedFlow<InputViewEffect>()
        private set

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy (EEE)")

    init {
        if (settingsRepository.firstTime)
            viewModelScope.launch {
                viewEffects.emit(InputViewEffect.MoveOnBoarding)
            }

        settingsRepository.needConfirmPin.load()
            .zip(settingsRepository.currentConfirmPin.load()) { needConfirmPin, currentConfirmPin ->
                needConfirmPin && !currentConfirmPin
            }
            .onEach { isMovePinPassword ->
                if (isMovePinPassword)
                    viewEffects.emit(InputViewEffect.MovePinPassword)
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            settingsRepository.currentConfirmPin.save(false)
        }

        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

        settingsRepository.observeDate()
            .onEach { date ->
                val dateFormat = dateFormatter.format(date)
                viewState.value = viewState.value
                    .copy(date = dateFormat)
            }.launchIn(viewModelScope)

    }

    init {

        categoryRepository.observeCategoriesExpense
            .onEach { categoriesExpense ->
                val editCategory = Category("e", context.getString(R.string.edit), 0)
                val categoriesExpenseWithEdit = categoriesExpense.toMutableList() + editCategory

                viewState.value = viewState.value
                    .copy(allCategoryExpense = categoriesExpenseWithEdit)

            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)

        categoryRepository.observeCategoriesIncome
            .onEach { categoriesIncome ->
                val editCategory = Category("i", context.getString(R.string.edit), 0)
                val categoriesIncomeWithEdit = categoriesIncome.toMutableList() + editCategory

                viewState.value = viewState.value
                    .copy(allCategoryIncome = categoriesIncomeWithEdit)

            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)

        transactionRepository.allTransactions
            .onEach { allTransactions ->
                viewState.value = viewState.value
                    .copy(allTransactions = allTransactions)

                viewState.value.transaction?.let { transaction ->
                    if (transaction.value > 0)
                        typeTransactionChanged(TypeTransaction.Income)
                    else
                        typeTransactionChanged(TypeTransaction.Expense)
                    transaction.value = abs(transaction.value)
                    valueTransactionChanged(transaction.value.toString())
                    noteTransactionChanged(transaction.note)
                    setPhotoPath(transaction.photo.toMutableList())

                    val position =
                        viewState.value.currentCategory
                            .indexOfFirst { it.name == transaction.nameCategory }

                    if (viewState.value.selectedAdapter == TypeTransaction.Expense)
                        selectExpense(position)
                    else
                        selectIncome(position)
                }
            }.launchIn(viewModelScope)

    }

    fun onEvent(event: InputEvent) {
        when (event) {
            InputEvent.BackButtonClicked -> backButtonClicked()
            InputEvent.RemoveTransactionButtonClicked -> removeTransactionButtonClicked()
            InputEvent.AddPhotoPathButtonClicked -> addPhotoPathButtonClicked()
            is InputEvent.Init -> init(event.transactionId)
            is InputEvent.TypeTransactionChanged -> typeTransactionChanged(event.typeTransactions)
            is InputEvent.DataTextClicked -> dataTextClicked()
            is InputEvent.LeftDataIconClicked -> leftDataIconClicked()
            is InputEvent.RightDataIconClicked -> rightDataIconClicked()
            is InputEvent.SubmitButtonClicked -> submitButtonClicked()
            is InputEvent.NoteTransactionChanged -> noteTransactionChanged(event.value)
            is InputEvent.ValueTransactionChanged -> valueTransactionChanged(event.value)
            is InputEvent.RemovePhotoButtonClicked -> removePhotoButtonClicked(event.numberPhoto)
            is InputEvent.CategoryItemCardClicked -> categoryItemCardClicked(event.position)
            is InputEvent.UpdateCurrentPhotoPath -> updateCurrentPhotoPath(event.absolutePath)
        }
    }

    private fun init(transactionId: Int) {
        viewState.value = viewState.value
            .copy(transactionId = transactionId)
    }

    private fun typeTransactionChanged(typeTransactions: TypeTransaction) {
        if (viewState.value.selectedAdapter != typeTransactions)
            viewState.value = viewState.value
                .copy(
                    allCategoryExpense = viewState.value.allCategoryExpense.map { it.copy(selected = false) },
                    allCategoryIncome = viewState.value.allCategoryIncome.map { it.copy(selected = false) },
                    selectedAdapter = typeTransactions
                )
    }

    private fun rightDataIconClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            val date = settingsRepository.getDate()
            val newDate = date.plusDays(1)
            settingsRepository.setDate(newDate)
        }
    }

    private fun leftDataIconClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            val date = settingsRepository.getDate()
            val newDate = date.minusDays(1)
            settingsRepository.setDate(newDate)
        }
    }

    private fun removeTransactionButtonClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            viewState.value.transaction?.let { transactionRepository.delete(it) }
            viewEffects.emit(InputViewEffect.MoveReturn)
        }
    }

    private suspend fun getTransactionItemForUpdate(selectedCategory: Category): TransactionEntity? {
        val state = viewState.value
        val valueTransaction = state.value.toDouble() *
                if (viewState.value.selectedAdapter == TypeTransaction.Expense)
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
        val state = viewState.value
        val valueTransaction = state.value.toDouble() *
                if (viewState.value.selectedAdapter == TypeTransaction.Expense)
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

    private fun valueTransactionChanged(text: String) {
        viewState.value = viewState.value
            .copy(value = text)
    }

    private fun setPhotoPath(list: MutableList<String?>) {
        val newPhotoPaths = if (list[0] == "")
            emptyList<String?>().toMutableList()
        else
            list

        viewState.value = viewState.value
            .copy(photoPaths = newPhotoPaths)
    }

    private fun addPhotoPathButtonClicked() {
        val photoPaths = viewState.value.photoPaths.toMutableList()
        photoPaths.add(viewState.value.currentPhotoPath)

        viewState.value = viewState.value
            .copy(photoPaths = photoPaths)
    }

    private fun removePhotoButtonClicked(number: Int) {
        viewState.value.photoPaths[number - 1]?.let {
            val fDelete = File(it)
            if (fDelete.exists()) {
                fDelete.delete()
            }
        }

        val photoPaths = viewState.value.photoPaths.toMutableList()
        photoPaths.removeAt(number - 1)

        viewState.value = viewState.value
            .copy(photoPaths = photoPaths)
    }

    private fun noteTransactionChanged(newNote: String) {
        viewState.value = viewState.value
            .copy(note = newNote)
    }

    fun checkCameraHardware(context: Context): Boolean {
        if (viewState.value.photoPaths.size == InputFragment.MAX_PHOTO)
            return false
        if (viewState.value.cashCheckCameraHardware == null)
            viewState.value = viewState.value
                .copy(
                    cashCheckCameraHardware =
                    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                )
        return viewState.value.cashCheckCameraHardware ?: false
    }

    private fun categoryItemCardClicked(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            if (viewState.value.isClickEditPosition(position)) {
                viewState.update { inputUiState ->
                    inputUiState.copy(
                        allCategoryExpense = inputUiState.allCategoryExpense.map { it.copy(selected = false) },
                        allCategoryIncome = inputUiState.allCategoryIncome.map { it.copy(selected = false) },
                    )
                }
                viewEffects.emit(InputViewEffect.MoveEdit)
                return@launch
            }
            if (viewState.value.selectedAdapter == TypeTransaction.Expense) {
                selectExpense(position)
            } else {
                selectIncome(position)
            }
        }
    }

    private fun selectExpense(position: Int) {
        viewState.update { inputUiState ->
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
        viewState.update { inputUiState ->
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


    private fun submitButtonClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedCategoryItem =
                viewState.value.currentCategory
                    .first { it.selected }

            if (viewState.value.transaction != null) {
                val transaction =
                    getTransactionItemForUpdate(selectedCategoryItem) ?: return@launch

                transactionRepository.updateTransaction(transaction.toTransaction())

                viewEffects.emit(InputViewEffect.MoveReturn)
            } else {
                val lastItem = viewState.value.allTransactions.lastOrNull()
                val id = lastItem?.id
                val newId = id?.let { it + 1 } ?: 0
                val transaction =
                    getTransactionItemForNew(
                        selectedCategoryItem,
                        newId,
                        settingsRepository.getDate()
                    )
                transactionRepository.insertNewTransaction(transaction.toTransaction())

                viewState.update { inputUiState ->
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
        if (viewState.value.isInput) {
            Log.d("AAA", "123")
            viewState.value.photoPaths.forEach {
                it?.let {
                    val fDelete = File(it)
                    if (fDelete.exists()) {
                        fDelete.delete()
                    }
                }
            }
        }
    }

    private fun backButtonClicked() {
        viewModelScope.launch {
            viewEffects.emit(InputViewEffect.MoveReturn)
        }
    }

    private fun dataTextClicked() {
        viewModelScope.launch {
            viewEffects.emit(InputViewEffect.MoveCalendar)
        }
    }

    private fun updateCurrentPhotoPath(absolutePath: String) {
        viewState.value = viewState.value
            .copy(currentPhotoPath = absolutePath)
    }

}