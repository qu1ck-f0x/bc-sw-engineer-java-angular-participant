import java.util.Scanner;

public class BillSummary {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Product name: ");
        // TODO: read name
         String name = scanner.nextLine();

        System.out.print("Quantity: ");
        // TODO: read qty (nextLine + Integer.parseInt)
         int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Unit price: ");
        // TODO: read price (nextLine + Double.parseDouble)
         double price = Double.parseDouble(scanner.nextLine());

        // TODO: compute total (qty * price), 10% discount, and final amount
        double total = price * qty;
        double discount = total * 0.1;
        double finalPrice = total - discount;
        // TODO: print Product, Quantity, Unit price, Total, Discount (10%), Final amount
        // hints: %.2f for money; use 10%% in the format string to print a literal %
        System.out.printf("Discount (10%%): %.2f%n", discount);  // %% escapes the percent sign
        System.out.printf("Final price: %.2f%n", finalPrice);

        scanner.close();
    }
}
