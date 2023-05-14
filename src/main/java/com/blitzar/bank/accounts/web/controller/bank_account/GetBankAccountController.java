package com.blitzar.bank.accounts.web.controller.bank_account;

import com.blitzar.bank.accounts.service.bank_account.GetBankAccountService;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

@Validated
@Controller(BankAccountAPIConstants.GET_BANK_ACCOUNT_V1_MAPPING)
public class GetBankAccountController {

    private final GetBankAccountService getBankAccountService;

    @Inject
    public GetBankAccountController(GetBankAccountService getBankAccountService) {
        this.getBankAccountService = getBankAccountService;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> byId(@PathVariable("bank-account-id") Long bankAccountId){
        BankAccountDTO bankAccountDTO = getBankAccountService.byId(bankAccountId);
        return HttpResponse.ok(bankAccountDTO);
    }
}