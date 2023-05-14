package com.blitzar.bank.accounts.web.dto;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.micronaut.core.annotation.Introspected;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Introspected
public class BankAccountDTO {

     @JsonProperty
     private final Long bankAccountId;
     @JsonProperty
     private final String iban;
     @JsonProperty
     private final List<AccountHolderDTO> accountHolders;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private final LocalDateTime dateOfOpening;

     @JsonCreator
     public BankAccountDTO(Long bankAccountId, String iban, List<AccountHolderDTO> accountHolders, LocalDateTime dateOfOpening) {
          this.bankAccountId = bankAccountId;
          this.iban = iban;
          this.accountHolders = accountHolders;
          this.dateOfOpening = dateOfOpening;
     }

     public BankAccountDTO(BankAccount bankAccount) {
          this(
                  bankAccount.getBankAccountId(),
                  bankAccount.getIban(),
                  bankAccount.getAccountHolders()
                          .stream()
                          .map(accountHolder -> new AccountHolderDTO(accountHolder))
                          .collect(Collectors.toList()),
                  bankAccount.getDateOfOpening()
          );
     }

     public Long getBankAccountId() {
          return bankAccountId;
     }

     public String getIban() {
          return iban;
     }

     public List<AccountHolderDTO> getAccountHolders() {
          return accountHolders;
     }

     public LocalDateTime getDateOfOpening() {
          return dateOfOpening;
     }
}