package com.blitzar.bank.accounts.web.controller;

public interface BankAccountAPIConstants {

    String BASE_PATH_API_V1_MAPPING = "/api/v1/bank-accounts";
    String GET_BANK_ACCOUNT_V1_MAPPING = BASE_PATH_API_V1_MAPPING + "/{bank-account-id}";
    String ACCOUNT_HOLDER_API_V1_MAPPING = GET_BANK_ACCOUNT_V1_MAPPING + "/account-holders";
    String GET_ACCOUNT_HOLDER_V1_MAPPING = ACCOUNT_HOLDER_API_V1_MAPPING + "/{account-holder-id}";

}
