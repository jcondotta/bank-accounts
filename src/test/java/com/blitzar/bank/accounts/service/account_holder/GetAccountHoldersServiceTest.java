package com.blitzar.bank.accounts.service.account_holder;

import com.blitzar.bank.accounts.domain.AccountHolder;
import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.web.dto.AccountHoldersDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountHoldersServiceTest {

    private GetAccountHolderService getAccountHolderService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    public void beforeEach(){
        getAccountHolderService = new GetAccountHolderService(bankAccountRepository);
    }

    @Test
    public void givenExistingBankAccountId_whenGetAccountHolders_thenReturnAllAccountHolders(){
        var accountHolder1 = mock(AccountHolder.class);
        var accountHolder2 = mock(AccountHolder.class);

        var bankAccount = new BankAccount();
        bankAccount.setAccountHolders(List.of(accountHolder1, accountHolder2));

        when(bankAccountRepository.findById(any()))
                .thenReturn(Optional.of(bankAccount));

        AccountHoldersDTO accountHoldersDTO = getAccountHolderService.findAllByBankAccountId(20L);
        assertThat(accountHoldersDTO).isNotNull();
        assertThat(accountHoldersDTO.getAccountHolders()).hasSize(2);
    }

    @Test
    public void givenNonExistingBankAccountId_whenGetAccountHolders_thenReturnEmpty(){
        when(bankAccountRepository.findById(any()))
                .thenReturn(Optional.empty());

        var bankAccountId = 20L;
        var exception = assertThrowsExactly(ResourceNotFoundException.class, () -> getAccountHolderService.findAllByBankAccountId(bankAccountId));
        assertThat(exception.getMessage()).isEqualTo("No bank account has been found with id: " + bankAccountId);
    }
}
