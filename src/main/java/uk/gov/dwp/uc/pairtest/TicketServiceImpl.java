package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {
    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validateAccount(accountId);
        var ticketCounts = getTicketCounts(ticketTypeRequests);
        validateTicketRules(ticketCounts);
        var totalAmount = calculateTotalAmount(ticketCounts);
        var totalSeats = calculateTotalSeats(ticketCounts);
        paymentService.makePayment(accountId, totalAmount);
        reservationService.reserveSeat(accountId, totalSeats);
    }

    private int calculateTotalSeats(Map<TicketType, Integer> ticketCounts) {
        return ticketCounts.entrySet().stream().
                filter(entry -> entry.getKey().requiresSeat()).
                mapToInt(Map.Entry::getValue).sum();
    }

    private int calculateTotalAmount(Map<TicketType, Integer> ticketCounts) {
        return ticketCounts.entrySet().stream().
                mapToInt(entry -> entry.getValue() * entry.getKey().getPrice()).sum();
    }

    private void validateTicketRules(Map<TicketType, Integer> ticketCounts) {
        var totalTickets = ticketCounts.values().stream().mapToInt(Integer::intValue).sum();
        var adultTickets = ticketCounts.getOrDefault(TicketType.ADULT, 0);
        if (totalTickets == 0)
            throw new InvalidPurchaseException("At least one ticket has to be purchased");
        if (totalTickets > 25)
            throw new InvalidPurchaseException("Cannot purchase more than 25 tickets");
        if (adultTickets == 0 && totalTickets > 0)
            throw new InvalidPurchaseException("Purchasing Child or Infant ticket requires " +
                    "at least one adult ticket");
    }

    private Map<TicketType, Integer> getTicketCounts(TicketTypeRequest[] ticketTypeRequests) {
        return Map.copyOf(Arrays.stream(ticketTypeRequests)
                .collect(Collectors.toMap(TicketTypeRequest::getTicketType,
                        TicketTypeRequest::getNoOfTickets, Integer::sum)));

    }

    private void validateAccount(Long accountId) {
        if (Optional.ofNullable(accountId).orElse(0L) <= 0) {
            throw new InvalidPurchaseException("Invalid account ID: " + accountId);
        }
    }

}
