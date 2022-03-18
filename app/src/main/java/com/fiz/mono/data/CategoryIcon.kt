package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.R

data class CategoryIcon(
    @DrawableRes
    val imgSrc: Int,
    var selected: Boolean = false
)

object CategoryIconItemDiff : DiffUtil.ItemCallback<CategoryIcon>() {
    override fun areItemsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem.imgSrc == newItem.imgSrc
    }

    override fun areContentsTheSame(
        oldItem: CategoryIcon,
        newItem: CategoryIcon
    ): Boolean {
        return oldItem == newItem
    }
}

val categoryIcons = mutableListOf(
    CategoryIcon(R.drawable.user),
    CategoryIcon(R.drawable.plane),
    CategoryIcon(R.drawable.chair),
    CategoryIcon(R.drawable.baby),
    CategoryIcon(R.drawable.bank),
    CategoryIcon(R.drawable.gym),
    CategoryIcon(R.drawable.cycles),
    CategoryIcon(R.drawable.bird),
    CategoryIcon(R.drawable.boat),
    CategoryIcon(R.drawable.books),
    CategoryIcon(R.drawable.brain),
    CategoryIcon(R.drawable.building),
    CategoryIcon(R.drawable.birthday),
    CategoryIcon(R.drawable.camera),
    CategoryIcon(R.drawable.car),
    CategoryIcon(R.drawable.cat),
    CategoryIcon(R.drawable.study),
    CategoryIcon(R.drawable.coffee),
    CategoryIcon(R.drawable.coin),
    CategoryIcon(R.drawable.pie),
    CategoryIcon(R.drawable.cook),
    CategoryIcon(R.drawable.coin),
    CategoryIcon(R.drawable.dog),
    CategoryIcon(R.drawable.facemask),
    CategoryIcon(R.drawable.medican),
    CategoryIcon(R.drawable.flower),
    CategoryIcon(R.drawable.dinner),
    CategoryIcon(R.drawable.gas),
    CategoryIcon(R.drawable.gift),
    CategoryIcon(R.drawable.bag),
    CategoryIcon(R.drawable.challenge),
    CategoryIcon(R.drawable.music),
    CategoryIcon(R.drawable.house),
    CategoryIcon(R.drawable.map),
    CategoryIcon(R.drawable.glass),
    CategoryIcon(R.drawable.money),
    CategoryIcon(R.drawable.package1),
    CategoryIcon(R.drawable.run),
    CategoryIcon(R.drawable.pill),
    CategoryIcon(R.drawable.food),
    CategoryIcon(R.drawable.`fun`),
    CategoryIcon(R.drawable.receipt),
    CategoryIcon(R.drawable.lawer),
    CategoryIcon(R.drawable.market),
    CategoryIcon(R.drawable.shower),
    CategoryIcon(R.drawable.football),
    CategoryIcon(R.drawable.store),
    CategoryIcon(R.drawable.study),
    CategoryIcon(R.drawable.tennis_ball),
    CategoryIcon(R.drawable.wc),
    CategoryIcon(R.drawable.train),
    CategoryIcon(R.drawable.cup),
    CategoryIcon(R.drawable.clothes),
    CategoryIcon(R.drawable.wallet),
    CategoryIcon(R.drawable.sea),
    CategoryIcon(R.drawable.party),
    CategoryIcon(R.drawable.fix)
)