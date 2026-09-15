package com.tezzar.mkopo.light.loan.enums;

public enum LoanState {
    PENDING,
    APPROVED,
    ACTIVE,
    OVERDUE,
    CLOSED,
    CANCELLED,
    WRITTEN_OFF;

    public boolean canTransitionTo(LoanState next) {
        return switch (this) {
            case PENDING     -> next == APPROVED || next == CANCELLED;
            case APPROVED    -> next == ACTIVE   || next == CANCELLED;
            case ACTIVE      -> next == OVERDUE  || next == CLOSED;
            case OVERDUE     -> next == ACTIVE   || next == CLOSED || next == WRITTEN_OFF;
            case CLOSED, CANCELLED, WRITTEN_OFF -> false;
        };
    }
}
