package com.ultimatesoftware.banking.account.common.events;

import java.util.UUID;

public class TransferCanceledEvent {
    private UUID transactionId;

    public TransferCanceledEvent(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
