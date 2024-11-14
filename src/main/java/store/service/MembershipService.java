package store.service;

import store.enumerate.Membership;
import store.validator.InputValidator;
import store.view.InputView;

public class MembershipService {

    private final InputView inputView;
    private final InputValidator inputValidator;

    public MembershipService(InputView inputView, InputValidator inputValidator) {
        this.inputView = inputView;
        this.inputValidator = inputValidator;
    }

    public Membership checkMembership() {
        while(true) {
            try{
                return getMembership();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int applyMembership(int nonPromotedPrice, Membership checkMembership) {
        int saleAmount = (int) (nonPromotedPrice * checkMembership.getDiscountRate());
        if(saleAmount > 8000) {
            saleAmount = 8000;
        }
        return -saleAmount;
    }

    private Membership getMembership() {
        String getMembership = inputView.checkMembership();
        inputValidator.validateYesOrNoType(getMembership);
        Membership checkMembership = Membership.NON_MEMBERSHIP;
        if (getMembership.equals("Y")) {
            checkMembership =  Membership.MEMBERSHIP;
        }
        return checkMembership;
    }
}
