package com.blitzar.bank.accounts.service.bank_account.request;

import com.blitzar.bank.accounts.service.account_holder.request.AccountHolderRequest;
import io.micronaut.core.annotation.Introspected;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Introspected
public class AddBankAccountRequest {

    @Valid
    @NotEmpty
    private List<AccountHolderRequest> accountHolders;

    public AddBankAccountRequest(List<AccountHolderRequest> accountHolders) {
        this.accountHolders = accountHolders;
    }

    public AddBankAccountRequest(AccountHolderRequest... accountHolder) {
        this(List.of(accountHolder));
    }

    public List<AccountHolderRequest> getAccountHolders() {
        return accountHolders;
    }
}
