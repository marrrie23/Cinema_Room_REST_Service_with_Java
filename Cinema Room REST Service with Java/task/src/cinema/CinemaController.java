package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CinemaController {

    private final Cinema cinema = new Cinema();

    /**
     * Endpoint to get information about all available seats in the cinema.
     * The response includes the row number, column number, and price of each seat.
     *
     * @return A map containing the number of rows, columns, and a list of seats with prices.
     */
    @GetMapping("/seats")
    public Map<String, Object> getSeats() {
        // Get the list of available seats
        List<Map<String, Object>> seats = cinema.getAvailableSeats();

        // Prepare the response object containing rows, columns, and seat details
        Map<String, Object> response = new HashMap<>();
        response.put("rows", 9);
        response.put("columns", 9);
        response.put("seats", seats);

        // Return the response as a JSON object
        return response;
    }

    /**
     * Endpoint to purchase a ticket for a specific seat.
     * Generates a unique token for each purchase and returns it along with ticket information.
     *
     * @param seatRequest A map containing the row and column numbers of the desired seat.
     * @return A ResponseEntity containing the purchase result with token or an error message.
     */
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseTicket(@RequestBody Map<String, Integer> seatRequest) {
        int row = seatRequest.get("row");
        int column = seatRequest.get("column");

        // Attempt to purchase the ticket
        Map<String, Object> purchaseResult = cinema.purchaseTicket(row, column);

        if (purchaseResult == null) {
            // Create an error response indicating the seat is already purchased or out of bounds
            Map<String, Object> errorResponse = new HashMap<>();
            if (row < 1 || row > 9 || column < 1 || column > 9) {
                errorResponse.put("error", "The number of a row or a column is out of bounds!");
            } else {
                errorResponse.put("error", "The ticket has been already purchased!");
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Return the successful purchase response with HTTP status 200 (OK)
        return new ResponseEntity<>(purchaseResult, HttpStatus.OK);
    }

    /**
     * Endpoint to return a purchased ticket using a token.
     * Marks the seat as available and returns the ticket information.
     *
     * @param refundRequest A map containing the token for the purchased ticket.
     * @return A ResponseEntity containing the refunded ticket or an error message.
     */
    @PostMapping("/return")
    public ResponseEntity<Map<String, Object>> returnTicket(@RequestBody Map<String, String> refundRequest) {
        String token = refundRequest.get("token");

        // Attempt to return the ticket
        Ticket ticket = cinema.returnTicket(token);

        if (ticket == null) {
            // Create an error response indicating the token is invalid
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Wrong token!");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Create a success response with the ticket details
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("ticket", ticket);

        // Return the successful refund response with HTTP status 200 (OK)
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to get statistics about the cinema.
     * Requires a password parameter to access the data.
     *
     * @param password The password parameter to access the stats.
     * @return A ResponseEntity containing the statistics or an error message.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(@RequestParam(value = "password", required = false) String password) {
        // Get the cinema statistics with the provided password
        Map<String, Object> stats = cinema.getStatistics(password);

        if (stats == null) {
            // Create an error response indicating the password is wrong
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "The password is wrong!");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        // Return the statistics response with HTTP status 200 (OK)
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}
