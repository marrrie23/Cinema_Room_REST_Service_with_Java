package cinema;

import java.util.*;

public class Cinema {
    private final int rows = 9;
    private final int columns = 9;

    // Track purchased seats using a unique "row-column" identifier
    private final Set<String> purchasedSeats = new HashSet<>();

    // Map tokens to purchased ticket information
    private final Map<String, Ticket> tokenToTicketMap = new HashMap<>();

    // Track the total income from ticket sales
    private int totalIncome = 0;

    // Password required to access stats
    private static final String MANAGER_PASSWORD = "super_secret";

    /**
     * Get a list of all available seats in the cinema.
     *
     * @return A list of available seats with row, column, and price information.
     */
    public List<Map<String, Object>> getAvailableSeats() {
        List<Map<String, Object>> seats = new ArrayList<>();

        // Iterate over each row and column to create seat entries
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column <= columns; column++) {
                // Create a map for each seat containing its row, column, and price
                Map<String, Object> seat = new HashMap<>();
                seat.put("row", row);
                seat.put("column", column);

                // Determine the price based on the row number
                seat.put("price", row <= 4 ? 10 : 8);

                // Only add available seats
                String seatId = row + "-" + column;
                if (!purchasedSeats.contains(seatId)) {
                    seats.add(seat);
                }
            }
        }
        return seats;
    }

    /**
     * Purchase a ticket for a specific seat.
     *
     * @param row    The row number of the seat.
     * @param column The column number of the seat.
     * @return A Map containing the token and ticket information, or null if the seat is already purchased.
     */
    public Map<String, Object> purchaseTicket(int row, int column) {
        // Check if the provided seat location is within valid bounds
        if (row < 1 || row > rows || column < 1 || column > columns) {
            return null;
        }

        // Create a unique identifier for the seat using the format "row-column"
        String seatId = row + "-" + column;

        // Check if the seat has already been purchased
        if (purchasedSeats.contains(seatId)) {
            return null;
        }

        // Mark the seat as purchased by adding it to the set
        purchasedSeats.add(seatId);

        // Determine the price based on the row number
        int price = row <= 4 ? 10 : 8;

        // Add the ticket price to the total income
        totalIncome += price;

        // Generate a unique token for the purchase
        String token = UUID.randomUUID().toString();

        // Create the ticket information
        Ticket ticket = new Ticket(row, column, price);

        // Map the token to the ticket information
        tokenToTicketMap.put(token, ticket);

        // Create a success response with the token and ticket details
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("token", token);
        successResponse.put("ticket", ticket);

        return successResponse;
    }

    /**
     * Return a purchased ticket using a token.
     *
     * @param token The token for the purchased ticket.
     * @return The Ticket object associated with the token, or null if the token is invalid.
     */
    public Ticket returnTicket(String token) {
        // Check if the token exists in the map
        if (!tokenToTicketMap.containsKey(token)) {
            return null;
        }

        // Retrieve the ticket information associated with the token
        Ticket ticket = tokenToTicketMap.remove(token);

        // Get row and column from the ticket information
        int row = ticket.getRow();
        int column = ticket.getColumn();

        // Create a unique identifier for the seat using the format "row-column"
        String seatId = row + "-" + column;

        // Mark the seat as available by removing it from the purchasedSeats set
        purchasedSeats.remove(seatId);

        // Subtract the ticket price from the total income
        totalIncome -= ticket.getPrice();

        return ticket;
    }

    /**
     * Get cinema statistics if the correct password is provided.
     *
     * @param password The password to access the statistics.
     * @return A Map containing the cinema statistics, or null if the password is incorrect.
     */
    public Map<String, Object> getStatistics(String password) {
        // Check if the password is correct
        if (!MANAGER_PASSWORD.equals(password)) {
            return null;
        }

        // Calculate the number of available and purchased seats
        int availableSeats = (rows * columns) - purchasedSeats.size();
        int purchasedTickets = purchasedSeats.size();

        // Create the statistics response
        Map<String, Object> statsResponse = new HashMap<>();
        statsResponse.put("income", totalIncome);
        statsResponse.put("available", availableSeats);
        statsResponse.put("purchased", purchasedTickets);

        return statsResponse;
    }
}
