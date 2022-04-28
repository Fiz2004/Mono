package com.fiz.mono.domain.use_case

import javax.inject.Inject

class ReportUseCase @Inject constructor(
    val getCurrentBalanceUseCase: GetCurrentBalanceUseCase,
    val getCurrentIncomeUseCase: GetCurrentIncomeUseCase,
    val getCurrentExpenseUseCase: GetCurrentExpenseUseCase,
    val getBalanceForMonthUseCase: GetBalanceForMonthUseCase,
    val getLastBalanceForMonthUseCase: GetLastBalanceForMonthUseCase,
    val observeAllTransactionsUseCase: ObserveAllTransactionsUseCase
)