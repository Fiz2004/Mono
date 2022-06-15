package com.fiz.mono.data.data_source

import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.data.entity.IconCategoryEntity
import com.fiz.mono.data.mapper.toCategoryIcon
import com.fiz.mono.domain.models.CategoryIcon

var mapIconsCategories: List<CategoryIcon> = listOf(
    IconCategoryEntity("user", R.drawable.user),
    IconCategoryEntity("plane", R.drawable.plane),
    IconCategoryEntity("chair", R.drawable.chair),
    IconCategoryEntity("baby", R.drawable.baby),
    IconCategoryEntity("bank", R.drawable.bank),
    IconCategoryEntity("gym", R.drawable.gym),
    IconCategoryEntity("cycles", R.drawable.cycles),
    IconCategoryEntity("bird", R.drawable.bird),
    IconCategoryEntity("boat", R.drawable.boat),
    IconCategoryEntity("books", R.drawable.books),
    IconCategoryEntity("brain", R.drawable.brain),
    IconCategoryEntity("building", R.drawable.building),
    IconCategoryEntity("birthday", R.drawable.birthday),
    IconCategoryEntity("camera", R.drawable.camera),
    IconCategoryEntity("car", R.drawable.car),
    IconCategoryEntity("cat", R.drawable.cat),
    IconCategoryEntity("study", R.drawable.study),
    IconCategoryEntity("coffee", R.drawable.coffee),
    IconCategoryEntity("coin", R.drawable.coin),
    IconCategoryEntity("pie", R.drawable.pie),
    IconCategoryEntity("cook", R.drawable.cook),
    IconCategoryEntity("coin", R.drawable.coin),
    IconCategoryEntity("dog", R.drawable.dog),
    IconCategoryEntity("facemask", R.drawable.facemask),
    IconCategoryEntity("medican", R.drawable.medican),
    IconCategoryEntity("flower", R.drawable.flower),
    IconCategoryEntity("dinner", R.drawable.dinner),
    IconCategoryEntity("gas", R.drawable.gas),
    IconCategoryEntity("gift", R.drawable.gift),
    IconCategoryEntity("bag", R.drawable.bag),
    IconCategoryEntity("challenge", R.drawable.challenge),
    IconCategoryEntity("music", R.drawable.music),
    IconCategoryEntity("house", R.drawable.house),
    IconCategoryEntity("map", R.drawable.map),
    IconCategoryEntity("glass", R.drawable.glass),
    IconCategoryEntity("money", R.drawable.money),
    IconCategoryEntity("package1", R.drawable.package1),
    IconCategoryEntity("run", R.drawable.run),
    IconCategoryEntity("pill", R.drawable.pill),
    IconCategoryEntity("food", R.drawable.food),
    IconCategoryEntity("fun", R.drawable.`fun`),
    IconCategoryEntity("receipt", R.drawable.receipt),
    IconCategoryEntity("lawer", R.drawable.lawer),
    IconCategoryEntity("market", R.drawable.market),
    IconCategoryEntity("shower", R.drawable.shower),
    IconCategoryEntity("football", R.drawable.football),
    IconCategoryEntity("store", R.drawable.store),
    IconCategoryEntity("study", R.drawable.study),
    IconCategoryEntity("tennis_ball", R.drawable.tennis_ball),
    IconCategoryEntity("wc", R.drawable.wc),
    IconCategoryEntity("train", R.drawable.train),
    IconCategoryEntity("cup", R.drawable.cup),
    IconCategoryEntity("clothes", R.drawable.clothes),
    IconCategoryEntity("wallet", R.drawable.wallet),
    IconCategoryEntity("sea", R.drawable.sea),
    IconCategoryEntity("party", R.drawable.party),
    IconCategoryEntity("fix", R.drawable.fix)
).map { it.toCategoryIcon() }

fun getResourceDrawableByIdIconsCategories(id: String): Int {
    return mapIconsCategories.first { it.id == id }.imgSrc
}

fun getIdIconsCategoriesByResourceDrawable(imgSrc: Int): String {
    return mapIconsCategories.first { it.imgSrc == imgSrc }.id
}
