package com.fiz.mono.data.data_source

import com.fiz.mono.R
import com.fiz.mono.data.entity.CategoryIcon
import com.fiz.mono.ui.models.CategoryIconUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CategoryIconUiStateDataSource {
    var allCategoryIcons: Flow<List<CategoryIconUiState>> = flow {
        emit(
            listOf(
                CategoryIcon("user", R.drawable.user).toCategoryIconUiState(),
                CategoryIcon("plane", R.drawable.plane).toCategoryIconUiState(),
                CategoryIcon("chair", R.drawable.chair).toCategoryIconUiState(),
                CategoryIcon("baby", R.drawable.baby).toCategoryIconUiState(),
                CategoryIcon("bank", R.drawable.bank).toCategoryIconUiState(),
                CategoryIcon("gym", R.drawable.gym).toCategoryIconUiState(),
                CategoryIcon("cycles", R.drawable.cycles).toCategoryIconUiState(),
                CategoryIcon("bird", R.drawable.bird).toCategoryIconUiState(),
                CategoryIcon("boat", R.drawable.boat).toCategoryIconUiState(),
                CategoryIcon("books", R.drawable.books).toCategoryIconUiState(),
                CategoryIcon("brain", R.drawable.brain).toCategoryIconUiState(),
                CategoryIcon("building", R.drawable.building).toCategoryIconUiState(),
                CategoryIcon("birthday", R.drawable.birthday).toCategoryIconUiState(),
                CategoryIcon("camera", R.drawable.camera).toCategoryIconUiState(),
                CategoryIcon("car", R.drawable.car).toCategoryIconUiState(),
                CategoryIcon("cat", R.drawable.cat).toCategoryIconUiState(),
                CategoryIcon("study", R.drawable.study).toCategoryIconUiState(),
                CategoryIcon("coffee", R.drawable.coffee).toCategoryIconUiState(),
                CategoryIcon("coin", R.drawable.coin).toCategoryIconUiState(),
                CategoryIcon("pie", R.drawable.pie).toCategoryIconUiState(),
                CategoryIcon("cook", R.drawable.cook).toCategoryIconUiState(),
                CategoryIcon("coin", R.drawable.coin).toCategoryIconUiState(),
                CategoryIcon("dog", R.drawable.dog).toCategoryIconUiState(),
                CategoryIcon("facemask", R.drawable.facemask).toCategoryIconUiState(),
                CategoryIcon("medican", R.drawable.medican).toCategoryIconUiState(),
                CategoryIcon("flower", R.drawable.flower).toCategoryIconUiState(),
                CategoryIcon("dinner", R.drawable.dinner).toCategoryIconUiState(),
                CategoryIcon("gas", R.drawable.gas).toCategoryIconUiState(),
                CategoryIcon("gift", R.drawable.gift).toCategoryIconUiState(),
                CategoryIcon("bag", R.drawable.bag).toCategoryIconUiState(),
                CategoryIcon("challenge", R.drawable.challenge).toCategoryIconUiState(),
                CategoryIcon("music", R.drawable.music).toCategoryIconUiState(),
                CategoryIcon("house", R.drawable.house).toCategoryIconUiState(),
                CategoryIcon("map", R.drawable.map).toCategoryIconUiState(),
                CategoryIcon("glass", R.drawable.glass).toCategoryIconUiState(),
                CategoryIcon("money", R.drawable.money).toCategoryIconUiState(),
                CategoryIcon("package1", R.drawable.package1).toCategoryIconUiState(),
                CategoryIcon("run", R.drawable.run).toCategoryIconUiState(),
                CategoryIcon("pill", R.drawable.pill).toCategoryIconUiState(),
                CategoryIcon("food", R.drawable.food).toCategoryIconUiState(),
                CategoryIcon("fun", R.drawable.`fun`).toCategoryIconUiState(),
                CategoryIcon("receipt", R.drawable.receipt).toCategoryIconUiState(),
                CategoryIcon("lawer", R.drawable.lawer).toCategoryIconUiState(),
                CategoryIcon("market", R.drawable.market).toCategoryIconUiState(),
                CategoryIcon("shower", R.drawable.shower).toCategoryIconUiState(),
                CategoryIcon("football", R.drawable.football).toCategoryIconUiState(),
                CategoryIcon("store", R.drawable.store).toCategoryIconUiState(),
                CategoryIcon("study", R.drawable.study).toCategoryIconUiState(),
                CategoryIcon("tennis_ball", R.drawable.tennis_ball).toCategoryIconUiState(),
                CategoryIcon("wc", R.drawable.wc).toCategoryIconUiState(),
                CategoryIcon("train", R.drawable.train).toCategoryIconUiState(),
                CategoryIcon("cup", R.drawable.cup).toCategoryIconUiState(),
                CategoryIcon("clothes", R.drawable.clothes).toCategoryIconUiState(),
                CategoryIcon("wallet", R.drawable.wallet).toCategoryIconUiState(),
                CategoryIcon("sea", R.drawable.sea).toCategoryIconUiState(),
                CategoryIcon("party", R.drawable.party).toCategoryIconUiState(),
                CategoryIcon("fix", R.drawable.fix).toCategoryIconUiState()
            )
        )
    }

    suspend fun getDrawableCategoryIcon(id: String): Int {
        return allCategoryIcons.first().first { it.id == id }.imgSrc
    }

    suspend fun getIDCategoryIcon(imgSrc: Int): String {
        return allCategoryIcons.first().first { it.imgSrc == imgSrc }.id
    }
}