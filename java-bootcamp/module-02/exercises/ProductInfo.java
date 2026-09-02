import java.util.Scanner;

public class ProductInfo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Product name: ");
        // TODO: read product name (may include spaces)
        String name = scanner.nextLine();

        System.out.print("Quantity: ");
        // TODO: read a full line, then parse to int (Integer.parseInt)
        int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Price: ");
        // TODO: read a full line, then parse to double (Double.parseDouble)
        double price = Double.parseDouble(scanner.nextLine());

        // TODO: print with printf — %s name, %d qty, %.2f price
        System.out.printf("Product: %s | Qty: %d | Price: %.2f%n", name, qty, price);

        scanner.close();
    }
}
