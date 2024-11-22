package com.olubunmialegbeleye.sudoku

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val SUDOKU = stringPreferencesKey("sudoku")
    val USER_INPUT = stringPreferencesKey("user_input")
}