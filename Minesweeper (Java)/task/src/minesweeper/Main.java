package minesweeper;

import java.util.*;
import static java.util.stream.IntStream.range;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Prompt user for difficulty level
        System.out.println("Select difficulty level: easy, medium, hard");
        String level = sc.nextLine().toLowerCase();

        int mineCount;
        int rows = 9, cols = 9; // Default board size (9x9)

        // Set number of mines based on difficulty level
        mineCount = switch (level) {
            case "easy" -> 10;
            case "medium" -> 20;
            case "hard" -> 30;
            default -> {
                System.out.println("Invalid level, setting to easy.");
                yield 10;
            }
        };

        // Initialise the game with chosen settings
        MineSweeper mineSweeper = new MineSweeper(mineCount, rows, cols);
        mineSweeper.playGame();
    }

}
