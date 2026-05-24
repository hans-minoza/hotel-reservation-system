import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class BookingForm {

    private RoomManager roomManager;
    private BookingManager bookingManager;
    private Scanner scanner;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingForm(RoomManager roomManager, BookingManager bookingManager, Scanner scanner) {
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n--- BOOK A ROOM ---");

        System.out.println("Select room category:");
        System.out.println("1. Standard - PHP 1,500 per night");
        System.out.println("2. Deluxe   - PHP 3,500 per night");
        System.out.println("3. Suite    - PHP 7,500 per night");
        System.out.print("Enter choice: ");
        String catChoice = scanner.nextLine();

        String category;
        if (catChoice.equals("1")) {
            category = "Standard";
        } else if (catChoice.equals("2")) {
            category = "Deluxe";
        } else if (catChoice.equals("3")) {
            category = "Suite";
        } else {
            System.out.println("Invalid category.");
            return;
        }

        ArrayList<Room> available = roomManager.getAvailableRoomsByCategory(category);
        if (available.isEmpty()) {
            System.out.println("Sorry, no " + category + " rooms are available right now.");
            return;
        }

        System.out.println("\nAvailable " + category + " rooms:");
        for (Room r : available) {
            System.out.println("  Room " + r.getRoomNumber());
        }

        System.out.println("\nPlease fill in your details:");

        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) {
            System.out.println("Phone cannot be empty.");
            return;
        }
        
        System.out.print("Email Address: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            return;
        }
        
        System.out.print("Enter room number: ");
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number.");
            return;
        }

        LocalDate checkIn = readDate("Check-in date (yyyy-MM-dd): ");
        if (checkIn == null) return;

        LocalDate checkOut = readDate("Check-out date (yyyy-MM-dd): ");
        if (checkOut == null) return;

        String result = bookingManager.createBooking(name, phone, email, category, roomNumber, checkIn, checkOut);

        if (result.startsWith("SUCCESS:")) {
            String bookingId = result.split(":")[1];
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double total = nights * Room.getPrice(category);

            System.out.println("\nBooking confirmed!");
            System.out.println("Booking ID    : " + bookingId);
            System.out.println("Name          : " + name);
            System.out.println("Room          : " + roomNumber + " (" + category + ")");
            System.out.println("Check-in      : " + checkIn);
            System.out.println("Check-out     : " + checkOut);
            System.out.println("Nights        : " + nights);
            System.out.println("Price/Night   : PHP " + String.format("%,.2f", Room.getPrice(category)));
            System.out.println("Total Cost    : PHP " + String.format("%,.2f", total));
            System.out.println("Payment       : UNPAID");
            System.out.println("\nIMPORTANT: Save your Booking ID -> " + bookingId);
            System.out.println("You will need it to check or cancel your booking.");
        } else {
            System.out.println(result.replace("ERROR: ", ""));
        }
    }

    private LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return LocalDate.parse(input, dateFormat);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd (example: 2025-12-25).");
            return null;
        }
    }
}
