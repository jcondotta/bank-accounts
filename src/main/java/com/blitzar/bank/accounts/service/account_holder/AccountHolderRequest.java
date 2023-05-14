package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.AccountHolder;
import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Introspected
public class AccountHolderRequest{

    @NotBlank
    private String accountHolderName;

    @Past
    @NotNull
    private LocalDate dateOfBirth;

    public AccountHolderRequest(String accountHolderName, LocalDate dateOfBirth) {
        this.accountHolderName = accountHolderName;
        this.dateOfBirth = dateOfBirth;
    }

    public AccountHolderRequest(AccountHolder accountHolder) {
        this(accountHolder.getAccountHolderName(), accountHolder.getDateOfBirth());
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}