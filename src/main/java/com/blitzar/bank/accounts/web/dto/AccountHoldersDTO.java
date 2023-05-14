package com.blitzar.bank.accounts.web.dto;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Introspected
public class AccountHoldersDTO {

    @JsonProperty
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<AccountHolderDTO> accountHolders;

    @JsonCreator
    public AccountHoldersDTO(List<AccountHolder> accountHolders){
        this.accountHolders = accountHolders
                .stream()
                .map(accountHolder -> new AccountHolderDTO(accountHolder))
                .collect(Collectors.toList());
    }

    public AccountHoldersDTO(AccountHolder... accountHolder){
        this(List.of(accountHolder));
    }

    public Collection<AccountHolderDTO> getAccountHolders() {
        return accountHolders;
    }

    public void setAccountHolders(List<AccountHolderDTO> accountHolders) {
        this.accountHolders = accountHolders;
    }
}
