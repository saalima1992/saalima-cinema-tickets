package uk.gov.dwp.uc.pairtest.domain;

public enum TicketType {
    ADULT(25, true),
    CHILD(15, true),
    INFANT(0, false);

    private final int price;
    private final boolean requiresSeat;

    TicketType(int price, boolean requiresSeat) {
        this.price = price;
        this.requiresSeat = requiresSeat;
    }

    public int getPrice() {
        return price;
    }

    public boolean requiresSeat() {
        return requiresSeat;
    }
}
