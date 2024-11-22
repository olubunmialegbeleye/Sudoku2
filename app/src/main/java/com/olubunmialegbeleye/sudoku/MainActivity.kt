package com.olubunmialegbeleye.sudoku

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.olubunmialegbeleye.sudoku.databinding.ActivityMainBinding
import com.olubunmialegbeleye.sudoku.databinding.ButtonBinding
import com.olubunmialegbeleye.sudoku.databinding.MinorGridBinding
import com.olubunmialegbeleye.sudoku.databinding.TextBoxBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val textBoxIds = MutableList(9) {MutableList(9) { 0} }

    private var focusedTextBoxCoord: Pair<Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setUpToolBar()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.sudokuNumbers.collect { numbers ->
                    numbers.forEachIndexed { i, ints ->
                        ints.forEachIndexed { j, number ->
                            val textView: TextView = findViewById(textBoxIds[i][j])
                            textView.text = if (number > 0) {
                                number.toString()
                            } else {
                                ""
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.userProvided.collect { provided ->
                    textBoxIds.forEachIndexed { rowIndex, column ->
                        column.forEachIndexed { columnIndex, viewId ->
                            val textView: TextView = findViewById(viewId)
                            if (provided[rowIndex][columnIndex]) {
                                textView.setTextColor(com.google.android.material.R.attr.colorOnPrimary)
                            } else {
                                textView.setTextColor(resources.getColor(R.color.colorSecondaryDark, theme))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setUpGrid()
        binding.solve.setOnClickListener { mainViewModel.solveSudoku() }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_refresh -> {
                mainViewModel.resetGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"

        fun start(context: Context) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
            )
        }
    }

    private fun setUpGrid() {
        for (i in 0..8) {
            val minorGrid = MinorGridBinding.inflate(layoutInflater)
            for (j in 0..8) {
                val x = ((i/3)*3) + (j/3)
                val y = ((i%3)*3) + (j%3)
                val id_param = View.generateViewId()
                val textBox = TextBoxBinding.inflate(layoutInflater)
                (textBox.root as TextView).apply {
                    id = id_param
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 100
                        height = 100
                        setMargins(1, 1, 1, 1)
                    }
                }

                textBox.root.setOnClickListener {
                    focusedTextBoxCoord = Pair(x,y)
                }

                (minorGrid.root as GridLayout).apply {
                    addView(textBox.root)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = GridLayout.LayoutParams.WRAP_CONTENT
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        setMargins(4, 4, 4, 4)
                    }
                }

                textBoxIds[x][y] = id_param

            }
            binding.mainGrid.addView(minorGrid.root)
        }

        for (i in 1 .. 9) {
            val button = ButtonBinding.inflate(layoutInflater)
            (button.root as MaterialButton).apply {
                text = i.toString()
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 150
                    height = 150
                    setMargins(8, 8, 8, 8)
                }
                setOnClickListener {
                    focusedTextBoxCoord?.let {
                        mainViewModel.updateCellValue(it, i)
                        mainViewModel.updateUserProvidedCells(it, true)
                    }
                }
            }
            binding.buttonsLayout.addView(button.root)
        }

        val cancelButton = ImageButton(this).apply {
            setImageResource(R.drawable.clear)
            setBackgroundResource(R.drawable.numbers_bg)
            layoutParams = GridLayout.LayoutParams().apply {
                width = 150
                height = 150
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener {
                focusedTextBoxCoord?.let {
                    mainViewModel.updateCellValue(it, 0)
                }
            }
        }
        binding.buttonsLayout.addView(cancelButton)

    }
}