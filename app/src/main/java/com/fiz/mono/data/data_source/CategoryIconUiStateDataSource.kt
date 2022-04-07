package com.fiz.mono.data.data_source

import com.fiz.mono.R
import com.fiz.mono.data.entity.CategoryIconEntity
import com.fiz.mono.ui.models.CategoryIconUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CategoryIconUiStateDataSource {
    var allCategoryIcons: Flow<List<CategoryIconUiState>> = flow {
        emit(
            listOf(
                CategoryIconEntity("user", R.drawable.user).toCategoryIconUiState(),
                CategoryIconEntity("plane", R.drawable.plane).toCategoryIconUiState(),
                CategoryIconEntity("chair", R.drawable.chair).toCategoryIconUiState(),
                CategoryIconEntity("baby", R.drawable.baby).toCategoryIconUiState(),
                CategoryIconEntity("bank", R.drawable.bank).toCategoryIconUiState(),
                CategoryIconEntity("gym", R.drawable.gym).toCategoryIconUiState(),
                CategoryIconEntity("cycles", R.drawable.cycles).toCategoryIconUiState(),
                CategoryIconEntity("bird", R.drawable.bird).toCategoryIconUiState(),
                CategoryIconEntity("boat", R.drawable.boat).toCategoryIconUiState(),
                CategoryIconEntity("books", R.drawable.books).toCategoryIconUiState(),
                CategoryIconEntity("brain", R.drawable.brain).toCategoryIconUiState(),
                CategoryIconEntity("building", R.drawable.building).toCategoryIconUiState(),
                CategoryIconEntity("birthday", R.drawable.birthday).toCategoryIconUiState(),
                CategoryIconEntity("camera", R.drawable.camera).toCategoryIconUiState(),
                CategoryIconEntity("car", R.drawable.car).toCategoryIconUiState(),
                CategoryIconEntity("cat", R.drawable.cat).toCategoryIconUiState(),
                CategoryIconEntity("study", R.drawable.study).toCategoryIconUiState(),
                CategoryIconEntity("coffee", R.drawable.coffee).toCategoryIconUiState(),
                CategoryIconEntity("coin", R.drawable.coin).toCategoryIconUiState(),
                CategoryIconEntity("pie", R.drawable.pie).toCategoryIconUiState(),
                CategoryIconEntity("cook", R.drawable.cook).toCategoryIconUiState(),
                CategoryIconEntity("coin", R.drawable.coin).toCategoryIconUiState(),
                CategoryIconEntity("dog", R.drawable.dog).toCategoryIconUiState(),
                CategoryIconEntity("facemask", R.drawable.facemask).toCategoryIconUiState(),
                CategoryIconEntity("medican", R.drawable.medican).toCategoryIconUiState(),
                CategoryIconEntity("flower", R.drawable.flower).toCategoryIconUiState(),
                CategoryIconEntity("dinner", R.drawable.dinner).toCategoryIconUiState(),
                CategoryIconEntity("gas", R.drawable.gas).toCategoryIconUiState(),
                CategoryIconEntity("gift", R.drawable.gift).toCategoryIconUiState(),
                CategoryIconEntity("bag", R.drawable.bag).toCategoryIconUiState(),
                CategoryIconEntity("challenge", R.drawable.challenge).toCategoryIconUiState(),
                CategoryIconEntity("music", R.drawable.music).toCategoryIconUiState(),
                CategoryIconEntity("house", R.drawable.house).toCategoryIconUiState(),
                CategoryIconEntity("map", R.drawable.map).toCategoryIconUiState(),
                CategoryIconEntity("glass", R.drawable.glass).toCategoryIconUiState(),
                CategoryIconEntity("money", R.drawable.money).toCategoryIconUiState(),
                CategoryIconEntity("package1", R.drawable.package1).toCategoryIconUiState(),
                CategoryIconEntity("run", R.drawable.run).toCategoryIconUiState(),
                CategoryIconEntity("pill", R.drawable.pill).toCategoryIconUiState(),
                CategoryIconEntity("food", R.drawable.food).toCategoryIconUiState(),
                CategoryIconEntity("fun", R.drawable.`fun`).toCategoryIconUiState(),
                CategoryIconEntity("receipt", R.drawable.receipt).toCategoryIconUiState(),
                CategoryIconEntity("lawer", R.drawable.lawer).toCategoryIconUiState(),
                CategoryIconEntity("market", R.drawable.market).toCategoryIconUiState(),
                CategoryIconEntity("shower", R.drawable.shower).toCategoryIconUiState(),
                CategoryIconEntity("football", R.drawable.football).toCategoryIconUiState(),
                CategoryIconEntity("store", R.drawable.store).toCategoryIconUiState(),
                CategoryIconEntity("study", R.drawable.study).toCategoryIconUiState(),
                CategoryIconEntity("tennis_ball", R.drawable.tennis_ball).toCategoryIconUiState(),
                CategoryIconEntity("wc", R.drawable.wc).toCategoryIconUiState(),
                CategoryIconEntity("train", R.drawable.train).toCategoryIconUiState(),
                CategoryIconEntity("cup", R.drawable.cup).toCategoryIconUiState(),
                CategoryIconEntity("clothes", R.drawable.clothes).toCategoryIconUiState(),
                CategoryIconEntity("wallet", R.drawable.wallet).toCategoryIconUiState(),
                CategoryIconEntity("sea", R.drawable.sea).toCategoryIconUiState(),
                CategoryIconEntity("party", R.drawable.party).toCategoryIconUiState(),
                CategoryIconEntity("fix", R.drawable.fix).toCategoryIconUiState()
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