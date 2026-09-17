package com.tezzar.mkopo.light.loan.loanmanagement;

import com.tezzar.mkopo.light.exception.InvalidLoanStateException;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanEntityTransitionTest {

    @Test
    void transitionToMovesToValidNextState() {
        LoanEntity loan = new LoanEntity();
        loan.setLoanState(LoanState.PENDING);

        loan.transitionTo(LoanState.APPROVED);

        assertEquals(LoanState.APPROVED, loan.getLoanState());
    }

    @Test
    void transitionToThrowsForInvalidStateMove() {
        LoanEntity loan = new LoanEntity();
        loan.setLoanState(LoanState.CLOSED);

        assertThrows(InvalidLoanStateException.class, () -> loan.transitionTo(LoanState.ACTIVE));
    }
}
