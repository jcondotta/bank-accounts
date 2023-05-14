package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.service.account_holder.AddAccountHolderService;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Controller(BankAccountAPIConstants.ACCOUNT_HOLDER_API_V1_MAPPING)
public class AddAccountHolderController {

    private static final Logger logger = LoggerFactory.getLogger(AddBankAccountService.class);

    private final AddAccountHolderService addAccountHolderService;

    @Inject
    public AddAccountHolderController(AddAccountHolderService addAccountHolderService) {
        this.addAccountHolderService = addAccountHolderService;
    }

    @Status(HttpStatus.CREATED)
    @Post(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> addBankAccount(@PathVariable("bank-account-id") Long bankAccountId, @Body AccountHolderRequest request){
        var accountHolder = addAccountHolderService.addAccountHolderToBankAccount(bankAccountId, request);
        return HttpResponse.created(accountHolder.getAccountHolderId());
    }
}