package com.blitzar.bank.accounts.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record AccountHolderRequest(@NotBlank String accountHolderName, @NotNull @Past LocalDate dateOfBirth) {
}