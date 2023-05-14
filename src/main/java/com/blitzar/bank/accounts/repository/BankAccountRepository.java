package com.blitzar.bank.accounts.repository;

import com.blitzar.bank.accounts.domain.BankAccount;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
