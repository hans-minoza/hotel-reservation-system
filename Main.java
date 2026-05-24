import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        RoomManager roomManager = new RoomManager();
        BookingManager bookingManager = new BookingManager(roomManager);

        BookingForm bookingForm = new BookingForm(roomManager, bookingManager, scanner);
        CustomerMenu customerMenu = new CustomerMenu(bookingManager, bookingForm, scanner);
        StaffActions staffActions = new StaffActions(roomManager, bookingManager, scanner);
        StaffMenu staffMenu = new StaffMenu(staffActions, scanner);

        System.out.println("==============================");
        System.out.println("SHANGHAI HOTEL");
        System.out.println("Reservation System");
        System.out.println("==============================");

        boolean running = true;
        while (running) {
            System.out.println("\nWho are you?");
            System.out.println("1. Customer");
            System.out.println("2. Staff");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                customerMenu.start();

            } else if (choice.equals("2")) {
                System.out.println("\n--- STAFF LOGIN ---");
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                if (username.equals("admin") && password.equals("admin")) {
                    System.out.println("Login successful. Welcome, " + username + "!");
                    staffMenu.start();
                } else {
                    System.out.println("Wrong username or password.");
                }

            } else if (choice.equals("0")) {
                System.out.println("Thank you for using Grand Java Hotel. Goodbye!");
                running = false;

            } else {
                System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }
}
