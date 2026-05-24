import java.util.ArrayList;

public class RoomManager {

    private ArrayList<Room> rooms = new ArrayList<>();

    public RoomManager() {
        //standard 
        rooms.add(new Room(1, "Standard"));
        rooms.add(new Room(2, "Standard"));
        rooms.add(new Room(3, "Standard"));
        rooms.add(new Room(4, "Standard"));
        rooms.add(new Room(5, "Standard"));

        //deluxe 
        rooms.add(new Room(6, "Deluxe"));
        rooms.add(new Room(7, "Deluxe"));
        rooms.add(new Room(8, "Deluxe"));
        rooms.add(new Room(9, "Deluxe"));
        rooms.add(new Room(10, "Deluxe"));

        //suit 
        rooms.add(new Room(11, "Suite"));
        rooms.add(new Room(12, "Suite"));
        rooms.add(new Room(13, "Suite"));
        rooms.add(new Room(14, "Suite"));
        rooms.add(new Room(15, "Suite"));
    }

    public ArrayList<Room> getAllRooms() {
        return rooms;
    }

    public ArrayList<Room> getAvailableRoomsByCategory(String category) {
        ArrayList<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (r.getCategory().equals(category) && r.isAvailable()) {
                result.add(r);
            }
        }
        return result;
    }

    public Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }

    public void setRoomOccupied(int roomNumber) {
        Room r = findRoom(roomNumber);
        if (r != null) {
            r.setAvailable(false);
        }
    }

    public void setRoomAvailable(int roomNumber) {
        Room r = findRoom(roomNumber);
        if (r != null) {
            r.setAvailable(true);
        }
    }

    public int countAvailable() {
        int count = 0;
        for (Room r : rooms) {
            if (r.isAvailable()) count++;
        }
        return count;
    }

    public int countOccupied() {
        return rooms.size() - countAvailable();
    }
}
