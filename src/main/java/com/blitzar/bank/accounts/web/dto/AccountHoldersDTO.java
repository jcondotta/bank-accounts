package com.blitzar.bank.accounts.web.dto;

import com.blitzar.bank.accounts.domain.AccountHolder;
import io.micronaut.core.annotation.Introspected;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Introspected
public class AccountHoldersDTO {

    private List<AccountHolderDTO> accountHolders;

    public AccountHoldersDTO() {}

    public AccountHoldersDTO(List<AccountHolder> accountHolders){
        this.accountHolders = accountHolders
                .stream()
                .map(accountHolder -> new AccountHolderDTO(accountHolder))
                .collect(Collectors.toList());
    }

    public AccountHoldersDTO(AccountHolder... accountHolder){
        this(List.of(accountHolder));
    }

    public List<AccountHolderDTO> getAccountHolders() {
        return accountHolders;
    }

    public void setAccountHolders(List<AccountHolderDTO> accountHolders) {
        this.accountHolders = accountHolders;
    }
}
