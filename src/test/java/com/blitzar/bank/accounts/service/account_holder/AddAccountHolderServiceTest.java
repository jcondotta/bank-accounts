package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.repository.AccountHolderRepository;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.service.account_holder.AddAccountHolderService;
import com.blitzar.bank.accounts.service.account_holder.AccountHolderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAccountHolderServiceTest {

    private AddAccountHolderService addAccountHolderService;

    @Mock
    private AccountHolderRepository accountHolderRepositoryMock;

    @Mock
    private BankAccountRepository bankAccountRepositoryMock;

    @BeforeEach
    public void beforeEach(){
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        addAccountHolderService = new AddAccountHolderService(accountHolderRepositoryMock, bankAccountRepositoryMock, validator);
    }

    @Test
    public void givenValidRequest_whenAddAccountHolder_thenSaveAccountHolder(){
        BankAccount bankAccount = mock(BankAccount.class);
        when(bankAccountRepositoryMock.findById(any())).thenReturn(Optional.of(bankAccount));

        var accountHolder = new AccountHolderRequest("Jefferson Condotta", LocalDate.of(1988, Month.JUNE, 20));

        addAccountHolderService.addAccountHolderToBankAccount(20L, accountHolder);

        verify(bankAccountRepositoryMock).findById(any());
        verify(accountHolderRepositoryMock).save(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddAccountHolder_thenThrowException(String invalidAccountHolderName){
        var accountHolderRequest = new AccountHolderRequest(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addAccountHolderService.addAccountHolderToBankAccount(20L, accountHolderRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be blank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolderName")
                ));

        verify(bankAccountRepositoryMock, never()).findById(any());
        verify(accountHolderRepositoryMock, never()).save(any());
    }

    @Test
    public void givenNullDateOfBirth_whenAddAccountHolder_thenThrowException(){
        var accountHolderRequest = new AccountHolderRequest("Jefferson Condotta", null);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addAccountHolderService.addAccountHolderToBankAccount(20L, accountHolderRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be null"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("dateOfBirth")
                ));

        verify(bankAccountRepositoryMock, never()).findById(any());
        verify(accountHolderRepositoryMock, never()).save(any());
    }

    @Test
    public void givenFutureDateOfBirth_whenAddBankAccount_thenThrowException(){
        var accountHolderRequest = new AccountHolderRequest("Jefferson Condotta", LocalDate.now().plusDays(1));

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> addAccountHolderService.addAccountHolderToBankAccount(20L, accountHolderRequest));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must be a past date"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("dateOfBirth")
                ));

        verify(bankAccountRepositoryMock, never()).findById(any());
        verify(accountHolderRepositoryMock, never()).save(any());
    }
}
