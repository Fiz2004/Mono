package com.fiz.mono.report.domain

import android.os.Parcel
import android.os.Parcelable
import com.fiz.mono.domain.use_case.*
import javax.inject.Inject

class ReportUseCase @Inject constructor(
    val getCurrentBalanceUseCase: GetCurrentBalanceUseCase,
    val getCurrentIncomeUseCase: GetCurrentIncomeUseCase,
    val getCurrentExpenseUseCase: GetCurrentExpenseUseCase,
    val getBalanceForMonthUseCase: GetBalanceForMonthUseCase,
    val getLastBalanceForMonthUseCase: GetLastBalanceForMonthUseCase,
    val observeAllTransactionsUseCase: ObserveAllTransactionsUseCase
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("getCurrentBalanceUseCase"),
        TODO("getCurrentIncomeUseCase"),
        TODO("getCurrentExpenseUseCase"),
        TODO("getBalanceForMonthUseCase"),
        TODO("getLastBalanceForMonthUseCase"),
        TODO("observeAllTransactionsUseCase")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportUseCase> {
        override fun createFromParcel(parcel: Parcel): ReportUseCase {
            return ReportUseCase(parcel)
        }

        override fun newArray(size: Int): Array<ReportUseCase?> {
            return arrayOfNulls(size)
        }
    }
}