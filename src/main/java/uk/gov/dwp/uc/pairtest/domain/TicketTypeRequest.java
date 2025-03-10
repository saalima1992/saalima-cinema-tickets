package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public final class TicketTypeRequest {

    private final int noOfTickets;
    private final TicketType type;

    public TicketTypeRequest(TicketType type, int noOfTickets) {
        if (noOfTickets < 0) throw new IllegalArgumentException("Ticket count cannot be negative");
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public TicketType getTicketType() {
        return type;
    }



}
