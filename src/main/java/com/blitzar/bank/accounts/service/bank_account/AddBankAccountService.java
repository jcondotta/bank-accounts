package com.blitzar.bank.accounts.service.bank_account;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.event.BankAccountCreatedTopicHandler;
import com.blitzar.bank.accounts.service.bank_account.request.AddBankAccountRequest;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
@Transactional
public class AddBankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(AddBankAccountService.class);

    private final BankAccountRepository repository;
    private final Clock currentInstant;
    private final Validator validator;

    private final BankAccountCreatedTopicHandler topicHandler;

    @Inject
    public AddBankAccountService(BankAccountRepository repository, Clock currentInstant, Validator validator, BankAccountCreatedTopicHandler topicHandler) {
        this.repository = repository;
        this.currentInstant = currentInstant;
        this.validator = validator;
        this.topicHandler = topicHandler;
    }

    public BankAccount addBankAccount(AddBankAccountRequest request){
        logger.info("Attempting to add a new bank account.");

        var constraintViolations = validator.validate(request);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        var bankAccount = new BankAccount();
        bankAccount.setIban(UUID.randomUUID().toString());
        bankAccount.setDateOfOpening(LocalDateTime.now(currentInstant));
        bankAccount.setAccountHolders(request.getAccountHolders().stream()
                .map(accountHolderRequest ->
                        new AccountHolder(bankAccount,
                                accountHolderRequest.getAccountHolderName(),
                                accountHolderRequest.getDateOfBirth(),
                                accountHolderRequest.getEmailAddress()))
                .collect(Collectors.toList()));

        repository.save(bankAccount);
        topicHandler.publishMessage(new BankAccountDTO(bankAccount));

        return bankAccount;
    }
}