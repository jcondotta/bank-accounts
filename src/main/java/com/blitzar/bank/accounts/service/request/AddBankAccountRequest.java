package com.blitzar.bank.accounts.service.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddBankAccountRequest(@Valid @NotEmpty List<AccountHolderRequest> accountHolders) {

    public AddBankAccountRequest(AccountHolderRequest accountHolderRequest) {
        this(List.of(accountHolderRequest));
    }
}