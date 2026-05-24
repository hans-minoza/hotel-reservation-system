import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Booking {

    private static int idCounter = 1000;

    private String bookingId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfNights;
    private double totalCost;
    private boolean paid;
    private String status;
    private LocalDateTime bookedAt;

    public Booking(String customerName, String customerPhone, String customerEmail, Room room, LocalDate checkIn, LocalDate checkOut) {
        idCounter++;
        this.bookingId = "BK" + idCounter;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalCost = numberOfNights * Room.getPrice(room.getCategory());
        this.paid = false;
        this.status = "Active";
        this.bookedAt = LocalDateTime.now();
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public Room getRoom() { return room; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getNumberOfNights() { return numberOfNights; }
    public double getTotalCost() { return totalCost; }
    public boolean isPaid() { return paid; }
    public String getStatus() { return status; }
    public LocalDateTime getBookedAt() { return bookedAt; }

    public void setPaid(boolean paid) { this.paid = paid; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() {
        return status.equals("Active");
    }

    public boolean overlaps(LocalDate newCheckIn, LocalDate newCheckOut) {
        return isActive()
            && newCheckIn.isBefore(checkOut)
            && newCheckOut.isAfter(checkIn);
    }

    public boolean isEligibleForFullRefund() {
        return LocalDateTime.now().isBefore(bookedAt.plusHours(24));
    }
}
