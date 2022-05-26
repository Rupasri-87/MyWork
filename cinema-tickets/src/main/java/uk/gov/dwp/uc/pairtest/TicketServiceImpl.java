package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class TicketServiceImpl implements TicketService {

    private TicketPaymentService ticketPaymentService;

    private SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {

        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalTicketAmt = 0;
        boolean isValidPurchase = true;
        List<TicketTypeRequest> ticketTypeRequestList = Arrays.asList(ticketTypeRequests);
        Long noOfTicketRequested = ticketTypeRequestList.stream().count();
        if (noOfTicketRequested <= 20 && accountId > 0L) {

            boolean isChildOrInfantTicketRequested = ticketTypeRequestList.stream().anyMatch(ticketReq -> ticketReq.getTicketType() == TicketTypeRequest.Type.CHILD || ticketReq.getTicketType() == TicketTypeRequest.Type.INFANT);
            if (isChildOrInfantTicketRequested)
                isValidPurchase = ticketTypeRequestList.stream().anyMatch(ticketReq -> ticketReq.getTicketType() == TicketTypeRequest.Type.ADULT);

            if (isValidPurchase) {
                for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests)
                    totalTicketAmt += calculateTicketAmount(ticketTypeRequest.getTicketType(), ticketTypeRequest.getNoOfTickets());
            } else {

                throw new InvalidPurchaseException();
            }
        } else {
            throw new InvalidPurchaseException();
        }
        ticketPaymentService.makePayment(accountId, totalTicketAmt);
        seatReservationService.reserveSeat(accountId,
                ticketTypeRequestList.stream().filter(ticketrequest -> ticketrequest.getTicketType() == TicketTypeRequest.Type.ADULT || ticketrequest.getTicketType() == TicketTypeRequest.Type.CHILD).map(tickets -> tickets.getNoOfTickets()).collect(Collectors.summingInt(Integer::intValue)));
    }

    private int calculateTicketAmount(TicketTypeRequest.Type type, int noOfTicekts) {
        int ticketAmt = 0;
        switch (type) {
            case ADULT:
                ticketAmt = noOfTicekts * 20;
                break;
            case CHILD:
                ticketAmt = noOfTicekts * 10;
                break;
        }
        return ticketAmt;

    }


}
