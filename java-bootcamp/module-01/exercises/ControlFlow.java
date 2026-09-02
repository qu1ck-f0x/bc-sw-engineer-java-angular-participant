public class ControlFlow {
    public static void main(String[] args) {
        int number = 4;
        // TODO: if / else — print "even" when number is even, "odd" otherwise
        if (number % 2 == 0) {
            System.out.println("even");
        } else {
            System.out.println("odd");
        }

        // TODO: for loop — print 1 through 5 (one number per line)
        for (int i = 1; i <= 5; i++) {
            System.out.println(i);
        }

        int count = 3;
        // TODO: while loop — countdown from 3 to 1 ("countdown " + count)
        for (int i = 3; i >= 1; i--) {
            System.out.println("countdown " + i);
        }

        int day = 2;
        // TODO: switch on day — 1 Monday, 2 Tuesday, default Other day
        switch (day) {
            case 1:
                System.out.println("Monday");
                break;

            case 2:
                System.out.println("Tuesday");
                break;
            default:
                System.out.println("Other day");
        }

    }
}
