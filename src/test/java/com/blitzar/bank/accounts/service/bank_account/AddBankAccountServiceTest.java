package com.blitzar.bank.accounts.service.bank_account;

import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountService;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import com.blitzar.bank.accounts.service.bank_account.AddBankAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddBankAccountServiceTest {

    private AddBankAccountService addBankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepositoryMock;

    @BeforeEach
    public void beforeEach(){
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        addBankAccountService = new AddBankAccountService(bankAccountRepositoryMock, Clock.system(ZoneOffset.UTC), validator);
    }

    @Test
    public void givenValidRequest_whenAddBankAccount_thenSaveBankAccount(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.of(1988, Month.JUNE, 20));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        addBankAccountService.addBankAccount(addBankAccountRequest);
        verify(bankAccountRepositoryMock).save(any());
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenThrowException(){
        var addBankAccountRequest = new AddBankAccountRequest(new ArrayList<AccountHolderRequest>());

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addBankAccountService.addBankAccount(addBankAccountRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be empty"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders")
                ));

        verify(bankAccountRepositoryMock, never()).save(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenThrowException(String invalidAccountHolderName){
        var accountHolder = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addBankAccountService.addBankAccount(addBankAccountRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be blank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].accountHolderName")
                ));

        verify(bankAccountRepositoryMock, never()).save(any());
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenThrowException(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", null);
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addBankAccountService.addBankAccount(addBankAccountRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be null"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].dateOfBirth")
                ));

        verify(bankAccountRepositoryMock, never()).save(any());
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenThrowException(){
        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.now().plusDays(1));
        var addBankAccountRequest = new AddBankAccountRequest(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addBankAccountService.addBankAccount(addBankAccountRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must be a past date"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].dateOfBirth")
                ));

        verify(bankAccountRepositoryMock, never()).save(any());
    }
}
