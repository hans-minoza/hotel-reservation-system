public class Room {

    private int roomNumber;
    private String category;
    private boolean available;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public static double getPrice(String category) {
        if (category.equals("Standard")) return 1500;
        if (category.equals("Deluxe")) return 3500;
        if (category.equals("Suite")) return 7500;
        return 0;
    }
}
