package com.example.StudentFinanceTracker.Model;

public enum PaymentStatus {
    PAID_ON_TIME("Paid On Time"),
    PAID_LATE("Paid Late");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

