import java.util.ArrayList;
import java.util.Scanner;

public class StaffActions {

    private RoomManager roomManager;
    private BookingManager bookingManager;
    private Scanner scanner;

    public StaffActions(RoomManager roomManager, BookingManager bookingManager, Scanner scanner) {
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.scanner = scanner;
    }

    public void viewAllRooms() {
        System.out.println("\n--- ALL ROOMS ---");
        ArrayList<Room> rooms = roomManager.getAllRooms();

        System.out.println("Total: " + rooms.size() + " | Available: " + roomManager.countAvailable() + " | Occupied: " + roomManager.countOccupied());
        System.out.println();

        String currentCategory = "";
        for (Room r : rooms) {
            if (!r.getCategory().equals(currentCategory)) {
                currentCategory = r.getCategory();
                System.out.println(currentCategory + " Rooms (PHP " + String.format("%,.0f", Room.getPrice(currentCategory)) + "/night):");
            }
            String status = r.isAvailable() ? "Available" : "Occupied";
            System.out.println("  Room " + r.getRoomNumber() + " - " + status);
        }
    }

    public void viewAllCustomers() {
        System.out.println("\n--- ALL CUSTOMER DETAILS ---");
        ArrayList<Booking> bookings = bookingManager.getAllBookings();

        if (bookings.isEmpty()) {
            System.out.println("No bookings on record.");
            return;
        }

        for (Booking b : bookings) {
            printFullDetails(b);
        }

        System.out.println("Total records: " + bookings.size());
    }

    public void searchBooking() {
        System.out.println("\n--- SEARCH BOOKING ---");
        System.out.println("1. Search by Customer Name");
        System.out.println("2. Search by Room Number");
        System.out.println("3. Search by Booking ID");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();
            ArrayList<Booking> results = bookingManager.searchByName(name);
            if (results.isEmpty()) {
                System.out.println("No bookings found.");
            } else {
                for (Booking b : results) printFullDetails(b);
            }

        } else if (choice.equals("2")) {
            System.out.print("Enter room number: ");
            try {
                int roomNum = Integer.parseInt(scanner.nextLine().trim());
                ArrayList<Booking> results = bookingManager.searchByRoomNumber(roomNum);
                if (results.isEmpty()) {
                    System.out.println("No bookings found.");
                } else {
                    for (Booking b : results) printFullDetails(b);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid room number.");
            }

        } else if (choice.equals("3")) {
            System.out.print("Enter Booking ID: ");
            String id = scanner.nextLine().trim().toUpperCase();
            Booking b = bookingManager.findBooking(id);
            if (b == null) {
                System.out.println("Booking not found.");
            } else {
                printFullDetails(b);
            }

        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void processPayment() {
        System.out.println("\n--- PROCESS PAYMENT ---");
        System.out.print("Enter Booking ID: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Booking b = bookingManager.findBooking(id);
        if (b == null) { System.out.println("Booking not found."); return; }
        if (!b.isActive()) { System.out.println("Booking is cancelled."); return; }
        if (b.isPaid()) { System.out.println("This booking is already paid."); return; }

        System.out.println("Customer   : " + b.getCustomerName());
        System.out.println("Room       : " + b.getRoom().getRoomNumber());
        System.out.println("Total Due  : PHP " + String.format("%,.2f", b.getTotalCost()));
        
        System.out.println("\nPayment Method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. E-Wallet");
        System.out.print("Enter choice: ");
        String method = scanner.nextLine();

        String methodName;
        if (method.equals("1")) methodName = "Cash";
        else if (method.equals("2")) methodName = "Card";
        else if (method.equals("3")) methodName = "E-Wallet";
        else { System.out.println("Invalid choice."); return; }

        String result = bookingManager.processPayment(id);
        if (result.equals("SUCCESS")) {
            System.out.println("Payment received via " + methodName + ".");
            System.out.println("Booking " + id + " is now PAID.");
        } else {
            System.out.println(result.replace("ERROR: ", ""));
        }
    }

    public void cancelBooking() {
        System.out.println("\n--- CANCEL BOOKING ---");
        System.out.print("Enter Booking ID: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Booking b = bookingManager.findBooking(id);
        if (b == null) { System.out.println("Booking not found."); return; }
        if (!b.isActive()) { System.out.println("Booking is already cancelled."); return; }

        System.out.println("Customer   : " + b.getCustomerName());
        System.out.println("Room       : " + b.getRoom().getRoomNumber());
        System.out.println("Total Cost : PHP " + String.format("%,.2f", b.getTotalCost()));
        System.out.println("Paid       : " + (b.isPaid() ? "Yes" : "No"));
        System.out.print("Confirm cancel? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = bookingManager.cancelBooking(id);
            if (result.startsWith("REFUND:")) {
                double amount = Double.parseDouble(result.split(":")[1]);
                System.out.println("Booking cancelled.");
                System.out.println("Full refund of PHP " + String.format("%,.2f", amount) + " will be processed.");
            } else if (result.equals("NOREFUND")) {
                System.out.println("Booking cancelled. No refund — 24-hour window has passed.");
            } else if (result.equals("CANCELLED")) {
                System.out.println("Booking cancelled. No payment was made.");
            } else {
                System.out.println(result.replace("ERROR: ", ""));
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    public void viewActiveBookings() {
        System.out.println("\n--- ACTIVE BOOKINGS ---");
        ArrayList<Booking> list = bookingManager.getActiveBookings();
        if (list.isEmpty()) {
            System.out.println("No active bookings.");
            return;
        }
        for (Booking b : list) printFullDetails(b);
        System.out.println("Total active: " + list.size());
    }

    public void viewAllBookings() {
        System.out.println("\n--- ALL BOOKINGS ---");
        ArrayList<Booking> list = bookingManager.getAllBookings();
        if (list.isEmpty()) {
            System.out.println("No bookings on record.");
            return;
        }
        for (Booking b : list) printFullDetails(b);
        System.out.println("Total: " + list.size());
    }

    private void printFullDetails(Booking b) {
        System.out.println("\nBooking ID  : " + b.getBookingId());
        System.out.println("Name        : " + b.getCustomerName());
        System.out.println("Phone       : " + b.getCustomerPhone());
        System.out.println("Email       : " + b.getCustomerEmail());
        System.out.println("Room        : " + b.getRoom().getRoomNumber() + " (" + b.getRoom().getCategory() + ")");
        System.out.println("Check-in    : " + b.getCheckIn());
        System.out.println("Check-out   : " + b.getCheckOut());
        System.out.println("Nights      : " + b.getNumberOfNights());
        System.out.println("Total Cost  : PHP " + String.format("%,.2f", b.getTotalCost()));
        System.out.println("Payment     : " + (b.isPaid() ? "PAID" : "UNPAID"));
        System.out.println("Status      : " + b.getStatus());
        System.out.println("-----------------------------");
    }
}
