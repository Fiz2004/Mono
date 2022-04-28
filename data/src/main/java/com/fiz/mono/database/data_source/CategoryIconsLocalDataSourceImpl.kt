package com.fiz.mono.database.data_source

import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.database.entity.CategoryIconEntity
import com.fiz.mono.database.mapper.toCategoryIcon
import com.fiz.mono.domain.models.CategoryIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CategoryIconsLocalDataSourceImpl {
    var allCategoryIcons: Flow<List<CategoryIcon>> = flow {
        emit(
            listOf(
                CategoryIconEntity("user", R.drawable.user),
                CategoryIconEntity("plane", R.drawable.plane),
                CategoryIconEntity("chair", R.drawable.chair),
                CategoryIconEntity("baby", R.drawable.baby),
                CategoryIconEntity("bank", R.drawable.bank),
                CategoryIconEntity("gym", R.drawable.gym),
                CategoryIconEntity("cycles", R.drawable.cycles),
                CategoryIconEntity("bird", R.drawable.bird),
                CategoryIconEntity("boat", R.drawable.boat),
                CategoryIconEntity("books", R.drawable.books),
                CategoryIconEntity("brain", R.drawable.brain),
                CategoryIconEntity("building", R.drawable.building),
                CategoryIconEntity("birthday", R.drawable.birthday),
                CategoryIconEntity("camera", R.drawable.camera),
                CategoryIconEntity("car", R.drawable.car),
                CategoryIconEntity("cat", R.drawable.cat),
                CategoryIconEntity("study", R.drawable.study),
                CategoryIconEntity("coffee", R.drawable.coffee),
                CategoryIconEntity("coin", R.drawable.coin),
                CategoryIconEntity("pie", R.drawable.pie),
                CategoryIconEntity("cook", R.drawable.cook),
                CategoryIconEntity("coin", R.drawable.coin),
                CategoryIconEntity("dog", R.drawable.dog),
                CategoryIconEntity("facemask", R.drawable.facemask),
                CategoryIconEntity("medican", R.drawable.medican),
                CategoryIconEntity("flower", R.drawable.flower),
                CategoryIconEntity("dinner", R.drawable.dinner),
                CategoryIconEntity("gas", R.drawable.gas),
                CategoryIconEntity("gift", R.drawable.gift),
                CategoryIconEntity("bag", R.drawable.bag),
                CategoryIconEntity("challenge", R.drawable.challenge),
                CategoryIconEntity("music", R.drawable.music),
                CategoryIconEntity("house", R.drawable.house),
                CategoryIconEntity("map", R.drawable.map),
                CategoryIconEntity("glass", R.drawable.glass),
                CategoryIconEntity("money", R.drawable.money),
                CategoryIconEntity("package1", R.drawable.package1),
                CategoryIconEntity("run", R.drawable.run),
                CategoryIconEntity("pill", R.drawable.pill),
                CategoryIconEntity("food", R.drawable.food),
                CategoryIconEntity("fun", R.drawable.`fun`),
                CategoryIconEntity("receipt", R.drawable.receipt),
                CategoryIconEntity("lawer", R.drawable.lawer),
                CategoryIconEntity("market", R.drawable.market),
                CategoryIconEntity("shower", R.drawable.shower),
                CategoryIconEntity("football", R.drawable.football),
                CategoryIconEntity("store", R.drawable.store),
                CategoryIconEntity("study", R.drawable.study),
                CategoryIconEntity("tennis_ball", R.drawable.tennis_ball),
                CategoryIconEntity("wc", R.drawable.wc),
                CategoryIconEntity("train", R.drawable.train),
                CategoryIconEntity("cup", R.drawable.cup),
                CategoryIconEntity("clothes", R.drawable.clothes),
                CategoryIconEntity("wallet", R.drawable.wallet),
                CategoryIconEntity("sea", R.drawable.sea),
                CategoryIconEntity("party", R.drawable.party),
                CategoryIconEntity("fix", R.drawable.fix)
            ).map { it.toCategoryIcon() }
        )
    }

    suspend fun getDrawableCategoryIcon(id: String): Int {
        return allCategoryIcons.first().first { it.id == id }.imgSrc
    }

    suspend fun getIDCategoryIcon(imgSrc: Int): String {
        return allCategoryIcons.first().first { it.imgSrc == imgSrc }.id
    }
}