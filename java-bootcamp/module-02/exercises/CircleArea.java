import java.util.Scanner;

public class CircleArea {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // TODO: read radius as double
        double radius = scanner.nextDouble();
        // TODO: area = Math.PI * r * r; printf with decimals
        double area = 3.14 * radius * radius;
        System.out.printf("The area of the circle is %.2f", area);
    }
}
