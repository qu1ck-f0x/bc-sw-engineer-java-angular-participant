import java.util.Scanner;

public class LoopsDemo {
    public static void main(String[] args) {
        // TODO: for loop demo (known count from input)
        System.out.println("Multiplication table for 5:");

        for (int i = 0; i <= 5; i++) {
            System.out.println(i*5);
        }
        // TODO: while loop demo
        int count = 3;
        while(count > 0) {
            System.out.println(count);
            count--;
        }
        // TODO: do-while demo (runs at least once)
        Scanner scanner = new Scanner(System.in);
        String choice;
         do {
             System.out.print("Type 'menu' to see it again, anything else to quit: ");
             choice = scanner.nextLine();
             if (choice.equals("menu")) {
                 System.out.println("1) Add  2) Withdraw  3) Exit");
             }
         } while (choice.equals("menu"));
    }
}
