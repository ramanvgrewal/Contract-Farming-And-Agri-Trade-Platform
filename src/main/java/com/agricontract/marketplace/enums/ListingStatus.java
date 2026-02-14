package com.agricontract.marketplace.enums;

public enum ListingStatus {
    OPEN,       // open for bids
    LOCKED,     // first bid placed, timer running
    CLOSED,     // finished, winner decided
    CANCELLED   // canceled by farmer
}
