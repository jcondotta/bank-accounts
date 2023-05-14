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

     private Long bankAccountId;
     private String iban;
     private List<AccountHolderDTO> accountHolders;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private LocalDateTime dateOfOpening;

     public BankAccountDTO() {}

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

     public void setBankAccountId(Long bankAccountId) {
          this.bankAccountId = bankAccountId;
     }

     public String getIban() {
          return iban;
     }

     public void setIban(String iban) {
          this.iban = iban;
     }

     public List<AccountHolderDTO> getAccountHolders() {
          return accountHolders;
     }

     public void setAccountHolders(List<AccountHolderDTO> accountHolders) {
          this.accountHolders = accountHolders;
     }

     public LocalDateTime getDateOfOpening() {
          return dateOfOpening;
     }

     public void setDateOfOpening(LocalDateTime dateOfOpening) {
          this.dateOfOpening = dateOfOpening;
     }
}