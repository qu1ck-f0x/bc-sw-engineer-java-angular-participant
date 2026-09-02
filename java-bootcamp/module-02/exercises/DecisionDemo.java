import java.util.Scanner;

public class DecisionDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Score (0-100): ");
        int score = Integer.parseInt(scanner.nextLine());

        // if / else if / else — print Grade: A/B/C/F
        //   A: score >= 90
        //   B: score >= 80
        //   C: score >= 70
        //   F: otherwise
        //   (branch order matters — first true wins)

        if (score >= 90) {
            System.out.println("A");
        } else if (score >= 80) {
            System.out.println("B");
        }  else if (score >= 70) {
            System.out.println("C");
        } else {
            System.out.println("F");
        }

        System.out.print("Day number (1-7): ");
        int day = Integer.parseInt(scanner.nextLine());

        // switch on day — print Monday..Sunday for 1..7
        //   use arrow form: case 1 -> System.out.println("Monday");
        //   default -> System.out.println("Not a valid day");
        switch (day) {
            case 1 -> System.out.println("Monday");
            case 2 -> System.out.println("Tuesday");
            case 3 -> System.out.println("Wednesday");
            case 4 -> System.out.println("Thursday");
            case 5 -> System.out.println("Friday");
            case 6 -> System.out.println("Saturday");
            case 7 -> System.out.println("Sunday");
            default -> System.out.println("Not a valid day");
        }

        scanner.close();
    }
}
