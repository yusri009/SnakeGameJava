
import java.util.Scanner;

public class SnakeGame extends GamePanel {
    public static void main(String[] args) {
        System.out.println("--------------------------");
        System.out.println("Welcome to the Snake Game: ");
        System.out.println("--------------------------");
        System.out.println("Enter the Difficulty level(1, 2, 3, 4, 5): ");
        Scanner scanner = new Scanner(System.in);
        int difficultyLevel = scanner.nextInt();
        if (difficultyLevel == 1){
            GamePanel.setDELAY(120);
            GamePanel.setScoreIncrement(1);
            new GameFrame();
        }
        if (difficultyLevel == 2){
            GamePanel.setDELAY(100);
            GamePanel.setScoreIncrement(2);
            new GameFrame();
        }
        if (difficultyLevel == 3){
            GamePanel.setDELAY(80);
            GamePanel.setScoreIncrement(3);
            new GameFrame();
        }
        if (difficultyLevel == 4){
            GamePanel.setDELAY(60);
            GamePanel.setScoreIncrement(4);
            new GameFrame();
        }
        if (difficultyLevel == 5){
            GamePanel.setDELAY(40);
            GamePanel.setScoreIncrement(5);
            new GameFrame();
        }
        scanner.close();
        

    }
}
