package com.blitzar.bank.accounts.web.controller;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.service.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.service.AddBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bank-accounts")
public class AddBankAccountController {

    private final AddBankAccountService addBankAccountService;

    @Autowired
    public AddBankAccountController(AddBankAccountService addBankAccountService) {
        this.addBankAccountService = addBankAccountService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBankAccount(@RequestBody AddBankAccountRequest request){
        BankAccount bankAccount = addBankAccountService.addBankAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(bankAccount);
    }
}