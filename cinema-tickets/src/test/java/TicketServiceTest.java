import org.junit.Before;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.*;


public class TicketServiceTest {

    private TicketPaymentServiceImpl paymentServiceMock = mock(TicketPaymentServiceImpl.class);
    private SeatReservationService seatReservationServiceMock = mock(SeatReservationService.class);
    private TicketServiceImpl ticketPaymentService;
    private Long accountId;

    @Before
    public void setUp() {
        ticketPaymentService = new TicketServiceImpl(paymentServiceMock, seatReservationServiceMock);
        accountId = 1L;
    }

    @Test
    public void adultTicketPurchaseTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        doNothing().when(paymentServiceMock).makePayment(accountId, 40);

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult);

        verify(paymentServiceMock, times(1)).makePayment(accountId, 40);
    }

    @Test
    public void ticketPurchaseForAllTypesTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest ticketTypeChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        TicketTypeRequest ticketTypeInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        doNothing().when(paymentServiceMock).makePayment(accountId, 60);

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult, ticketTypeChild, ticketTypeInfant);

        verify(paymentServiceMock, times(1)).makePayment(accountId, 60);
    }


    @Test(expected = InvalidPurchaseException.class)
    public void invalidIdTicketPurchaseTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        Long accountId = 0L;

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void invalidTicketRequestOnlyChildTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        Long accountId = 0L;

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void invalidTicketRequestOnlyInfantTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        Long accountId = 0L;

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult);
    }

    @Test
    public void seatReservationTest() {
        TicketTypeRequest ticketTypeAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        doNothing().when(seatReservationServiceMock).reserveSeat(accountId, 4);

        ticketPaymentService.purchaseTickets(accountId, ticketTypeAdult);

        verify(seatReservationServiceMock, times(1)).reserveSeat(accountId, 4);
    }
}
