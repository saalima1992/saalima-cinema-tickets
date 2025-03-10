import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {
    @Mock
    private TicketPaymentService paymentService;  // Mock Payment Service

    @Mock
    private SeatReservationService reservationService;  // Mock Seat Reservation Service

    @InjectMocks
    private TicketServiceImpl ticketService;  // Inject Mocks into TicketServiceImpl

    private static final Long VALID_ACCOUNT_ID = 1L;
    private static final Long INVALID_ACCOUNT_ID = 0L;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    // Test: Should make payment and reserve seats correctly
    @Test
    void shouldMakePaymentAndReserveSeatsWhenValidPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketType.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketType.CHILD, 1);

        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicket, childTicket);

        // Verify correct amount is paid (2 Adults * £25 + 1 Child * £15 = £65)
        verify(paymentService).makePayment(VALID_ACCOUNT_ID, 65);

        // Verify correct number of seats are reserved (2 Adults + 1 Child = 3 Seats)
        verify(reservationService).reserveSeat(VALID_ACCOUNT_ID, 3);
    }

    //  Test: Should throw exception for invalid account ID
    @Test
    void shouldThrowExceptionForInvalidAccountId() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketType.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(INVALID_ACCOUNT_ID, adultTicket));

        verifyNoInteractions(paymentService, reservationService);
    }

    // Test: Should throw exception when purchasing only child tickets
    @Test
    void shouldThrowExceptionWhenPurchasingChildTicketsWithoutAdult() {
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketType.CHILD, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(VALID_ACCOUNT_ID, childTicket));

        verifyNoInteractions(paymentService, reservationService);
    }

    // Test: Should throw exception when purchasing only infant tickets
    @Test
    void shouldThrowExceptionWhenPurchasingInfantTicketsWithoutAdult() {
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketType.INFANT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(VALID_ACCOUNT_ID, infantTicket));

        verifyNoInteractions(paymentService, reservationService);
    }

    // Test: Should not reserve seats for infants
    @Test
    void shouldNotReserveSeatsForInfants() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketType.ADULT, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketType.INFANT, 2);

        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicket, infantTicket);

        // Verify correct payment (1 Adult * £25 = £25)
        verify(paymentService).makePayment(VALID_ACCOUNT_ID, 25);

        // Verify only 1 seat reserved (Infants don’t get seats)
        verify(reservationService).reserveSeat(VALID_ACCOUNT_ID, 1);
    }

    // Test: Should throw exception if purchasing more than 25 tickets
    @Test
    void shouldThrowExceptionWhenPurchasingMoreThan25Tickets() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketType.ADULT, 26);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicket));

        verifyNoInteractions(paymentService, reservationService);
    }

    // Test: Should handle multiple ticket types correctly
    @Test
    void shouldCorrectlyCalculatePaymentAndSeatsForMixedTickets() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketType.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketType.CHILD, 2);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketType.INFANT, 1);

        ticketService.purchaseTickets(VALID_ACCOUNT_ID, adultTicket, childTicket, infantTicket);

        // Total payment: (2 Adults * £25) + (2 Children * £15) = £80
        verify(paymentService).makePayment(VALID_ACCOUNT_ID, 80);

        // Total seats reserved: 2 Adults + 2 Children = 4 (Infants don’t get seats)
        verify(reservationService).reserveSeat(VALID_ACCOUNT_ID, 4);
    }

    // Test: Should throw exception if no tickets are purchased
    @Test
    void shouldThrowExceptionWhenNoTicketsArePurchased() {
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(VALID_ACCOUNT_ID));

        verifyNoInteractions(paymentService, reservationService);
    }

}
