package com.blitzar.bank.accounts.service.bank_account;

import com.blitzar.bank.accounts.domain.BankAccount;
import com.blitzar.bank.accounts.exception.ResourceNotFoundException;
import com.blitzar.bank.accounts.repository.BankAccountRepository;
import com.blitzar.bank.accounts.web.dto.BankAccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBankAccountServiceTest {

    private GetBankAccountService getBankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    public void beforeEach(){
        getBankAccountService = new GetBankAccountService(bankAccountRepository);
    }

    @Test
    public void givenExistingBankAccountId_whenGetBankAccount_thenReturnBankAccount(){
        var bankAccount = mock(BankAccount.class);

        when(bankAccountRepository.findById(any()))
                .thenReturn(Optional.of(bankAccount));

        BankAccountDTO bankAccountDTO = getBankAccountService.byId(20L);
        assertThat(bankAccountDTO).isNotNull();


    }

    @Test
    public void givenNonExistingBankAccountId_whenGetAccountHolders_thenReturnEmpty(){
        when(bankAccountRepository.findById(any()))
                .thenReturn(Optional.empty());

        var bankAccountId = 20L;
        var exception = assertThrowsExactly(ResourceNotFoundException.class, () -> getBankAccountService.byId(bankAccountId));
        assertThat(exception.getMessage()).isEqualTo("No bank account has been found with id: " + bankAccountId);
    }
}
