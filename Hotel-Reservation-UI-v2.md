# Hotel Reservation System UI Codebase (Java Swing)

> Save this as `HotelReservationUI.java` if you want to compile it directly, or keep it inside a markdown file as requested.

```java
import javax.swing.*; // Imports Swing UI components like JFrame, JPanel, JTable, and buttons.
import javax.swing.border.EmptyBorder; // Imports a border helper for clean spacing around panels.
import javax.swing.table.DefaultTableModel; // Imports a table model for displaying room and booking lists.
import java.awt.*; // Imports AWT classes for layout, colors, fonts, and dimensions.
import java.awt.event.ActionEvent; // Imports the event type used when buttons are clicked.
import java.awt.event.ActionListener; // Imports the listener interface for button actions.
import java.time.LocalDate; // Imports LocalDate for date handling without time.
import java.time.format.DateTimeFormatter; // Imports a formatter for converting text to date objects.
import java.time.temporal.ChronoUnit; // Imports ChronoUnit for counting the number of days between dates.
import java.util.ArrayList; // Imports ArrayList for storing rooms and bookings dynamically;
import java.util.List; // Imports List to use a flexible collection interface.
import java.util.UUID; // Imports UUID for generating booking reference numbers.

public class HotelReservationUI extends JFrame { // Declares the main window class and makes it a JFrame.

    // -----------------------------
    // THEME COLORS
    // -----------------------------

    private final Color backgroundDark = new Color(10, 14, 30); // Deep navy background for the app.
    private final Color panelDark = new Color(18, 24, 48); // Slightly lighter dark panel color.
    private final Color neonPink = new Color(255, 70, 170); // Neon pink accent color.
    private final Color neonBlue = new Color(80, 180, 255); // Neon blue accent color.
    private final Color neonSky = new Color(120, 230, 255); // Neon sky-blue accent color.
    private final Color neonPurple = new Color(170, 110, 255); // Neon purple accent color.
    private final Color textWhite = new Color(240, 245, 255); // Bright white text color.
    private final Color successGreen = new Color(70, 220, 140); // Green used for success messages.
    private final Color dangerRed = new Color(255, 90, 100); // Red used for errors or danger.
    private final Color warningOrange = new Color(255, 180, 80); // Orange used for warnings.

    // -----------------------------
    // DATA AND STATE
    // -----------------------------

    private final List<Room> rooms = new ArrayList<>(); // Stores all room objects in memory.
    private final List<Booking> bookings = new ArrayList<>(); // Stores all active booking objects in memory.

    // -----------------------------
    // UI COMPONENTS
    // -----------------------------

    private JTable roomTable; // Displays all hotel rooms.
    private JTable bookingTable; // Displays all active bookings.
    private JTable searchTable; // Displays search results.

    private DefaultTableModel roomTableModel; // The model behind the room table.
    private DefaultTableModel bookingTableModel; // The model behind the booking table.
    private DefaultTableModel searchTableModel; // The model behind the search table.

    private JTextField customerNameField; // Input for customer full name.
    private JComboBox<String> roomCategoryCombo; // Dropdown for room category.
    private JComboBox<String> roomNumberCombo; // Dropdown for available room numbers.
    private JTextField checkInField; // Input for check-in date.
    private JTextField checkOutField; // Input for check-out date.
    private JLabel nightsValueLabel; // Displays computed nights.
    private JLabel totalValueLabel; // Displays computed total cost.
    private JLabel availabilityValueLabel; // Displays selected room availability.

    private JTextField searchField; // Input for searching by name or room number.
    private JComboBox<String> searchModeCombo; // Lets the user choose name search or room search.

    private JTextField cancelRefField; // Input for booking reference to cancel.
    private JLabel refundValueLabel; // Displays the refund result after cancellation.

    private JLabel statusLabel; // Displays application messages such as success or error.

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Defines the accepted date format.

    public HotelReservationUI() { // Constructor that builds the entire interface.
        setTitle("Hotel Reservation System - Swing UI"); // Sets the window title.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes the application when the window closes.
        setSize(1280, 820); // Sets the window size.
        setLocationRelativeTo(null); // Centers the window on the screen.
        setMinimumSize(new Dimension(1150, 760)); // Prevents the UI from becoming too small.
        getContentPane().setBackground(backgroundDark); // Applies the main background color.

        loadSampleRooms(); // Creates starter room records for the UI.

        setLayout(new BorderLayout(12, 12)); // Uses BorderLayout with spacing between regions.
        setBorderAndBackground((JComponent) getContentPane()); // Styles the root container.

        add(buildHeader(), BorderLayout.NORTH); // Adds the top header panel.
        add(buildTabs(), BorderLayout.CENTER); // Adds the main tabbed content.
        add(buildStatusBar(), BorderLayout.SOUTH); // Adds the bottom status bar.

        refreshRoomTable(); // Fills the room table with data.
        refreshBookingTable(); // Fills the booking table with data.
        refreshRoomNumberCombo(); // Fills room number dropdown with available rooms.
        refreshSearchTable(new ArrayList<>()); // Starts the search table empty.
    }

    // -----------------------------
    // APP START
    // -----------------------------

    public static void main(String[] args) { // Main method where the program begins.
        SwingUtilities.invokeLater(() -> { // Runs the UI on the Swing event dispatch thread.
            HotelReservationUI ui = new HotelReservationUI(); // Creates the window object.
            ui.setVisible(true); // Shows the window to the user.
        });
    }

    // -----------------------------
    // HEADER AND STATUS BAR
    // -----------------------------

    private JPanel buildHeader() { // Builds the top title area.
        JPanel header = new JPanel(new BorderLayout()); // Creates a panel with left and right areas.
        header.setBackground(backgroundDark); // Applies the dark theme.
        header.setBorder(new EmptyBorder(18, 18, 18, 18)); // Adds inner spacing.

        JLabel title = new JLabel("HOTEL RESERVATION SYSTEM"); // Creates the main title label.
        title.setFont(new Font("SansSerif", Font.BOLD, 30)); // Sets a large bold font.
        title.setForeground(neonSky); // Applies a bright sky-blue color.

        JLabel subtitle = new JLabel("Room Booking • Search • Cancellation • Refund Rules"); // Creates a subtitle.
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Sets a smaller font.
        subtitle.setForeground(new Color(180, 190, 215)); // Uses a soft gray-blue color.

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4)); // Stacks title and subtitle vertically.
        titleBox.setOpaque(false); // Makes the panel transparent.
        titleBox.add(title); // Adds the title label.
        titleBox.add(subtitle); // Adds the subtitle label.

        JLabel badge = new JLabel("DCIT 23 • UI Module"); // Adds a class badge label.
        badge.setFont(new Font("SansSerif", Font.BOLD, 14)); // Sets a strong readable font.
        badge.setForeground(textWhite); // Uses bright text.
        badge.setBorder(BorderFactory.createCompoundBorder( // Creates a double border effect.
                BorderFactory.createLineBorder(neonPink, 2), // Outer neon pink border.
                new EmptyBorder(10, 14, 10, 14) // Inner spacing for the badge.
        ));

        header.add(titleBox, BorderLayout.WEST); // Places title block on the left.
        header.add(badge, BorderLayout.EAST); // Places badge on the right.
        return header; // Returns the complete header panel.
    }

    private JPanel buildStatusBar() { // Builds the bottom status bar.
        JPanel statusBar = new JPanel(new BorderLayout()); // Creates a panel with a left-to-right layout.
        statusBar.setBackground(panelDark); // Gives the status bar a dark panel color.
        statusBar.setBorder(new EmptyBorder(8, 14, 8, 14)); // Adds padding.

        statusLabel = new JLabel("Ready."); // Initializes the status label.
        statusLabel.setForeground(textWhite); // Sets text color.
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Uses a readable font.

        JLabel hint = new JLabel("Dates format: yyyy-MM-dd | Refund rule: full refund if cancelled 24 hours before check-in"); // Shows a quick rule reminder.
        hint.setForeground(new Color(160, 170, 205)); // Uses a soft color.
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Uses a small font.

        statusBar.add(statusLabel, BorderLayout.WEST); // Adds status label to left side.
        statusBar.add(hint, BorderLayout.EAST); // Adds helper text to the right.
        return statusBar; // Returns the status bar.
    }

    // -----------------------------
    // TABS
    // -----------------------------

    private JTabbedPane buildTabs() { // Builds the tabbed navigation.
        JTabbedPane tabs = new JTabbedPane(); // Creates the main tabbed pane.
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13)); // Sets the tab font.

        tabs.addTab("Dashboard", buildDashboardPanel()); // Adds dashboard tab.
        tabs.addTab("Rooms", buildRoomsPanel()); // Adds rooms tab.
        tabs.addTab("Bookings", buildBookingsPanel()); // Adds bookings tab.
        tabs.addTab("Search", buildSearchPanel()); // Adds search tab.
        tabs.addTab("Cancellation", buildCancellationPanel()); // Adds cancellation tab.

        return tabs; // Returns the tabbed pane.
    }

    private JPanel buildDashboardPanel() { // Builds the dashboard overview.
        JPanel panel = createMainPanel(); // Creates a styled main panel.
        panel.setLayout(new GridLayout(2, 2, 14, 14)); // Uses a grid layout for cards.

        panel.add(createMetricCard("TOTAL ROOMS", String.valueOf(rooms.size()), neonPink)); // Card for total rooms.
        panel.add(createMetricCard("ACTIVE BOOKINGS", String.valueOf(bookings.size()), neonBlue)); // Card for bookings count.
        panel.add(createMetricCard("AVAILABLE ROOMS", String.valueOf(countAvailableRooms()), neonPurple)); // Card for available rooms.
        panel.add(createMetricCard("SYSTEM STATUS", "ONLINE", successGreen)); // Card for system status.

        return panel; // Returns the dashboard.
    }

    // -----------------------------
    // ROOMS TAB
    // -----------------------------

    private JPanel buildRoomsPanel() { // Builds the rooms table tab.
        JPanel panel = createMainPanel(); // Creates a dark panel.

        JLabel heading = createSectionTitle("Room Inventory"); // Creates section heading.
        panel.add(heading, BorderLayout.NORTH); // Adds heading to top.

        String[] columns = {"Room No.", "Category", "Price/Night", "Availability"}; // Defines table columns.
        roomTableModel = new DefaultTableModel(columns, 0) { // Creates a table model with custom behavior.
            @Override
            public boolean isCellEditable(int row, int column) { // Stops users from editing table cells directly.
                return false; // Makes every cell read-only.
            }
        };

        roomTable = createStyledTable(roomTableModel); // Creates the styled room table.
        JScrollPane scrollPane = new JScrollPane(roomTable); // Wraps the table in a scroll pane.
        scrollPane.setBorder(BorderFactory.createLineBorder(neonSky, 1)); // Adds a neon border.
        panel.add(scrollPane, BorderLayout.CENTER); // Places the table in the center.

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Creates a bottom action area.
        footer.setOpaque(false); // Makes it transparent.
        JButton refreshButton = createNeonButton("Refresh Rooms", neonBlue); // Creates a refresh button.
        refreshButton.addActionListener(e -> { // Adds click behavior.
            refreshRoomTable(); // Updates the table.
            setStatus("Room list refreshed."); // Shows a message.
        });
        footer.add(refreshButton); // Adds button to footer.
        panel.add(footer, BorderLayout.SOUTH); // Adds footer to bottom.

        return panel; // Returns the room panel.
    }

    // -----------------------------
    // BOOKINGS TAB
    // -----------------------------

    private JPanel buildBookingsPanel() { // Builds the booking creation and active booking area.
        JPanel panel = createMainPanel(); // Creates the root panel.
        panel.setLayout(new BorderLayout(14, 14)); // Uses BorderLayout for top and center sections.

        JLabel heading = createSectionTitle("Create Booking"); // Adds a heading for the form.
        panel.add(heading, BorderLayout.NORTH); // Places heading at the top.

        JPanel formAndSummary = new JPanel(new GridLayout(1, 2, 14, 14)); // Splits screen into two panels.
        formAndSummary.setOpaque(false); // Makes the container transparent.

        formAndSummary.add(buildBookingForm()); // Adds the booking form.
        formAndSummary.add(buildBookingSummary()); // Adds the booking summary panel.

        panel.add(formAndSummary, BorderLayout.CENTER); // Adds the two-column area.

        JPanel bottom = new JPanel(new BorderLayout(12, 12)); // Bottom section with table.
        bottom.setOpaque(false); // Makes it transparent.

        JLabel activeLabel = createSectionTitle("Active Bookings"); // Heading for active bookings.
        bottom.add(activeLabel, BorderLayout.NORTH); // Places active bookings label at the top.

        String[] columns = {"Ref No.", "Customer", "Room No.", "Category", "Check-In", "Check-Out", "Nights", "Total"}; // Booking table columns.
        bookingTableModel = new DefaultTableModel(columns, 0) { // Creates the model.
            @Override
            public boolean isCellEditable(int row, int column) { // Prevents direct editing.
                return false; // Makes table read-only.
            }
        };

        bookingTable = createStyledTable(bookingTableModel); // Creates a styled bookings table.
        JScrollPane bookingScroll = new JScrollPane(bookingTable); // Wraps it in a scroll pane.
        bookingScroll.setBorder(BorderFactory.createLineBorder(neonPink, 1)); // Adds neon border.
        bottom.add(bookingScroll, BorderLayout.CENTER); // Adds the table to the center.

        panel.add(bottom, BorderLayout.SOUTH); // Places bottom booking list area.

        return panel; // Returns the bookings panel.
    }

    private JPanel buildBookingForm() { // Builds the booking input form.
        JPanel form = createTitledPanel("Booking Details", neonPink); // Creates a titled panel.

        form.setLayout(new GridBagLayout()); // Uses flexible grid bag layout.
        GridBagConstraints gbc = new GridBagConstraints(); // Creates constraints for positioning.
        gbc.insets = new Insets(8, 8, 8, 8); // Adds spacing around fields.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Makes components stretch horizontally.
        gbc.gridx = 0; // Starts in the first column.
        gbc.gridy = 0; // Starts at the first row.
        gbc.weightx = 0.35; // Label column takes less width.

        customerNameField = createTextField(); // Input field for customer name.
        roomCategoryCombo = createComboBox(new String[]{"Standard", "Deluxe", "Suite"}); // Room category choices.
        roomNumberCombo = createComboBox(new String[0]); // Empty room number dropdown; filled dynamically.
        checkInField = createTextField(); // Input for check-in date.
        checkOutField = createTextField(); // Input for check-out date.

        addFormRow(form, gbc, "Customer Name", customerNameField); // Adds customer name row.
        addFormRow(form, gbc, "Room Category", roomCategoryCombo); // Adds room category row.
        addFormRow(form, gbc, "Room Number", roomNumberCombo); // Adds room number row.
        addFormRow(form, gbc, "Check-In Date", checkInField); // Adds check-in row.
        addFormRow(form, gbc, "Check-Out Date", checkOutField); // Adds check-out row.

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Row for action buttons.
        buttonRow.setOpaque(false); // Transparent background.

        JButton computeButton = createNeonButton("Compute Stay", neonPurple); // Button to compute nights and total.
        computeButton.addActionListener(e -> computeStay()); // Triggers date and price computation.

        JButton bookButton = createNeonButton("Book Room", successGreen); // Button to save the booking.
        bookButton.addActionListener(e -> createBooking()); // Triggers booking creation.

        buttonRow.add(computeButton); // Adds compute button.
        buttonRow.add(bookButton); // Adds book button.

        gbc.gridx = 0; // Resets to first column.
        gbc.gridy++; // Moves to next row.
        gbc.gridwidth = 2; // Spans both columns.
        form.add(buttonRow, gbc); // Adds buttons to the form.

        return form; // Returns the booking form.
    }

    private JPanel buildBookingSummary() { // Builds the live summary panel.
        JPanel summary = createTitledPanel("Live Booking Summary", neonBlue); // Creates a title panel.
        summary.setLayout(new GridLayout(0, 1, 10, 10)); // Arranges fields vertically.

        JLabel nightsLabel = createSummaryLine("Number of Nights"); // Label for nights.
        nightsValueLabel = createSummaryValue("0"); // Value label for nights.

        JLabel totalLabel = createSummaryLine("Estimated Total"); // Label for total.
        totalValueLabel = createSummaryValue("₱0.00"); // Value label for total cost.

        JLabel availabilityLabel = createSummaryLine("Selected Room Status"); // Label for availability.
        availabilityValueLabel = createSummaryValue("Select a room"); // Value label for room status.

        summary.add(wrapSummaryRow(nightsLabel, nightsValueLabel)); // Adds nights row.
        summary.add(wrapSummaryRow(totalLabel, totalValueLabel)); // Adds total row.
        summary.add(wrapSummaryRow(availabilityLabel, availabilityValueLabel)); // Adds availability row.

        JLabel note = new JLabel("<html><body style='width: 320px'>This panel updates automatically when the user selects a room category, room number, and date range.</body></html>"); // Adds helper note.
        note.setForeground(new Color(190, 200, 230)); // Soft text color.
        note.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Readable font.

        summary.add(note); // Adds note to summary panel.
        return summary; // Returns summary.
    }

    // -----------------------------
    // SEARCH TAB
    // -----------------------------

    private JPanel buildSearchPanel() { // Builds the search tab.
        JPanel panel = createMainPanel(); // Creates a dark main panel.
        panel.setLayout(new BorderLayout(14, 14)); // Uses border layout.

        JLabel heading = createSectionTitle("Search Booking"); // Adds section title.
        panel.add(heading, BorderLayout.NORTH); // Adds heading to top.

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Creates search controls row.
        searchBar.setOpaque(false); // Makes background transparent.

        searchModeCombo = createComboBox(new String[]{"By Name", "By Room Number"}); // Search mode selector.
        searchField = createTextField(); // Search text field.
        searchField.setColumns(18); // Sets a width hint.

        JButton searchButton = createNeonButton("Search", neonPink); // Creates search button.
        searchButton.addActionListener(e -> performSearch()); // Executes search.

        searchBar.add(createSmallLabel("Mode")); // Adds mode label.
        searchBar.add(searchModeCombo); // Adds mode combo box.
        searchBar.add(createSmallLabel("Keyword")); // Adds keyword label.
        searchBar.add(searchField); // Adds search text field.
        searchBar.add(searchButton); // Adds search button.

        panel.add(searchBar, BorderLayout.NORTH); // Adds search controls near the top.

        String[] columns = {"Ref No.", "Customer", "Room No.", "Category", "Check-In", "Check-Out", "Nights", "Total"}; // Result columns.
        searchTableModel = new DefaultTableModel(columns, 0) { // Creates result table model.
            @Override
            public boolean isCellEditable(int row, int column) { // Prevents edits.
                return false; // Table stays read-only.
            }
        };

        searchTable = createStyledTable(searchTableModel); // Builds the result table.
        JScrollPane searchScroll = new JScrollPane(searchTable); // Wraps it.
        searchScroll.setBorder(BorderFactory.createLineBorder(neonPurple, 1)); // Adds border.
        panel.add(searchScroll, BorderLayout.CENTER); // Places table in center.

        return panel; // Returns search panel.
    }

    // -----------------------------
    // CANCELLATION TAB
    // -----------------------------

    private JPanel buildCancellationPanel() { // Builds the cancellation interface.
        JPanel panel = createMainPanel(); // Creates the root panel.
        panel.setLayout(new BorderLayout(14, 14)); // Sets overall layout.

        JLabel heading = createSectionTitle("Cancellation and Refund Rules"); // Adds heading.
        panel.add(heading, BorderLayout.NORTH); // Places heading at top.

        JPanel center = new JPanel(new GridLayout(2, 1, 14, 14)); // Splits center into two parts.
        center.setOpaque(false); // Transparent background.

        JPanel cancelForm = createTitledPanel("Cancel Booking", neonPurple); // Panel for cancellation form.
        cancelForm.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Horizontal layout.

        cancelRefField = createTextField(); // Input for booking reference.
        cancelRefField.setColumns(18); // Sets width.

        JButton cancelButton = createNeonButton("Cancel Booking", dangerRed); // Button to cancel booking.
        cancelButton.addActionListener(e -> cancelBooking()); // Cancels and computes refund.

        cancelForm.add(createSmallLabel("Booking Ref")); // Label for reference.
        cancelForm.add(cancelRefField); // Input field.
        cancelForm.add(cancelButton); // Action button.

        JPanel rules = createTitledPanel("Refund Policy", neonBlue); // Panel for policy text.
        rules.setLayout(new BorderLayout()); // Uses border layout.

        JTextArea policyArea = new JTextArea(); // Text area for refund rules.
        policyArea.setEditable(false); // Makes it read-only.
        policyArea.setLineWrap(true); // Wraps long lines.
        policyArea.setWrapStyleWord(true); // Wraps at word boundaries.
        policyArea.setOpaque(false); // Transparent background.
        policyArea.setForeground(textWhite); // Text color.
        policyArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Font.
        policyArea.setText(
                "• Full refund if the booking is cancelled at least 24 hours before check-in.\n" +
                "• Partial or no refund may be applied if cancellation happens less than 24 hours before check-in.\n" +
                "• The system auto-updates room availability after cancellation."
        );

        rules.add(policyArea, BorderLayout.CENTER); // Adds policy text to panel.

        center.add(cancelForm); // Adds cancel form.
        center.add(rules); // Adds rules panel.

        panel.add(center, BorderLayout.CENTER); // Adds the middle content.

        JPanel bottom = new JPanel(new BorderLayout()); // Bottom refund summary.
        bottom.setOpaque(false); // Transparent background.

        refundValueLabel = createSummaryValue("Refund will appear here."); // Refund message label.
        bottom.add(refundValueLabel, BorderLayout.WEST); // Places message at left.

        panel.add(bottom, BorderLayout.SOUTH); // Adds bottom area.

        return panel; // Returns cancellation panel.
    }

    // -----------------------------
    // ACTIONS
    // -----------------------------

    private void computeStay() { // Computes the number of nights and estimated total.
        try { // Begins error protection.
            Room room = getSelectedRoom(); // Gets the room currently selected by the user.
            if (room == null) { // Checks if no room is selected.
                setStatus("Please select a valid room first."); // Shows error message.
                return; // Stops execution.
            }

            LocalDate checkIn = parseDate(checkInField.getText().trim()); // Parses check-in date.
            LocalDate checkOut = parseDate(checkOutField.getText().trim()); // Parses check-out date.

            if (!checkOut.isAfter(checkIn)) { // Ensures checkout is after check-in.
                setStatus("Check-out date must be after check-in date."); // Shows validation message.
                return; // Stops execution.
            }

            long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // Counts nights between dates.
            double total = nights * room.pricePerNight; // Computes total cost.

            nightsValueLabel.setText(String.valueOf(nights)); // Updates nights label.
            totalValueLabel.setText(String.format("₱%,.2f", total)); // Updates total label.
            availabilityValueLabel.setText(room.available ? "Available" : "Unavailable"); // Shows room availability.

            setStatus("Stay computed successfully."); // Shows success message.
        } catch (Exception ex) { // Catches any parsing or logic errors.
            setStatus("Error: " + ex.getMessage()); // Prints the error message.
        }
    }

    private void createBooking() { // Creates a new booking record.
        try { // Begins validation block.
            String name = customerNameField.getText().trim(); // Gets the customer name.
            if (name.isEmpty()) { // Checks for empty input.
                setStatus("Customer name cannot be empty."); // Displays validation message.
                return; // Stops the method.
            }

            Room room = getSelectedRoom(); // Gets the selected room.
            if (room == null) { // Ensures a room is selected.
                setStatus("Please choose a valid room."); // Shows error.
                return; // Stops execution.
            }

            if (!room.available) { // Prevents double booking.
                setStatus("That room is already booked."); // Shows availability error.
                return; // Stops execution.
            }

            LocalDate checkIn = parseDate(checkInField.getText().trim()); // Parses check-in date.
            LocalDate checkOut = parseDate(checkOutField.getText().trim()); // Parses check-out date.
            if (!checkOut.isAfter(checkIn)) { // Ensures the date range is valid.
                setStatus("Check-out must be later than check-in."); // Shows date error.
                return; // Stops execution.
            }

            long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // Calculates nights.
            double total = nights * room.pricePerNight; // Calculates total price.

            String reference = generateReference(); // Generates a unique booking reference.

            Booking booking = new Booking(reference, name, room.roomNumber, room.category, checkIn, checkOut, nights, total); // Creates booking object.
            bookings.add(booking); // Saves the booking in memory.
            room.available = false; // Marks the room as unavailable.

            refreshRoomTable(); // Refreshes room table.
            refreshBookingTable(); // Refreshes booking table.
            refreshRoomNumberCombo(); // Updates room dropdown.
            clearBookingForm(); // Clears fields for next input.

            setStatus("Booking created successfully. Reference: " + reference); // Shows success message.
        } catch (Exception ex) { // Handles input errors.
            setStatus("Booking failed: " + ex.getMessage()); // Shows failure message.
        }
    }

    private void performSearch() { // Searches bookings by name or room number.
        String keyword = searchField.getText().trim(); // Gets the search text.
        if (keyword.isEmpty()) { // Ensures the field is not empty.
            setStatus("Enter a search keyword."); // Displays validation message.
            return; // Stops method.
        }

        String mode = (String) searchModeCombo.getSelectedItem(); // Reads the selected search mode.
        List<Booking> results = new ArrayList<>(); // Creates a list for search results.

        for (Booking booking : bookings) { // Loops over each booking.
            if ("By Name".equals(mode)) { // If searching by name...
                if (booking.customerName.toLowerCase().contains(keyword.toLowerCase())) { // Checks partial match.
                    results.add(booking); // Adds matching booking.
                }
            } else { // Otherwise search by room number.
                if (String.valueOf(booking.roomNumber).contains(keyword)) { // Checks room number match.
                    results.add(booking); // Adds matching booking.
                }
            }
        }

        refreshSearchTable(results); // Displays the filtered results.
        setStatus("Search completed. Results found: " + results.size()); // Shows result count.
    }

    private void cancelBooking() { // Cancels a booking and applies refund logic.
        String ref = cancelRefField.getText().trim(); // Gets the booking reference.
        if (ref.isEmpty()) { // Checks for empty reference.
            setStatus("Enter a booking reference."); // Validation message.
            return; // Stops method.
        }

        Booking target = null; // Holds the booking that matches.
        for (Booking booking : bookings) { // Searches every booking.
            if (booking.reference.equalsIgnoreCase(ref)) { // Matches by reference.
                target = booking; // Stores match.
                break; // Stops searching.
            }
        }

        if (target == null) { // If no booking is found...
            setStatus("Booking reference not found."); // Shows error.
            refundValueLabel.setText("Refund will appear here."); // Resets refund label.
            return; // Stops execution.
        }

        long hoursBeforeCheckIn = ChronoUnit.HOURS.between(LocalDate.now().atStartOfDay(), target.checkIn.atStartOfDay()); // Approximate hours between today and check-in.
        boolean fullRefund = hoursBeforeCheckIn >= 24; // Full refund if cancelled at least 24 hours before check-in.

        double refund = fullRefund ? target.totalCost : target.totalCost * 0.5; // Simple refund rule for UI demo.
        bookings.remove(target); // Removes the booking from active bookings.

        Room room = findRoomByNumber(target.roomNumber); // Finds the room tied to this booking.
        if (room != null) { // Checks whether the room exists.
            room.available = true; // Marks the room as available again.
        }

        refreshBookingTable(); // Refreshes active booking list.
        refreshRoomTable(); // Refreshes room availability list.
        refreshRoomNumberCombo(); // Refreshes room selector.
        refundValueLabel.setText(String.format("Refund for %s: ₱%,.2f", ref, refund)); // Shows refund amount.
        setStatus(fullRefund ? "Cancellation completed with full refund." : "Cancellation completed with partial refund."); // Shows refund outcome.
    }

    // -----------------------------
    // DATA SETUP AND REFRESH
    // -----------------------------

    private void loadSampleRooms() { // Loads starter room inventory into memory.
        rooms.add(new Room(101, "Standard", 1800.00, true)); // Adds a standard room.
        rooms.add(new Room(102, "Standard", 1800.00, true)); // Adds another standard room.
        rooms.add(new Room(201, "Deluxe", 2800.00, true)); // Adds a deluxe room.
        rooms.add(new Room(202, "Deluxe", 2800.00, true)); // Adds another deluxe room.
        rooms.add(new Room(301, "Suite", 4500.00, true)); // Adds a suite.
        rooms.add(new Room(302, "Suite", 5000.00, true)); // Adds a premium suite.
    }

    private void refreshRoomTable() { // Rebuilds the room table contents.
        roomTableModel.setRowCount(0); // Clears the old table rows.
        for (Room room : rooms) { // Loops over every room.
            roomTableModel.addRow(new Object[]{room.roomNumber, room.category, String.format("₱%,.2f", room.pricePerNight), room.available ? "Available" : "Booked"}); // Adds a row.
        }
    }

    private void refreshBookingTable() { // Rebuilds the active bookings table.
        bookingTableModel.setRowCount(0); // Clears old rows.
        for (Booking booking : bookings) { // Loops through all bookings.
            bookingTableModel.addRow(new Object[]{booking.reference, booking.customerName, booking.roomNumber, booking.category, booking.checkIn, booking.checkOut, booking.nights, String.format("₱%,.2f", booking.totalCost)}); // Adds booking row.
        }
    }

    private void refreshSearchTable(List<Booking> results) { // Rebuilds the search result table.
        searchTableModel.setRowCount(0); // Clears previous results.
        for (Booking booking : results) { // Loops through matched bookings.
            searchTableModel.addRow(new Object[]{booking.reference, booking.customerName, booking.roomNumber, booking.category, booking.checkIn, booking.checkOut, booking.nights, String.format("₱%,.2f", booking.totalCost)}); // Adds a result row.
        }
    }

    private void refreshRoomNumberCombo() { // Updates room numbers shown in the dropdown.
        roomNumberCombo.removeAllItems(); // Clears previous room numbers.
        String selectedCategory = (String) roomCategoryCombo.getSelectedItem(); // Reads currently selected category.
        for (Room room : rooms) { // Loops over rooms.
            if (room.available && room.category.equals(selectedCategory)) { // Only shows available rooms in the chosen category.
                roomNumberCombo.addItem(String.valueOf(room.roomNumber)); // Adds matching room number.
            }
        }

        if (roomNumberCombo.getItemCount() > 0) { // Checks if the dropdown has at least one item.
            roomNumberCombo.setSelectedIndex(0); // Picks the first item.
            updateSelectedRoomSummary(); // Updates the summary panel.
        } else { // If no rooms are available...
            availabilityValueLabel.setText("No available rooms"); // Warns the user.
            nightsValueLabel.setText("0"); // Resets nights.
            totalValueLabel.setText("₱0.00"); // Resets total.
        }
    }

    private void clearBookingForm() { // Clears all booking inputs.
        customerNameField.setText(""); // Clears name.
        checkInField.setText(""); // Clears check-in date.
        checkOutField.setText(""); // Clears check-out date.
        nightsValueLabel.setText("0"); // Resets nights value.
        totalValueLabel.setText("₱0.00"); // Resets total value.
        availabilityValueLabel.setText("Select a room"); // Resets availability text.
    }

    // -----------------------------
    // HELPERS
    // -----------------------------

    private JPanel createMainPanel() { // Creates a standard styled main panel.
        JPanel panel = new JPanel(new BorderLayout(12, 12)); // Uses BorderLayout with spacing.
        panel.setBackground(backgroundDark); // Sets the background color.
        panel.setBorder(new EmptyBorder(16, 16, 16, 16)); // Adds padding.
        return panel; // Returns the panel.
    }

    private JPanel createTitledPanel(String titleText, Color accent) { // Creates a stylized content box with a title.
        JPanel panel = new JPanel(); // Builds a new panel.
        panel.setBackground(panelDark); // Sets the fill color.
        panel.setBorder(BorderFactory.createCompoundBorder( // Creates a compound border.
                BorderFactory.createLineBorder(accent, 2), // Outer colorful border.
                new EmptyBorder(12, 12, 12, 12) // Inner spacing.
        ));
        return panel; // Returns the panel.
    }

    private JLabel createSectionTitle(String text) { // Creates a section heading label.
        JLabel label = new JLabel(text); // Creates the label.
        label.setForeground(neonSky); // Sets color.
        label.setFont(new Font("SansSerif", Font.BOLD, 20)); // Sets size and style.
        return label; // Returns the label.
    }

    private JLabel createSmallLabel(String text) { // Creates a compact field label.
        JLabel label = new JLabel(text); // Creates the label.
        label.setForeground(textWhite); // Uses white text.
        label.setFont(new Font("SansSerif", Font.BOLD, 13)); // Uses a bold small font.
        return label; // Returns the label.
    }

    private JTextField createTextField() { // Creates a styled text field.
        JTextField field = new JTextField(); // Instantiates the field.
        field.setBackground(new Color(32, 38, 62)); // Dark background.
        field.setForeground(textWhite); // White text.
        field.setCaretColor(textWhite); // Visible caret.
        field.setBorder(BorderFactory.createCompoundBorder( // Adds borders.
                BorderFactory.createLineBorder(new Color(80, 90, 130), 1), // Outer border.
                new EmptyBorder(8, 10, 8, 10) // Inner padding.
        ));
        return field; // Returns field.
    }

    private JComboBox<String> createComboBox(String[] items) { // Creates a styled combo box.
        JComboBox<String> comboBox = new JComboBox<>(items); // Populates combo box.
        comboBox.setBackground(new Color(32, 38, 62)); // Dark background.
        comboBox.setForeground(textWhite); // White text.
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 90, 130), 1)); // Border.
        comboBox.addActionListener(e -> { // Reacts when category changes.
            if (comboBox == roomCategoryCombo) { // Checks if it is the room category combo.
                refreshRoomNumberCombo(); // Updates room numbers based on selected category.
            } else if (comboBox == roomNumberCombo) { // Checks if it is the room number combo.
                updateSelectedRoomSummary(); // Updates the preview summary.
            }
        });
        return comboBox; // Returns combo box.
    }

    private JButton createNeonButton(String text, Color accent) { // Creates a stylish neon button.
        JButton button = new JButton(text); // Builds the button.
        button.setFocusPainted(false); // Removes focus outline.
        button.setBackground(accent); // Uses the accent color.
        button.setForeground(Color.WHITE); // Makes text white.
        button.setFont(new Font("SansSerif", Font.BOLD, 13)); // Sets font style.
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16)); // Adds padding.
        return button; // Returns the button.
    }

    private JTable createStyledTable(DefaultTableModel model) { // Creates a styled JTable.
        JTable table = new JTable(model); // Creates the table.
        table.setRowHeight(28); // Makes rows taller.
        table.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Sets readable font.
        table.setForeground(textWhite); // White text.
        table.setBackground(panelDark); // Dark table background.
        table.setGridColor(new Color(60, 70, 100)); // Soft grid lines.
        table.getTableHeader().setBackground(new Color(35, 45, 80)); // Dark header background.
        table.getTableHeader().setForeground(neonSky); // Neon header text.
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13)); // Bold header font.
        return table; // Returns the table.
    }

    private JLabel createSummaryLine(String text) { // Creates a summary label.
        JLabel label = new JLabel(text); // Creates the label.
        label.setForeground(new Color(200, 210, 235)); // Soft bright color.
        label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Sets font.
        return label; // Returns label.
    }

    private JLabel createSummaryValue(String text) { // Creates the value display label.
        JLabel label = new JLabel(text); // Creates the label.
        label.setForeground(textWhite); // White text.
        label.setFont(new Font("SansSerif", Font.BOLD, 16)); // Slightly larger font.
        return label; // Returns the label.
    }

    private JPanel wrapSummaryRow(JLabel left, JLabel right) { // Places a label and value on the same row.
        JPanel row = new JPanel(new BorderLayout()); // Creates a horizontal row.
        row.setOpaque(false); // Makes it transparent.
        row.add(left, BorderLayout.WEST); // Places label on left.
        row.add(right, BorderLayout.EAST); // Places value on right.
        return row; // Returns the row.
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) { // Adds a labeled form row.
        gbc.gridx = 0; // Label column.
        gbc.weightx = 0.35; // Smaller width for label.

        JLabel label = createSmallLabel(labelText); // Creates the label component.
        panel.add(label, gbc); // Adds label to panel.

        gbc.gridx = 1; // Field column.
        gbc.weightx = 0.65; // Wider width for the input field.
        panel.add(field, gbc); // Adds field component.

        gbc.gridy++; // Moves to the next row.
    }

    private Room getSelectedRoom() { // Finds the room from the current category and room number selection.
        String numberText = (String) roomNumberCombo.getSelectedItem(); // Reads selected room number.
        if (numberText == null || numberText.trim().isEmpty()) { // Checks for empty selection.
            return null; // Returns no room.
        }
        int roomNumber = Integer.parseInt(numberText); // Converts text to a number.
        return findRoomByNumber(roomNumber); // Finds matching room.
    }

    private Room findRoomByNumber(int roomNumber) { // Searches the room list by room number.
        for (Room room : rooms) { // Loops through rooms.
            if (room.roomNumber == roomNumber) { // Checks for match.
                return room; // Returns the found room.
            }
        }
        return null; // Returns null when not found.
    }

    private void updateSelectedRoomSummary() { // Updates the summary area when room selection changes.
        Room room = getSelectedRoom(); // Gets selected room.
        if (room == null) { // If no room selected...
            availabilityValueLabel.setText("Select a room"); // Display default text.
            return; // Stops execution.
        }
        availabilityValueLabel.setText(room.available ? "Available" : "Booked"); // Shows availability.
        if (checkInField.getText().trim().length() > 0 && checkOutField.getText().trim().length() > 0) { // Checks if dates exist.
            try { // Attempts to compute live pricing.
                LocalDate checkIn = parseDate(checkInField.getText().trim()); // Parses check-in date.
                LocalDate checkOut = parseDate(checkOutField.getText().trim()); // Parses check-out date.
                if (checkOut.isAfter(checkIn)) { // Ensures the dates are valid.
                    long nights = ChronoUnit.DAYS.between(checkIn, checkOut); // Computes nights.
                    double total = nights * room.pricePerNight; // Computes total.
                    nightsValueLabel.setText(String.valueOf(nights)); // Updates nights.
                    totalValueLabel.setText(String.format("₱%,.2f", total)); // Updates total.
                }
            } catch (Exception ignored) { // Silently ignores preview errors.
                // This is only a preview helper, so invalid input is not disruptive here.
            }
        }
    }

    private LocalDate parseDate(String input) { // Parses a string into LocalDate.
        return LocalDate.parse(input, DATE_FORMAT); // Converts text using the yyyy-MM-dd pattern.
    }

    private String generateReference() { // Generates a booking reference.
        return "HB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Returns short unique code.
    }

    private void setStatus(String message) { // Updates the status bar text.
        statusLabel.setText(message); // Sets the message.
    }

    private void setBorderAndBackground(JComponent component) { // Applies common background styling.
        component.setBackground(backgroundDark); // Sets the component background.
    }

    // -----------------------------
    // DATA CLASSES
    // -----------------------------

    private static class Room { // Represents one hotel room.
        int roomNumber; // Stores the room number.
        String category; // Stores Standard, Deluxe, or Suite.
        double pricePerNight; // Stores the nightly price.
        boolean available; // Tracks whether the room is open for booking.

        Room(int roomNumber, String category, double pricePerNight, boolean available) { // Constructor for a room.
            this.roomNumber = roomNumber; // Saves the room number.
            this.category = category; // Saves the room category.
            this.pricePerNight = pricePerNight; // Saves the nightly rate.
            this.available = available; // Saves availability.
        }
    }

    private static class Booking { // Represents a booking record.
        String reference; // Unique booking reference.
        String customerName; // Name of the guest.
        int roomNumber; // Room booked.
        String category; // Room category.
        LocalDate checkIn; // Check-in date.
        LocalDate checkOut; // Check-out date.
        long nights; // Number of nights.
        double totalCost; // Total booking cost.

        Booking(String reference, String customerName, int roomNumber, String category, LocalDate checkIn, LocalDate checkOut, long nights, double totalCost) { // Booking constructor.
            this.reference = reference; // Saves reference.
            this.customerName = customerName; // Saves customer name.
            this.roomNumber = roomNumber; // Saves room number.
            this.category = category; // Saves category.
            this.checkIn = checkIn; // Saves check-in date.
            this.checkOut = checkOut; // Saves check-out date.
            this.nights = nights; // Saves number of nights.
            this.totalCost = totalCost; // Saves cost.
        }
    }
}
```

## How to use this file

Compile it with Java 8 or newer, then run the class as a desktop Swing application.  
The code is written as one self-contained file so your group can study it quickly and split it later into separate classes if needed.

## Suggested next step

You can ask me to split this into a cleaner multi-file Java project structure with `Room`, `Booking`, `HotelFrame`, and `ThemeUtils` separated properly.

# Setup Guide for Java Swing (NetBeans or Eclipse)

This section explains how to set up the project, place the UI components, and connect the hotel reservation workflow step by step. The code above is already a complete single-file Swing UI, so the setup below is mainly for understanding how the interface is organized and how you would recreate or split it into tabs and components inside an IDE.

## 1) Project setup

### NetBeans
1. Open NetBeans.
2. Click **File > New Project**.
3. Choose **Java with Ant > Java Application**.
4. Name the project something like `HotelReservationSystem`.
5. Uncheck **Create Main Class** if you want to paste the provided file directly, or leave it checked if you want NetBeans to generate a starter class first.
6. Finish creating the project.
7. In the **Source Packages** area, right-click the package you want to use.
8. Choose **New > Java Class**.
9. Name the class `HotelReservationUI`.
10. Replace the generated code with the code from this markdown file.
11. Save the file.
12. Run the project with **Run Project** or press **F6**.

### Eclipse
1. Open Eclipse.
2. Click **File > New > Java Project**.
3. Enter a project name such as `HotelReservationSystem`.
4. Click **Finish**.
5. Inside `src`, right-click and choose **New > Class**.
6. Name the class `HotelReservationUI`.
7. Check the option to generate a `main` method only if you want Eclipse to create a starter method.
8. Paste the code into the class file.
9. Save the file.
10. Run the file by right-clicking the class and choosing **Run As > Java Application**.

## 2) Java version and libraries

This code uses standard Java and Swing, so no external library is required.

Use:
- **Java 8 or newer**
- `javax.swing.*` for UI components
- `java.awt.*` for layout and colors
- `java.time.*` for date handling
- `java.util.*` for lists and unique booking references

If your school computer uses an older Java version, the `LocalDate` and `ChronoUnit` parts will still work only if Java 8 or later is installed.

## 3) Overall UI structure

The interface is divided into three major parts:

### A. Header
The top section contains:
- The system title
- A subtitle
- A small academic badge

This area gives the application a professional presentation and helps users understand that the screen belongs to the Hotel Reservation module.

### B. Main tabbed content
The center of the window uses a `JTabbedPane`, which is the main navigation container.  
It includes these tabs:

- **Dashboard**
- **Rooms**
- **Bookings**
- **Search**
- **Cancellation**

Each tab is built by its own method so the code stays modular and easy to explain in a presentation.

### C. Status bar
The bottom section displays system messages such as:
- “Ready.”
- “Booking created successfully.”
- “Check-out must be after check-in.”
- “Booking reference not found.”

This is useful for validation and for showing whether an action succeeded or failed.

## 4) How each tab is set up

## Dashboard Tab

The dashboard is the first visual summary screen.

### Purpose
It shows quick statistics such as:
- Total rooms
- Active bookings
- Available rooms
- System status

### Components used
- `JPanel`
- `JLabel`
- Grid layout cards

### How it works
The dashboard uses small metric cards. Each card is created with a helper method so the display stays uniform.  
When the room or booking lists change, the counts should also refresh to reflect the latest data.

### Suggested improvement
If you later split the project into multiple files, create a method like `refreshDashboard()` to update the numbers every time a booking is added or cancelled.

## Rooms Tab

The Rooms tab displays the room inventory.

### Purpose
It lists all hotel rooms and their current availability.

### Components used
- `JTable`
- `DefaultTableModel`
- `JScrollPane`
- `JButton`

### Columns shown
- Room No.
- Category
- Price/Night
- Availability

### How it works
The room data is stored in an `ArrayList<Room>`.  
When the table is refreshed, every room from the list is inserted into the table model.

### Important logic
- The table is read-only.
- Rooms marked as booked show “Booked”.
- Rooms marked as open show “Available”.

### Suggested IDE setup
If you are recreating this in NetBeans GUI Builder or Eclipse WindowBuilder, drag:
- one `JPanel`
- one `JLabel`
- one `JTable` inside a `JScrollPane`
- one `JButton` for refreshing the list

Then connect the refresh button to the room table update method.

## Bookings Tab

This is the most important tab because it contains the reservation form.

### Purpose
It lets the user enter:
- Customer name
- Room category
- Room number
- Check-in date
- Check-out date

It also computes:
- Number of nights
- Estimated total cost

### Components used
- `JTextField` for customer name
- `JComboBox` for room category
- `JComboBox` for room number
- `JTextField` for check-in date
- `JTextField` for check-out date
- `JButton` for compute and book actions
- `JLabel` for live summary values

### How the form should be organized
Use a two-column layout:
- Left side: booking form inputs
- Right side: live booking summary

### Booking flow
1. The user enters the customer name.
2. The user chooses a room category.
3. The room number dropdown updates to show only available rooms in that category.
4. The user enters check-in and check-out dates using `yyyy-MM-dd`.
5. The user clicks **Compute Stay**.
6. The system calculates the number of nights and total cost.
7. The user clicks **Book Room**.
8. The room becomes unavailable.
9. The booking gets added to the active bookings table.

### Validation rules
- Customer name must not be empty.
- Check-in and check-out must be valid dates.
- Check-out must be later than check-in.
- Room must be available before booking.
- Duplicate booking of the same room is blocked.

### Suggested layout in GUI Builder
For the booking form, use:
- a `JPanel` with `GridBagLayout`
- labels aligned left
- fields aligned right
- a small horizontal panel for the two buttons

This gives a clean academic layout and makes the form easier to explain.

## Active Bookings Table

This table appears below the booking form.

### Purpose
It shows all confirmed reservations.

### Columns shown
- Ref No.
- Customer
- Room No.
- Category
- Check-In
- Check-Out
- Nights
- Total

### How it works
When a booking is created, it is added to the `bookings` list and shown in the table.  
When a booking is cancelled, it is removed from the list and the table refreshes.

### Why this matters
This gives your group a visible record of all reservations, which is important for both the project requirements and the grading rubric.

## Search Tab

This tab helps locate bookings quickly.

### Purpose
It searches bookings by:
- Customer name
- Room number

### Components used
- `JComboBox` for search mode
- `JTextField` for keyword input
- `JButton` for search action
- `JTable` for search results

### How it works
1. The user selects a search mode.
2. The user types a keyword.
3. The system scans all bookings.
4. Matching results are shown in the search table.

### Search behavior
- Name search uses partial matching.
- Room number search checks the room number text.
- The search is case-insensitive for names.

### Suggested improvement
You can add a “Clear Search” button later if you want to reset the table quickly.

## Cancellation Tab

This tab handles booking cancellation and refund display.

### Purpose
It allows the user to:
- enter a booking reference
- cancel the reservation
- see the computed refund

### Components used
- `JTextField` for booking reference
- `JButton` for cancel action
- `JLabel` for refund output
- `JTextArea` for policy explanation

### How it works
1. The user enters the booking reference.
2. The system finds the booking in the list.
3. The cancellation rule is checked.
4. The booking is removed.
5. The room is marked available again.
6. The refund amount is displayed.

### Refund rule
The file currently demonstrates the rule visually in the UI:
- full refund if cancelled at least 24 hours before check-in
- partial refund otherwise

### Important note for class presentation
Because this is a UI-focused version, the refund rule is shown clearly in the interface and processed in a simple, understandable way for academic demonstration.

## 5) How the data works internally

The code uses two internal classes:

### `Room`
Stores:
- room number
- category
- price per night
- availability

### `Booking`
Stores:
- reference number
- customer name
- room number
- category
- check-in date
- check-out date
- number of nights
- total cost

These classes are inside the main file, which is fine for a school project prototype.  
Later, you can separate them into their own files if your instructor wants a more formal OOP structure.

## 6) Recommended file organization if you split the project later

If you want a cleaner multi-file setup, use this structure:

```text
src/
 └── hotelreservation/
     ├── HotelReservationUI.java
     ├── Room.java
     ├── Booking.java
     └── ThemeUtils.java
```

### What each file should contain
- `HotelReservationUI.java`  
  Main window, tabs, tables, buttons, and event handlers

- `Room.java`  
  Room data model

- `Booking.java`  
  Booking data model

- `ThemeUtils.java`  
  Optional helper methods for colors, fonts, and reusable styling

## 7) How to recreate the UI in NetBeans GUI Builder

If your team prefers drag-and-drop design, here is the practical layout method.

### Step 1: Create a JFrame form
Create a new `JFrame Form` named `HotelReservationUI`.

### Step 2: Add a top header panel
Add a `JPanel` at the top with:
- `JLabel` for the title
- `JLabel` for the subtitle
- optional badge on the right

Set the background to a dark navy color and use neon-style text colors.

### Step 3: Add a `JTabbedPane`
Place it in the center of the frame and create five tabs:
- Dashboard
- Rooms
- Bookings
- Search
- Cancellation

### Step 4: Build each tab panel
Use separate panels for each tab so the UI stays organized.

### Step 5: Add a bottom status label
Place a small panel at the bottom of the frame and add a `JLabel` for system messages.

### Step 6: Wire the events
Attach action listeners to buttons:
- Compute Stay
- Book Room
- Search
- Cancel Booking
- Refresh Rooms

## 8) How to recreate the UI in Eclipse

If Eclipse WindowBuilder is available, the workflow is similar.

### Step 1: Create a Swing `JFrame`
Use `JFrame` as the main window class.

### Step 2: Add the main panels
Use:
- a header panel
- a tabbed pane panel
- a footer/status bar panel

### Step 3: Place components logically
Avoid putting all components in one huge panel.  
Use smaller panels inside each tab so the code and visual structure match.

### Step 4: Connect buttons to methods
For each button, add `ActionListener` code that calls the corresponding function:
- `computeStay()`
- `createBooking()`
- `performSearch()`
- `cancelBooking()`

## 9) Suggested component arrangement by tab

### Dashboard
- `JPanel`
- 4 statistic cards
- `JLabel` titles and values

### Rooms
- `JPanel`
- section title
- `JTable`
- refresh button

### Bookings
- left panel: booking form
- right panel: summary labels
- bottom area: active bookings table

### Search
- top search controls
- result table in center

### Cancellation
- cancel form panel
- refund policy text panel
- refund result label

## 10) Presentation tips for your defense

When presenting, explain it like this:

- The system uses Swing for the GUI layer.
- The room data is stored in an `ArrayList`.
- Booking creation uses input validation and date calculation.
- Search uses partial string matching and room number lookup.
- Cancellation updates availability and shows refund output.
- Each tab represents one major hotel function.

That explanation will make the project sound organized and academically solid.

## 11) Common errors to avoid

### Wrong date format
The date fields must follow:
`yyyy-MM-dd`

Example:
`2026-05-10`

### Empty fields
Do not allow blank customer names or blank booking references.

### Invalid date order
Check-out must always be after check-in.

### Double booking
Do not allow the same room to be reserved again while it is already booked.

### Forgetting to refresh tables
After booking or cancellation, refresh:
- room table
- booking table
- room dropdown
- search results if needed

## 12) Final workflow summary

The correct flow for the hotel module is:

1. Load sample rooms.
2. Show available rooms in the room tab.
3. Let the user enter booking information.
4. Compute nights and cost.
5. Save the booking.
6. Mark the room unavailable.
7. Show the booking in the active bookings table.
8. Allow searching by name or room number.
9. Allow cancellation using booking reference.
10. Restore room availability after cancellation.

This is the full setup logic your group can use for the Hotel Reservation System UI in Java Swing.

