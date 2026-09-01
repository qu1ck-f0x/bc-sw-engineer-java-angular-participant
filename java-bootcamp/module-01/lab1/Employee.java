public class Employee {
    private int id;
    private String name;

    public Employee(int id, String name) {
        // TODO: assign id and name to this object's fields
        this.id = id;
        this.name = name;
    }

    public void display() {
        // TODO: print id + " - " + name
        System.out.println(id + " - " + name);
    }

    public static void main(String[] args) {
        // TODO: create Employee(101, "Aman") and call display()
        Employee e1 = new Employee(101, "Aman");
        e1.display();
    }
}
