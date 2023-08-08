package com.blitzar.bank.accounts.web.dto;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;

import java.time.LocalDate;

@Introspected
public class AccountHolderDTO {

    private long accountHolderId;

    private String accountHolderName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String emailAddress;

    public AccountHolderDTO() {}

    public AccountHolderDTO(long accountHolderId, String accountHolderName, LocalDate dateOfBirth, String emailAddress) {
        this.accountHolderId = accountHolderId;
        this.accountHolderName = accountHolderName;
        this.dateOfBirth = dateOfBirth;
        this.emailAddress = emailAddress;
    }

    public AccountHolderDTO(AccountHolder accountHolder) {
        this(accountHolder.getAccountHolderId(), accountHolder.getAccountHolderName(), accountHolder.getDateOfBirth(), accountHolder.getEmailAddress());
    }

    public long getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(long accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
