package com.olubunmialegbeleye.sudoku

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


class GameRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val sudokuFlow: Flow<List<List<Int>>> = dataStore.data
        .map { preferences ->
            val sudokuString = preferences[PreferencesKeys.SUDOKU]
            return@map if (sudokuString != null) {
                Json.decodeFromString<List<List<Int>>>(sudokuString)
            } else {
                List(9) { List<Int>(9) { 0 } }
            }
        }

    val userProvidedCellsFlow: Flow<List<List<Boolean>>> = dataStore.data
        .map { preferences ->
            val sudokuString = preferences[PreferencesKeys.USER_INPUT]
            return@map if (sudokuString != null) {
                Json.decodeFromString<List<List<Boolean>>>(sudokuString)
            } else {
                List(9) {List<Boolean>(9) { false} }
            }
        }

    suspend fun updateUserProvidedCells(x: Int, y: Int, provided: Boolean) {
        val string = dataStore.data.first()[PreferencesKeys.USER_INPUT]
        val list: MutableList<MutableList<Boolean>> = (if (string != null) {
            Json.decodeFromString<MutableList<MutableList<Boolean>>>(string)
        } else {
            MutableList(9) {MutableList<Boolean>(9) { false} }
        })
        list[x][y] = provided
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_INPUT] = Json.encodeToString(list)
        }
    }

    suspend fun updateSudoku(x: Int, y: Int, number: Int) {
        val string = dataStore.data.map { preferences->
            preferences[PreferencesKeys.SUDOKU]
        }.first()
        val list: MutableList<MutableList<Int>> = (if (string != null) {
            Json.decodeFromString<MutableList<MutableList<Int>>>(string)
        } else {
            MutableList(9) {MutableList(9) { 0} }
        })

        if (number > 0 && !canPlaceNumberInCell(list, Pair(x,y), number)) return

        list[x][y] = number
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUDOKU] = Json.encodeToString(list)
        }
    }

    suspend fun updateSudoku(sudoku: List<List<Int>>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUDOKU] = Json.encodeToString(sudoku)
        }
    }

    suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getSudoku(): List<List<Int>> {
        val sudokuString = dataStore.data.first()[PreferencesKeys.SUDOKU]
        return if (sudokuString != null) {
            Json.decodeFromString<List<List<Int>>>(sudokuString)
        } else {
            List(9) { List<Int>(9) { 0 } }
        }
    }

    //Todo: In progress
    suspend fun updateCellValue(coordinates: Pair<Int, Int>, value: Int) {
        val sudoku = getSudoku().map { it.toMutableList() }
        sudoku[coordinates.first][coordinates.second] = value
    }

    private fun canPlaceNumberInCell(sudoku: List<List<Int>>, coordinate: Pair<Int, Int>, value: Int): Boolean {
        val (x, y) = coordinate
        val subGridX = (x/3)*3
        val subGridY = (y/3)*3
        sudoku.forEachIndexed { i, row ->
            if (i == x && row.contains(value)) return false
            if (row[y] == value) return false
            row.forEachIndexed { j, cell ->
                if (
                    subGridX <= i && i <= (subGridX+2)
                    && subGridY <= j && j <= (subGridY+2)
                    && cell == value) return false
            }
        }
        return true
    }

    suspend fun solveSudoku() {
        val sudoku = getSudoku()
        val solved = sudoku.map { it.toIntArray() }.toTypedArray()
        solveSudoku(solved, 0, 0)
        //val solved = solveSudoku(sudoku, 0, 0)
        //updateSudoku(solved)
        updateSudoku(solved.map { it.toList() })
    }

    private fun solveSudoku(sudoku: List<List<Int>>, row: Int, col: Int): List<List<Int>> {
        val _sudoku: MutableList<MutableList<Int>> = sudoku.map { it.toMutableList() }.toMutableList()
        var _row = row
        var _col = col
        val N = sudoku.size
        if (row == N - 1 && col == N) return _sudoku

        if (col == N) {
            _row++;
            _col = 0;
        }

        if (_sudoku[_row][_col] != 0) return solveSudoku(_sudoku, _row, _col + 1)

        for (num in 1 until 10) {
            if (canPlaceNumberInCell(_sudoku, Pair(_row, _col), num)) {
                _sudoku[_row][_col] = num
                break
            }
        }

        return _sudoku
    }

    suspend fun solveSudoku(grid: Array<IntArray>, row: Int, col: Int): Boolean {
        val N = 9
        // If we've reached the 8th row and 9th column, the Sudoku is solved
        if (row == N - 1 && col == N) return true

        // Move to the next row if column exceeds grid size
        if (col == N) return solveSudoku(grid, row + 1, 0)

        // If the cell is already filled, move to the next column
        if (grid[row][col] != 0) return solveSudoku(grid, row, col + 1)

        // Try placing numbers from 1 to 9
        for (num in 1..9) {
            if (canPlaceNumberInCell(grid.map { it.toList() }, Pair(row, col), num)) {
                // Place the number
                grid[row][col] = num
                updateUserProvidedCells(row, col, false)

                // Recursively try to solve the rest of the grid
                if (solveSudoku(grid, row, col + 1)) return true

                // Backtrack if placing the number didn't work
                grid[row][col] = 0
            }
        }
        return false
    }

}