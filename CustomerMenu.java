import java.util.Scanner;

public class CustomerMenu {

    private BookingManager bookingManager;
    private BookingForm bookingForm;
    private Scanner scanner;

    public CustomerMenu(BookingManager bookingManager, BookingForm bookingForm, Scanner scanner) {
        this.bookingManager = bookingManager;
        this.bookingForm = bookingForm;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n--- CUSTOMER MENU ---");
        System.out.println("1. Book a Room");
        System.out.println("2. Check My Booking");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            bookingForm.start();
        } else if (choice.equals("2")) {
            checkMyBooking();
        } else if (choice.equals("0")) {
            return;
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void checkMyBooking() {
        System.out.println("\n--- CHECK MY BOOKING ---");
        System.out.print("Enter your Booking ID: ");
        String id = scanner.nextLine().trim().toUpperCase();

        Booking b = bookingManager.findBooking(id);
        if (b == null) {
            System.out.println("Booking ID not found. Please check and try again.");
            return;
        }

        printBookingDetails(b);

        if (!b.isActive()) {
            System.out.println("This booking has been cancelled.");
            return;
        }

        System.out.println("\n1. Cancel my booking");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            cancelMyBooking(b);
        }
    }

    private void cancelMyBooking(Booking b) {
        System.out.println("\nRefund Policy:");
        System.out.println("  Full refund if cancelled within 24 hours of booking.");
        System.out.println("  No refund if cancelled after 24 hours.");
        System.out.println("Refund eligible: " + (b.isEligibleForFullRefund() ? "Yes" : "No"));
        System.out.print("\nAre you sure you want to cancel? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = bookingManager.cancelBooking(b.getBookingId());
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

    private void printBookingDetails(Booking b) {
        System.out.println("\nBooking ID    : " + b.getBookingId());
        System.out.println("Name          : " + b.getCustomerName());
        System.out.println("Room          : " + b.getRoom().getRoomNumber() + " (" + b.getRoom().getCategory() + ")");
        System.out.println("Check-in      : " + b.getCheckIn());
        System.out.println("Check-out     : " + b.getCheckOut());
        System.out.println("Nights        : " + b.getNumberOfNights());
        System.out.println("Total Cost    : PHP " + String.format("%,.2f", b.getTotalCost()));
        System.out.println("Payment       : " + (b.isPaid() ? "PAID" : "UNPAID"));
        System.out.println("Status        : " + b.getStatus());
    }
}
