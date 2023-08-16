package com.blitzar.bank.accounts.event;

import com.blitzar.bank.accounts.web.dto.BankAccountDTO;

//@NotificationClient
public interface BankAccountCreatedTopicProducer {

//    @Topic("bank-account-created-topic")
    String sendMessage(BankAccountDTO bankAccount);

}
