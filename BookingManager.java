import java.time.LocalDate;
import java.util.ArrayList;

public class BookingManager {

    private ArrayList<Booking> bookings = new ArrayList<>();
    private RoomManager roomManager;

    public BookingManager(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public String createBooking(String name, String phone, String email, String category, int roomNumber, LocalDate checkIn, LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {
            return "ERROR: Check-out date must be after check-in date.";
        }

        if (checkIn.isBefore(LocalDate.now())) {
            return "ERROR: Check-in date cannot be in the past.";
        }

        Room room = roomManager.findRoom(roomNumber);

        if (room == null) {
            return "ERROR: Room " + roomNumber + " does not exist.";
        }

        if (!room.getCategory().equals(category)) {
            return "ERROR: Room " + roomNumber + " is not a " + category + " room.";
        }

        for (Booking b : bookings) {
            if (b.getRoom().getRoomNumber() == roomNumber && b.overlaps(checkIn, checkOut)) {
                return "ERROR: Room " + roomNumber + " is already booked for those dates.";
            }
        }

        Booking newBooking = new Booking(name, phone, email, room, checkIn, checkOut); bookings.add(newBooking);

        roomManager.setRoomOccupied(roomNumber);

        return "SUCCESS:" + newBooking.getBookingId();

    }

    public String processPayment(String bookingId) {
        Booking b = findBooking(bookingId);
        if (b == null) return "ERROR: Booking not found.";
        if (!b.isActive()) return "ERROR: Booking is already cancelled.";
        if (b.isPaid()) return "ERROR: Booking is already paid.";
        b.setPaid(true);
        return "SUCCESS";
    }

    public String cancelBooking(String bookingId) {
        Booking b = findBooking(bookingId);
        if (b == null) return "ERROR: Booking not found.";
        if (!b.isActive()) return "ERROR: Booking is already cancelled.";

        b.setStatus("Cancelled");

        boolean stillBooked = false;
        for (Booking x : bookings) {
            if (x.isActive() && x.getRoom().getRoomNumber() == b.getRoom().getRoomNumber()) {
                stillBooked = true;
                break;
            }
        }

        if (!stillBooked) {
            roomManager.setRoomAvailable(b.getRoom().getRoomNumber());
        }

        if (b.isPaid()) {
            if (b.isEligibleForFullRefund()) {
                return "REFUND:" + b.getTotalCost();
            } else {
                return "NOREFUND";
            }
        }

        return "CANCELLED";
    }

    public Booking findBooking(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                return b;
            }
        }
        return null;
    }

    public ArrayList<Booking> searchByName(String name) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<Booking> searchByRoomNumber(int roomNumber) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getRoom().getRoomNumber() == roomNumber) {
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<Booking> getActiveBookings() {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.isActive()) {
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<Booking> getAllBookings() {
        return bookings;
    }
}
