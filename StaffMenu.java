import java.util.Scanner;

public class StaffMenu {

    private StaffActions staffActions;
    private Scanner scanner;

    public StaffMenu(StaffActions staffActions, Scanner scanner) {
        this.staffActions = staffActions;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- STAFF MENU ---");
            System.out.println("1. View All Rooms");
            System.out.println("2. View All Customer Details");
            System.out.println("3. Search Booking");
            System.out.println("4. Process Payment");
            System.out.println("5. Cancel a Booking");
            System.out.println("6. View Active Bookings");
            System.out.println("7. View All Bookings");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                staffActions.viewAllRooms();
            } else if (choice.equals("2")) {
                staffActions.viewAllCustomers();
            } else if (choice.equals("3")) {
                staffActions.searchBooking();
            } else if (choice.equals("4")) {
                staffActions.processPayment();
            } else if (choice.equals("5")) {
                staffActions.cancelBooking();
            } else if (choice.equals("6")) {
                staffActions.viewActiveBookings();
            } else if (choice.equals("7")) {
                staffActions.viewAllBookings();
            } else if (choice.equals("0")) {
                running = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
