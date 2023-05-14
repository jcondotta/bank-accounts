package com.blitzar.bank.accounts.web.controller.bank_account;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import com.blitzar.bank.accounts.web.controller.BankAccountAPIConstants;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Controller(BankAccountAPIConstants.BASE_PATH_API_V1_MAPPING)
public class AddBankAccountController {

    private static final Logger logger = LoggerFactory.getLogger(AddBankAccountService.class);

    private final AddBankAccountService addBankAccountService;

    @Inject
    public AddBankAccountController(AddBankAccountService addBankAccountService) {
        this.addBankAccountService = addBankAccountService;
    }

    @Status(HttpStatus.CREATED)
    @Post(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> addBankAccount(@Body AddBankAccountRequest request){
        System.out.println("Teste");
        logger.debug("TesteD");
        logger.info("TesteI");
        logger.error("TesteE");
        BankAccount bankAccount = addBankAccountService.addBankAccount(request);

        return HttpResponse.created(bankAccount.getBankAccountId());
    }
}