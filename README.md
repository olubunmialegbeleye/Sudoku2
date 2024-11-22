# Sudoku Solver

A powerful and efficient Sudoku Solver application built with Kotlin, designed to solve any valid Sudoku puzzle. Whether you're a casual player or an enthusiast, this app provides instant solutions and helps you learn strategies for solving Sudoku puzzles.

## Features

- ✅ **Instant Puzzle Solving**: Input your puzzle and get a solution instantly.
- 📊 **Algorithm**: Uses a backtracking algorithm optimized for speed and accuracy.
- 🧩 **Customizable Input**: Input puzzles dynamically with a clean and intuitive interface.
- 🌙 **Theme Support**: Light and dark mode compatibility for a comfortable user experience.
- 📱 **Responsive UI**: Fully responsive, works seamlessly on devices of all sizes.

## Screenshots

### Landing Activity
![Landing Activity](assets/images/Landing_Activity.png)

### Main Activity
![Main Activity](assets/images/Main_Activity.png)

## Getting Started

### Prerequisites
- Android Studio (Arctic Fox or later)
- Kotlin 1.5 or higher
- A device or emulator running Android 6.0 (Marshmallow) or higher

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/olubunmialegbeleye/sudoku-solver.git

2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Run the app on your preferred emulator or device.

### How It Works
The app uses a **backtracking algorithm** to solve Sudoku puzzles. Here's a brief overview of the algorithm:

1. Start from the top-left cell.
2. Find an empty cell.
3. Place a number (1-9) in the cell and check if it’s valid:
    - No repetition in the current row, column, or 3x3 grid.
4. If valid, move to the next cell.
5. If invalid, backtrack to the previous cell and try the next number.
6. Repeat until the puzzle is solved or determined unsolvable.
For more details on the algorithm, check out the Sudoku Solver Algorithm.

### Contribution
Contributions are welcome! If you'd like to add features or improve the app:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/new-feature
3. Commit your changes and push the branch:
   ```bash
   git push origin feature/new-feature
4. Open a pull request.
