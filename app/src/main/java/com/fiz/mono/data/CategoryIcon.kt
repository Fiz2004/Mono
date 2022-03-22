package com.fiz.mono.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.R

data class CategoryIcon(
    val id: String,
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
    CategoryIcon("user", R.drawable.user),
    CategoryIcon("plane", R.drawable.plane),
    CategoryIcon("chair", R.drawable.chair),
    CategoryIcon("baby", R.drawable.baby),
    CategoryIcon("bank", R.drawable.bank),
    CategoryIcon("gym", R.drawable.gym),
    CategoryIcon("cycles", R.drawable.cycles),
    CategoryIcon("bird", R.drawable.bird),
    CategoryIcon("boat", R.drawable.boat),
    CategoryIcon("books", R.drawable.books),
    CategoryIcon("brain", R.drawable.brain),
    CategoryIcon("building", R.drawable.building),
    CategoryIcon("birthday", R.drawable.birthday),
    CategoryIcon("camera", R.drawable.camera),
    CategoryIcon("car", R.drawable.car),
    CategoryIcon("cat", R.drawable.cat),
    CategoryIcon("study", R.drawable.study),
    CategoryIcon("coffee", R.drawable.coffee),
    CategoryIcon("coin", R.drawable.coin),
    CategoryIcon("pie", R.drawable.pie),
    CategoryIcon("cook", R.drawable.cook),
    CategoryIcon("coin", R.drawable.coin),
    CategoryIcon("dog", R.drawable.dog),
    CategoryIcon("facemask", R.drawable.facemask),
    CategoryIcon("medican", R.drawable.medican),
    CategoryIcon("flower", R.drawable.flower),
    CategoryIcon("dinner", R.drawable.dinner),
    CategoryIcon("gas", R.drawable.gas),
    CategoryIcon("gift", R.drawable.gift),
    CategoryIcon("bag", R.drawable.bag),
    CategoryIcon("challenge", R.drawable.challenge),
    CategoryIcon("music", R.drawable.music),
    CategoryIcon("house", R.drawable.house),
    CategoryIcon("map", R.drawable.map),
    CategoryIcon("glass", R.drawable.glass),
    CategoryIcon("money", R.drawable.money),
    CategoryIcon("package1", R.drawable.package1),
    CategoryIcon("run", R.drawable.run),
    CategoryIcon("pill", R.drawable.pill),
    CategoryIcon("food", R.drawable.food),
    CategoryIcon("fun", R.drawable.`fun`),
    CategoryIcon("receipt", R.drawable.receipt),
    CategoryIcon("lawer", R.drawable.lawer),
    CategoryIcon("market", R.drawable.market),
    CategoryIcon("shower", R.drawable.shower),
    CategoryIcon("football", R.drawable.football),
    CategoryIcon("store", R.drawable.store),
    CategoryIcon("study", R.drawable.study),
    CategoryIcon("tennis_ball", R.drawable.tennis_ball),
    CategoryIcon("wc", R.drawable.wc),
    CategoryIcon("train", R.drawable.train),
    CategoryIcon("cup", R.drawable.cup),
    CategoryIcon("clothes", R.drawable.clothes),
    CategoryIcon("wallet", R.drawable.wallet),
    CategoryIcon("sea", R.drawable.sea),
    CategoryIcon("party", R.drawable.party),
    CategoryIcon("fix", R.drawable.fix)
)

fun getDrawableCategoryIcon(id: String): Int {
    return categoryIcons.first { it.id == id }.imgSrc
}