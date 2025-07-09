import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isBooked;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = false;
    }
}

class Booking implements Serializable {
    String customerName;
    int roomNumber;
    String category;
    String date;
    boolean paymentCompleted;

    public Booking(String customerName, int roomNumber, String category, String date) {
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.date = date;
        this.paymentCompleted = false;
    }

    public String toString() {
        return "Customer: " + customerName +
               ", Room: " + roomNumber +
               ", Category: " + category +
               ", Date: " + date +
               ", Payment: " + (paymentCompleted ? "Done" : "Pending");
    }
}

public class Main {
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String FILE_NAME = "bookings.dat";

    public static void main(String[] args) {
        loadBookings();
        initializeRooms();
        int choice;

        do {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Booking");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            choice = scanner.nextInt(); scanner.nextLine();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> makeBooking();
                case 3 -> cancelBooking();
                case 4 -> viewBookings();
                case 5 -> saveBookings();
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 5);
    }

    static void initializeRooms() {
        if (rooms.isEmpty()) {
            for (int i = 101; i <= 105; i++) rooms.add(new Room(i, "Standard"));
            for (int i = 201; i <= 205; i++) rooms.add(new Room(i, "Deluxe"));
            for (int i = 301; i <= 303; i++) rooms.add(new Room(i, "Suite"));

            // Mark booked rooms from saved data
            for (Booking b : bookings) {
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) r.isBooked = true;
                }
            }
        }
    }

    static void viewAvailableRooms() {
        System.out.println("--- Available Rooms ---");
        for (Room r : rooms) {
            if (!r.isBooked) {
                System.out.println("Room " + r.roomNumber + " - " + r.category);
            }
        }
    }

    static void makeBooking() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        viewAvailableRooms();

        System.out.print("Enter room number to book: ");
        int roomNum = scanner.nextInt(); scanner.nextLine();

        Room selectedRoom = null;
        for (Room r : rooms) {
            if (r.roomNumber == roomNum && !r.isBooked) {
                selectedRoom = r;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid or already booked room.");
            return;
        }

        System.out.print("Enter booking date (dd-mm-yyyy): ");
        String date = scanner.nextLine();

        Booking booking = new Booking(name, roomNum, selectedRoom.category, date);
        System.out.println("Simulating payment...");
        booking.paymentCompleted = true;

        bookings.add(booking);
        selectedRoom.isBooked = true;

        System.out.println("Booking successful!\n" + booking);
    }

    static void cancelBooking() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        Iterator<Booking> iterator = bookings.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Booking b = iterator.next();
            if (b.customerName.equalsIgnoreCase(name)) {
                iterator.remove();
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) r.isBooked = false;
                }
                System.out.println("Booking canceled for " + name);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No booking found for " + name);
        }
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("--- All Bookings ---");
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    static void saveBookings() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(bookings);
            System.out.println("Bookings saved.");
        } catch (IOException e) {
            System.out.println("Error saving bookings.");
        }
    }

    static void loadBookings() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            bookings = (ArrayList<Booking>) in.readObject();
            System.out.println("Loaded existing bookings.");
        } catch (Exception e) {
            System.out.println("No existing bookings to load.");
        }
    }
}
