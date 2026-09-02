public class MethodsDemo {
    // TODO: method that takes an int and returns n * n
    public static int square(int n) {
        // return _____;
        return n * n;
    }

    // TODO: overload — same name, double parameter, return n * n as double
    public static double square(double n) {
        // return _____;
        return n * n;
    }

    public static void main(String[] args) {
        // TODO: call both overloads and print the results
         int intResult = square(4);
         double doubleResult = square(2.5);
         System.out.println("square(4) = " + intResult);
         System.out.println("square(2.5) = " + doubleResult);
    }
}
