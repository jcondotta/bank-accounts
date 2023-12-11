package com.blitzar.bank.accounts.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Entity
@Table(name = "account_holder")
public class AccountHolder {

    @Id
    @Column(name = "account_holder_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountHolderId;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "email_address")
    private String emailAddress;

    @Transient
    private String identityDocumentKey = "service-card-management.pdf";

    @ManyToOne
    @JoinColumn(name="bank_account_id", nullable = false)
    private BankAccount bankAccount;

    public AccountHolder() {}

    public AccountHolder(@NotNull BankAccount bankAccount, @NotBlank String accountHolderName, @Past LocalDate dateOfBirth,
                         @NotNull String emailAddress) {
        this.bankAccount = bankAccount;
        this.accountHolderName = accountHolderName;
        this.dateOfBirth = dateOfBirth;
        this.emailAddress = emailAddress;
    }

    public Long getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(Long accountHolderId) {
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

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIdentityDocumentKey() {
        return identityDocumentKey;
    }

    public void setIdentityDocumentKey(String identityDocumentKey) {
        this.identityDocumentKey = identityDocumentKey;
    }
}
