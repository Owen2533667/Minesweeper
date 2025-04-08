package minesweeper;

import java.util.Arrays;
import java.util.Scanner;

import static java.util.stream.IntStream.range;

public class MineSweeper {
    int mines; // Total number of mines
    Character[][] field; // Actual game field with mines and numbers
    Character[][] displayField; // Player-visible field
    boolean[][] explored; // Tracks visited cells during exploration
    boolean firstMove; // Ensures mines are not placed on the first move
    int rows, cols; // Dimensions of the field
    long startTime; // Start time for tracking duration


    /**
     * Constructor to initialise the game field and other state.
     *
     * @param mines  the total number of mines to place on the field
     * @param x      the number of rows in the field
     * @param y      the number of columns in the field
     */
    public MineSweeper(int mines, int x, int y) {
        this.mines = mines;
        this.rows = x;
        this.cols = y;
        this.field = new Character[x][y];
        this.displayField = new Character[x][y];
        this.explored = new boolean[x][y];
        this.firstMove = true;
        initField(); // Prepare actual field with '.'
        initDisplayField(); // Prepare display field for the user
    }

    /**
     * Initialises the actual game field with '.' characters.
     *
     * <p>This method is called when the game is first created and when the player
     * restarts the game. It ensures that the field is always reset to its initial
     * state.</p>
     */
    private void initField() {
        range(0, field.length).forEach(i -> Arrays.fill(field[i], '.'));
    }

    // Place mines randomly on the board, avoiding the first clicked cell
    private void placeMines(int avoidX, int avoidY) {
        int minesPlaced = 0;
        while (minesPlaced < mines) {
            int x = (int) (Math.random() * field.length);
            int y = (int) (Math.random() * field[0].length);
            if (field[x][y] != 'X' && (x != avoidX || y != avoidY)) {
                field[x][y] = 'X';
                minesPlaced++;
            }
        }
    }

    /**
     * For each non-mine cell, count surrounding mines and label it.
     * <p>This method is called after the mines have been placed on the board.
     * It counts the number of mines adjacent to each non-mine cell and labels
     * the cell with that number. Cells with no adjacent mines are left blank.</p>
     */
    private void countMinesAroundCells() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j] != 'X') {
                    int count = countMinesAroundCell(i, j);
                    if (count > 0) {
                        field[i][j] = (char) (count + '0');
                    } else {
                        field[i][j] = '.'; // No surrounding mines
                    }
                }
            }
        }
    }

    // Count how many mines are adjacent to a given cell
    private int countMinesAroundCell(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < field.length && newY >= 0 && newY < field[0].length) {
                    if (field[newX][newY] == 'X') {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // Initialise displayField with '.' for all cells
    private void initDisplayField() {
        for (Character[] characters : displayField) {
            Arrays.fill(characters, '.');
        }
    }

    // Render the player's view of the field, optionally showing all mines
    public void printField(boolean revealMines) {
        System.out.println("\n |123456789|");
        System.out.println("-|---------|");
        for (int i = 0; i < displayField.length; i++) {
            System.out.print((i + 1) + "|");
            for (int j = 0; j < displayField[0].length; j++) {
                if (revealMines && field[i][j] == 'X') {
                    System.out.print('X');
                } else {
                    System.out.print(displayField[i][j]);
                }
            }
            System.out.println("|");
        }
        System.out.println("-|---------|");
    }

    // Main loop for the game logic
    public void playGame() {
        printField(false);
        Scanner sc = new Scanner(System.in);
        startTime = System.currentTimeMillis(); // Start the timer

        while (true) {
            System.out.println("\nEnter coordinates (column row) and command (mine/free):");
            int y = sc.nextInt() - 1; // Convert to 0-based index
            int x = sc.nextInt() - 1;
            String command = sc.next();

            // On first move, place mines avoiding the clicked cell
            if (firstMove && command.equals("free")) {
                initField();
                placeMines(x, y);
                countMinesAroundCells();
                firstMove = false;
            }

            // Mark or unmark a cell as a mine
            if (command.equals("mine")) {
                if (displayField[x][y] == '*') {
                    displayField[x][y] = '.'; // Unmark
                } else if (displayField[x][y] == '.') {
                    displayField[x][y] = '*'; // Mark
                }
            }
            // Reveal a cell
            else if (command.equals("free")) {
                if (field[x][y] == 'X') {
                    printField(true); // Show all mines
                    System.out.println("\nYou stepped on a mine and failed!");
                    break;
                } else {
                    explore(x, y); // Explore safe area
                }
            }

            printField(false); // Refresh the display

            // Check if all conditions for a win are met
            if (hasWon()) {
                long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
                System.out.println("\nCongratulations! You found all the mines!");
                System.out.println("Time taken: " + timeTaken + " seconds.");
                break;
            }
        }
    }

    // Recursively explore blank cells and reveal them
    private void explore(int x, int y) {
        if (x < 0 || x >= rows || y < 0 || y >= cols || explored[x][y]) {
            return; // Ignore invalid or already visited cells
        }

        if (displayField[x][y] == '*') {
            displayField[x][y] = '.'; // Unmark incorrect flag
        }

        explored[x][y] = true; // Mark this cell as visited

        if (field[x][y] == '.') {
            displayField[x][y] = '/'; // Show as blank explored
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    explore(x + i, y + j); // Recurse to neighbors
                }
            }
        } else {
            displayField[x][y] = field[x][y]; // Reveal number
        }
    }

    // Determine if the player has won
    private boolean hasWon() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Win condition: all mines are flagged and all other cells revealed
                if (field[i][j] == 'X' && displayField[i][j] != '*') {
                    return false;
                }
                if (field[i][j] != 'X' && (displayField[i][j] == '*' || displayField[i][j] == '.')) {
                    return false;
                }
            }
        }
        return true;
    }
}