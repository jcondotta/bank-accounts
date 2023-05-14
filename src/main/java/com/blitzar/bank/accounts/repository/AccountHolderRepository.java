package com.blitzar.bank.accounts.repository;

import com.blitzar.bank.accounts.domain.AccountHolder;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Collection;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {

    @Query(value = "select a from AccountHolder a where a.bankAccount.bankAccountId = :bankAccountId")
    Collection<AccountHolder> findByBankAccountId(@Parameter("bankAccountId") Long bankAccountId);
}
