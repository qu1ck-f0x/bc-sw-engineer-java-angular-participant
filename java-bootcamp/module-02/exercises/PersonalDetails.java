import java.util.Scanner;

public class PersonalDetails {
    public static void main(String[] args) {
        // TODO: Scanner scanner = new Scanner(System.in);
        // TODO: read name (nextLine), age (nextInt), consume leftover newline, city (nextLine)
        // TODO: printf greeting
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        int age = sc.nextInt();
        sc.nextLine();
        String city = sc.nextLine();

//        TODO: print a greeting with printf — %s for strings, %d for age, %n for newline
        System.out.printf("Hello, %s! You are %d years old and live in %s.%n",
                name, age, city);
    }
}
