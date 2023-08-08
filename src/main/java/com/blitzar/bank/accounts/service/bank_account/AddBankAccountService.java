package com.blitzar.bank.accounts.service.bank_account;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import jakarta.inject.Inject;
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

    private final AmazonSNS amazonSNS;

    @Inject
    public AddBankAccountService(BankAccountRepository repository, Clock currentInstant, Validator validator, AmazonSNS amazonSNS) {
        this.repository = repository;
        this.currentInstant = currentInstant;
        this.validator = validator;
        this.amazonSNS = amazonSNS;
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

//        amazonSNS.subscribe(new SubscribeRequest())publish(new PublishRequest("arn:aws:sns:eu-west-3:470315484552:qualquer", "Se Chegar, Funcionou"));
        return bankAccount;
    }
}