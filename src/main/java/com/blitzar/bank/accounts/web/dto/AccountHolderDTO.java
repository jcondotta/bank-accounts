package com.blitzar.bank.accounts.web.dto;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import java.time.LocalDate;

@Introspected
public class AccountHolderDTO {

    @JsonProperty
    private final long accountHolderId;

    @JsonProperty
    private final String accountHolderName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate dateOfBirth;

    @JsonCreator
    public AccountHolderDTO(long accountHolderId, String accountHolderName, LocalDate dateOfBirth) {
        this.accountHolderId = accountHolderId;
        this.accountHolderName = accountHolderName;
        this.dateOfBirth = dateOfBirth;
    }

    public AccountHolderDTO(AccountHolder accountHolder) {
        this(accountHolder.getAccountHolderId(), accountHolder.getAccountHolderName(), accountHolder.getDateOfBirth());
    }

    public long getAccountHolderId() {
        return accountHolderId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}
