package com.olubunmialegbeleye.sudoku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameRepository: GameRepository
): ViewModel() {

    val sudokuNumbers = gameRepository.sudokuFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MutableList(9) {MutableList<Int>(9) { 0} }
        )

    val userProvided = gameRepository.userProvidedCellsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MutableList(9) {MutableList<Boolean>(9) { false} }
        )



    fun resetGame() {
        viewModelScope.launch { gameRepository.clearData() }
    }

    fun updateCellValue(coordinates: Pair<Int, Int>, value: Int) {
        viewModelScope.launch {
            gameRepository.updateSudoku(coordinates.first, coordinates.second, value)
        }
    }

    fun updateUserProvidedCells(coordinates: Pair<Int, Int>, provided: Boolean) {
        viewModelScope.launch {
            gameRepository.updateUserProvidedCells(coordinates.first, coordinates.second, provided)
        }
    }

    fun solveSudoku() {
        viewModelScope.launch { gameRepository.solveSudoku() }
    }
}