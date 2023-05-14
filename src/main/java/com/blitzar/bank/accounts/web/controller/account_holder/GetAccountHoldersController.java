package com.blitzar.bank.accounts.web.controller.account_holder;

import com.blitzar.bank.accounts.service.account_holder.GetAccountHolderService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import com.blitzar.bank.accounts.web.dto.AccountHoldersDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

@Validated
@Controller(BankAccountAPIConstants.ACCOUNT_HOLDER_API_V1_MAPPING)
public class GetAccountHoldersController {

    private final GetAccountHolderService getAccountHolderService;

    @Inject
    public GetAccountHoldersController(GetAccountHolderService getAccountHolderService) {
        this.getAccountHolderService = getAccountHolderService;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> findAll(@PathVariable("bank-account-id") Long bankAccountId){
        AccountHoldersDTO accountHoldersDTO = getAccountHolderService.findAllByBankAccountId(bankAccountId);
        return HttpResponse.ok(accountHoldersDTO);
    }
}