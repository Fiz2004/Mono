package com.fiz.mono.ui.calendar

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.databinding.ItemCalendarBinding
import com.fiz.mono.databinding.ItemCalendarDayWeekBinding
import com.fiz.mono.util.themeColor

class CalendarAdapter(private val callback: (TransactionsDay) -> Unit) :
    ListAdapter<CalendarDataItem, RecyclerView.ViewHolder>(DataItemDiff) {

    private val ITEM_VIEW_TYPE_DAY_WEEK = 0
    private val ITEM_VIEW_TYPE_DAY = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_DAY_WEEK -> DayWeekItemViewHolder.from(parent)
            ITEM_VIEW_TYPE_DAY -> DayItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CalendarDataItem.DayWeekItem -> ITEM_VIEW_TYPE_DAY_WEEK
            is CalendarDataItem.DayItem -> ITEM_VIEW_TYPE_DAY
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayWeekItemViewHolder -> {
                val dayWeek = getItem(position) as CalendarDataItem.DayWeekItem
                holder.bind(dayWeek.dayWeek)
            }
            is DayItemViewHolder -> {
                val transactionsDay = getItem(position) as CalendarDataItem.DayItem
                holder.bind(transactionsDay.transactionsDay, callback)
            }
        }

    }
}

class DayWeekItemViewHolder(
    private var binding: ItemCalendarDayWeekBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(dayWeek: String) {
        binding.descriptionTextView.text = dayWeek
    }

    companion object {
        fun from(parent: ViewGroup): DayWeekItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemCalendarDayWeekBinding.inflate(layoutInflater, parent, false)
            return DayWeekItemViewHolder(view)
        }
    }
}

class DayItemViewHolder(
    private var binding: ItemCalendarBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(transactionsDay: TransactionsDay, callback: (TransactionsDay) -> Unit) {
        binding.apply {
            dayOfMonthTextView.text = transactionsDay.getFormatDayOfMonthOrBlank()
            expenseImageView.visibility = getVisibility(transactionsDay.expense)
            incomeImageView.visibility = getVisibility(transactionsDay.income)
            cardMaterialCard.backgroundTintList = getBackgroundTint(transactionsDay.today)
            cardMaterialCard.strokeWidth = getStrokeWidth(transactionsDay.selected)

            root.setOnClickListener { callback(transactionsDay) }
        }

    }

    private fun getVisibility(visibility: Boolean) =
        if (visibility) View.VISIBLE else View.GONE

    private fun getBackgroundTint(today: Boolean) =
        ColorStateList.valueOf(
            binding.root.context.run {
                if (today)
                    themeColor(R.attr.colorGray)
                else
                    themeColor(R.attr.colorBackground)
            }
        )

    private fun getStrokeWidth(selected: Boolean) =
        if (selected)
            2
        else
            0

    companion object {
        fun from(parent: ViewGroup): DayItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                ItemCalendarBinding.inflate(layoutInflater, parent, false)
            return DayItemViewHolder(view)
        }
    }
}